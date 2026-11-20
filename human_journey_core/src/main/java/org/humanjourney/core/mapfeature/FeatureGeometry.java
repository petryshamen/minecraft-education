package org.humanjourney.core.mapfeature;
import java.util.*;
import org.humanjourney.core.water.WaterGeography.Point;
import org.humanjourney.core.geography.map.MapArea.Bounds;
/** Immutable explicit world geometry. Parts belong to one feature; holes exclude their boundaries. */
public final class FeatureGeometry {
 public static final int MAX_RINGS=512,MAX_VERTICES=16384,MAX_CATALOG_VERTICES=1048576,MAX_CATALOG_RINGS=65536;
 public enum Kind{POLYGON,POLYLINE,MULTIPOLYGON}
 private record Edge(int index,Point a,Point b,Bounds bounds){}
 /** Immutable segment BVH used only at construction/validation; exact predicates are unchanged. */
 private static final class EdgeIndex {
  final Bounds bounds;final EdgeIndex left,right;final List<Edge> leaf;
  EdgeIndex(List<Edge> input){Bounds b=input.getFirst().bounds();for(var edge:input)b=b.union(edge.bounds());bounds=b;if(input.size()<=8){leaf=List.copyOf(input);left=right=null;}else{var sorted=new ArrayList<>(input);boolean x=b.maxX()-b.minX()>=b.maxZ()-b.minZ();sorted.sort(Comparator.comparingDouble(e->x?e.bounds().minX()+e.bounds().maxX():e.bounds().minZ()+e.bounds().maxZ()));int m=sorted.size()/2;left=new EdgeIndex(sorted.subList(0,m));right=new EdgeIndex(sorted.subList(m,sorted.size()));leaf=null;}}
  boolean touches(Edge edge,int sameRingSize){if(!bounds.intersects(edge.bounds()))return false;if(leaf==null)return left.touches(edge,sameRingSize)||right.touches(edge,sameRingSize);for(var other:leaf){int difference=Math.abs(edge.index()-other.index());if(sameRingSize>0&&(difference<=1||difference==sameRingSize-1))continue;if(edge.bounds().intersects(other.bounds())&&intersects(edge.a(),edge.b(),other.a(),other.b()))return true;}return false;}
 }
 public static final class Ring {
  private final List<Point> points;private final Bounds bounds;private final List<Edge> edges;private final EdgeIndex edgeIndex;
  public Ring(List<Point> input){points=validatePoints(input,3,MAX_VERTICES);bounds=Bounds.of(points);var built=new ArrayList<Edge>();double area=0;var origin=points.getFirst();for(int i=0;i<points.size();i++){var a=points.get(i);var b=points.get((i+1)%points.size());var c=points.get((i+2)%points.size());if(on(a,b,c)||on(b,c,a))throw new IllegalArgumentException("overlapping adjacent ring segments");area+=(a.x()-origin.x())*(b.z()-origin.z())-(b.x()-origin.x())*(a.z()-origin.z());built.add(new Edge(i,a,b,box(a,b)));}if(Math.abs(area)<1e-6)throw new IllegalArgumentException("degenerate ring");built.sort(Comparator.comparingDouble(e->e.bounds().minX()));edges=List.copyOf(built);edgeIndex=new EdgeIndex(edges);for(var edge:edges)if(edgeIndex.touches(edge,points.size()))throw new IllegalArgumentException("self-intersecting ring");}
  public List<Point> points(){return points;}public Bounds bounds(){return bounds;}
  public boolean contains(double x,double z){if(!bounds.contains(x,z))return false;boolean inside=false;var p=new Point(x,z);for(int i=0,j=points.size()-1;i<points.size();j=i++){var a=points.get(j);var b=points.get(i);if(on(a,b,p))return true;if((a.z()>z)!=(b.z()>z)&&x<(b.x()-a.x())*(z-a.z())/(b.z()-a.z())+a.x())inside=!inside;}return inside;}
  boolean crosses(Ring other){if(!bounds.intersects(other.bounds))return false;var smaller=edges.size()<=other.edges.size()?this:other;var larger=smaller==this?other:this;for(var edge:smaller.edges)if(larger.edgeIndex.touches(edge,0))return true;return false;}
  public double distanceSquared(double x,double z){double best=Double.POSITIVE_INFINITY;for(var e:edges)if(boxDistance(x,z,e.bounds())<=best)best=Math.min(best,segmentDistanceSquared(x,z,e.a(),e.b()));return best;}
  @Override public boolean equals(Object o){return o instanceof Ring ring&&points.equals(ring.points);}@Override public int hashCode(){return points.hashCode();}
 }
 public record Part(Ring outer,List<Ring> holes){
  public Part{Objects.requireNonNull(outer);holes=List.copyOf(holes);if(holes.size()+1>MAX_RINGS)throw new IllegalArgumentException("too many hole rings");int vertices=outer.points().size();for(int i=0;i<holes.size();i++){var hole=holes.get(i);vertices+=hole.points().size();if(vertices>MAX_VERTICES)throw new IllegalArgumentException("polygon exceeds vertex limit");var p=hole.points().getFirst();if(!outer.contains(p.x(),p.z())||outer.crosses(hole))throw new IllegalArgumentException("hole["+i+"] must be strictly inside outer without touching/crossing");for(int j=0;j<i;j++){var prior=holes.get(j);if(!prior.bounds().intersects(hole.bounds()))continue;var q=prior.points().getFirst();if(prior.crosses(hole)||prior.contains(p.x(),p.z())||hole.contains(q.x(),q.z()))throw new IllegalArgumentException("hole["+i+"] overlaps/touches/nests hole["+j+"]");}}}
  public Bounds bounds(){return outer.bounds();}public List<Ring> rings(){var list=new ArrayList<Ring>();list.add(outer);list.addAll(holes);return List.copyOf(list);}
  public boolean contains(double x,double z){return outer.contains(x,z)&&holes.stream().noneMatch(h->h.contains(x,z));}
  public double distanceSquared(double x,double z){double best=outer.distanceSquared(x,z);for(var h:holes)best=Math.min(best,h.distanceSquared(x,z));return best;}
  public boolean near(double x,double z,double radius){return contains(x,z)||distanceSquared(x,z)<=radius*radius;}
 }
 private final Kind kind;private final List<Point> points;private final List<Part> parts;private final Bounds bounds;private final int vertexCount,ringCount;
 /** Legacy simple constructor preserves schema-v1 validation and its 512-point cap. */
 public FeatureGeometry(Kind kind,List<Point> points,Bounds ignored){this(kind,kind==Kind.POLYLINE?validatePoints(points,2,512):List.of(),kind==Kind.POLYGON?List.of(new Part(new Ring(validatePoints(points,3,512)),List.of())):List.of());if(kind==Kind.MULTIPOLYGON)throw new IllegalArgumentException("multipart geometry requires explicit parts");}
 private FeatureGeometry(Kind kind,List<Point> line,List<Part> polygons){this.kind=Objects.requireNonNull(kind);if(kind==Kind.POLYLINE){points=validatePoints(line,2,MAX_VERTICES);parts=List.of();bounds=Bounds.of(points);vertexCount=points.size();ringCount=0;}else{parts=List.copyOf(polygons);if(parts.isEmpty()||parts.size()>MAX_RINGS||kind==Kind.POLYGON&&parts.size()!=1)throw new IllegalArgumentException("invalid polygon part count");int vertices=0,rings=0;Bounds b=parts.getFirst().bounds();for(var p:parts){b=b.union(p.bounds());rings+=1+p.holes().size();vertices+=p.outer().points().size();for(var h:p.holes())vertices+=h.points().size();}if(rings>MAX_RINGS||vertices>MAX_VERTICES)throw new IllegalArgumentException("feature exceeds 512 rings/16384 vertices");vertexCount=vertices;ringCount=rings;bounds=b;points=parts.getFirst().outer().points();}}
 public static FeatureGeometry polygon(Part part){return new FeatureGeometry(Kind.POLYGON,List.of(),List.of(part));}
 public static FeatureGeometry multipolygon(List<Part> parts){return new FeatureGeometry(Kind.MULTIPOLYGON,List.of(),parts);}
 public static FeatureGeometry polyline(List<Point> points){return new FeatureGeometry(Kind.POLYLINE,points,List.of());}
 public Kind kind(){return kind;}public List<Point> points(){return points;}public List<Part> parts(){return parts;}public Bounds bounds(){return bounds;}public int vertexCount(){return vertexCount;}public int ringCount(){return ringCount;}public int holeCount(){return ringCount-parts.size();}
 /** Legacy path methods are only meaningful for a simple polygon or open line. */
 public int segments(){return kind==Kind.POLYLINE?points.size()-1:points.size();}
 public boolean contains(double x,double z){return kind!=Kind.POLYLINE&&parts.stream().anyMatch(p->p.contains(x,z));}
 public double distanceSquared(double x,double z){double best=Double.POSITIVE_INFINITY;if(kind==Kind.POLYLINE){for(int i=0;i+1<points.size();i++)best=Math.min(best,segmentDistanceSquared(x,z,points.get(i),points.get(i+1)));}else for(var p:parts)best=Math.min(best,p.distanceSquared(x,z));return best;}
 public boolean near(double x,double z,double radius){return contains(x,z)||distanceSquared(x,z)<=radius*radius;}
 private static List<Point> validatePoints(List<Point> input,int minimum,int maximum){var points=List.copyOf(input);if(points.size()<minimum||points.size()>maximum||new HashSet<>(points.stream().map(p->new Point(p.x()==0?0:p.x(),p.z()==0?0:p.z())).toList()).size()!=points.size())throw new IllegalArgumentException("invalid ring/path point count or duplicate/zero-length vertex");return points;}
 private static Bounds box(Point a,Point b){return new Bounds(Math.min(a.x(),b.x()),Math.min(a.z(),b.z()),Math.max(a.x(),b.x()),Math.max(a.z(),b.z()));}
 private static double boxDistance(double x,double z,Bounds b){double dx=Math.max(0,Math.max(b.minX()-x,x-b.maxX())),dz=Math.max(0,Math.max(b.minZ()-z,z-b.maxZ()));return dx*dx+dz*dz;}
 public static double cross(Point a,Point b,Point p){return (b.x()-a.x())*(p.z()-a.z())-(b.z()-a.z())*(p.x()-a.x());}
 private static boolean on(Point a,Point b,Point p){return Math.abs(cross(a,b,p))<1e-7&&p.x()>=Math.min(a.x(),b.x())&&p.x()<=Math.max(a.x(),b.x())&&p.z()>=Math.min(a.z(),b.z())&&p.z()<=Math.max(a.z(),b.z());}
 private static boolean intersects(Point a,Point b,Point c,Point d){return on(a,b,c)||on(a,b,d)||on(c,d,a)||on(c,d,b)||(cross(a,b,c)*cross(a,b,d)<0&&cross(c,d,a)*cross(c,d,b)<0);}
 public static double segmentDistanceSquared(double x,double z,Point a,Point b){double dx=b.x()-a.x(),dz=b.z()-a.z(),length=dx*dx+dz*dz,t=length==0?0:Math.max(0,Math.min(1,((x-a.x())*dx+(z-a.z())*dz)/length));double ex=x-(a.x()+t*dx),ez=z-(a.z()+t*dz);return ex*ex+ez*ez;}
 @Override public boolean equals(Object o){return o instanceof FeatureGeometry g&&kind==g.kind&&points.equals(g.points)&&parts.equals(g.parts);}@Override public int hashCode(){return Objects.hash(kind,points,parts);}
}
