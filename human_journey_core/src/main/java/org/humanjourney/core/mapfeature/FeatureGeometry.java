package org.humanjourney.core.mapfeature;
import java.util.*;
import org.humanjourney.core.water.WaterGeography.Point;
import org.humanjourney.core.geography.map.MapArea.Bounds;
/** Exact world X/Z geometry; independent of geography hierarchy and water quality. */
public record FeatureGeometry(Kind kind,List<Point> points,Bounds bounds){
 public enum Kind{POLYGON,POLYLINE}
 public FeatureGeometry{Objects.requireNonNull(kind);points=List.copyOf(points);int min=kind==Kind.POLYGON?3:2;if(points.size()<min||points.size()>512||new HashSet<>(points.stream().map(p->new Point(p.x()==0?0:p.x(),p.z()==0?0:p.z())).toList()).size()!=points.size())throw new IllegalArgumentException("invalid feature point count/duplicate vertices");for(int i=1;i<points.size();i++)if(points.get(i-1).x()==points.get(i).x()&&points.get(i-1).z()==points.get(i).z())throw new IllegalArgumentException("zero-length feature segment");bounds=Bounds.of(points);if(kind==Kind.POLYGON){double area=0;for(int i=0;i<points.size();i++){var a=points.get(i);var b=points.get((i+1)%points.size());area+=a.x()*b.z()-b.x()*a.z();}if(Math.abs(area)<1e-6)throw new IllegalArgumentException("degenerate feature polygon");for(int i=0;i<points.size();i++)for(int j=i+1;j<points.size();j++){if(j==i+1||(i==0&&j==points.size()-1))continue;if(intersects(points.get(i),points.get((i+1)%points.size()),points.get(j),points.get((j+1)%points.size())))throw new IllegalArgumentException("self-intersecting feature polygon");}}}
 public int segments(){return kind==Kind.POLYGON?points.size():points.size()-1;}
 public static double cross(Point a,Point b,Point p){return (b.x()-a.x())*(p.z()-a.z())-(b.z()-a.z())*(p.x()-a.x());}
 private static boolean on(Point a,Point b,Point p){return Math.abs(cross(a,b,p))<1e-7&&p.x()>=Math.min(a.x(),b.x())&&p.x()<=Math.max(a.x(),b.x())&&p.z()>=Math.min(a.z(),b.z())&&p.z()<=Math.max(a.z(),b.z());}
 private static boolean intersects(Point a,Point b,Point c,Point d){return on(a,b,c)||on(a,b,d)||on(c,d,a)||on(c,d,b)||(cross(a,b,c)*cross(a,b,d)<0&&cross(c,d,a)*cross(c,d,b)<0);}
 public boolean contains(double x,double z){if(kind!=Kind.POLYGON||!bounds.contains(x,z))return false;boolean inside=false;var p=new Point(x,z);for(int i=0,j=points.size()-1;i<points.size();j=i++){var a=points.get(j);var b=points.get(i);if(on(a,b,p))return true;if((a.z()>z)!=(b.z()>z)&&x<(b.x()-a.x())*(z-a.z())/(b.z()-a.z())+a.x())inside=!inside;}return inside;}
 public static double segmentDistanceSquared(double x,double z,Point a,Point b){double dx=b.x()-a.x(),dz=b.z()-a.z(),length=dx*dx+dz*dz,t=length==0?0:Math.max(0,Math.min(1,((x-a.x())*dx+(z-a.z())*dz)/length));double ex=x-(a.x()+t*dx),ez=z-(a.z()+t*dz);return ex*ex+ez*ez;}
 public double distanceSquared(double x,double z){double result=Double.POSITIVE_INFINITY;for(int i=0;i<segments();i++)result=Math.min(result,segmentDistanceSquared(x,z,points.get(i),points.get((i+1)%points.size())));return result;}
 public boolean near(double x,double z,double radius){return contains(x,z)||distanceSquared(x,z)<=radius*radius;}
}
