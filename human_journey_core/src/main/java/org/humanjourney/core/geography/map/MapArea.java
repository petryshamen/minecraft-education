package org.humanjourney.core.geography.map;
import java.util.*;
import org.humanjourney.core.geography.DiscoveryLedger;
import org.humanjourney.core.water.*;
/** Transport has selected geography fields only; no profile, NPC, economy or progression data. */
public record MapArea(Kind kind,String id,String label,String dimension,int priority,String parent,WaterGeography.Region shape,Bounds bounds){
 public enum Kind{MACRO,LOCAL}
 public record Key(Kind kind,String id) implements Comparable<Key>{public int compareTo(Key other){int n=kind.compareTo(other.kind);return n==0?id.compareTo(other.id):n;}}
 public record Bounds(double minX,double minZ,double maxX,double maxZ){
  public Bounds{if(!Double.isFinite(minX)||!Double.isFinite(minZ)||!Double.isFinite(maxX)||!Double.isFinite(maxZ)||minX>maxX||minZ>maxZ)throw new IllegalArgumentException("invalid map bounds");}
  public boolean contains(double x,double z){return x>=minX&&x<=maxX&&z>=minZ&&z<=maxZ;}
  public boolean intersects(Bounds b){return minX<=b.maxX&&maxX>=b.minX&&minZ<=b.maxZ&&maxZ>=b.minZ;}
  public static Bounds of(List<WaterGeography.Point> points){return new Bounds(points.stream().mapToDouble(WaterGeography.Point::x).min().orElseThrow(),points.stream().mapToDouble(WaterGeography.Point::z).min().orElseThrow(),points.stream().mapToDouble(WaterGeography.Point::x).max().orElseThrow(),points.stream().mapToDouble(WaterGeography.Point::z).max().orElseThrow());}
  public Bounds union(Bounds b){return new Bounds(Math.min(minX,b.minX),Math.min(minZ,b.minZ),Math.max(maxX,b.maxX),Math.max(maxZ,b.maxZ));}
 }
 public MapArea{Objects.requireNonNull(kind);Objects.requireNonNull(shape);if(!DiscoveryLedger.validId(id)||label==null||label.isBlank()||label.length()>128||label.chars().anyMatch(Character::isISOControl)||dimension==null||dimension.length()>256||!dimension.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")||Math.abs((long)priority)>1000000||parent==null||!parent.isEmpty()&&!DiscoveryLedger.validId(parent)||kind==Kind.MACRO&&!parent.isEmpty())throw new IllegalArgumentException("invalid map area metadata");if(!shape.id().equals(id)||!shape.dimension().equals(dimension))throw new IllegalArgumentException("map shape identity mismatch");bounds=Bounds.of(shape.polygon());}
 public static MapArea decoded(Kind kind,String id,String label,String dimension,int priority,String parent,List<WaterGeography.Point> points){return new MapArea(kind,id,label,dimension,priority,parent,new WaterGeography.Region(id,dimension,WaterPolicy.Quality.NORMAL,points),null);}
 public Key key(){return new Key(kind,id);}
 public List<WaterGeography.Point> points(){return shape.polygon();}
 public boolean contains(double x,double z){return bounds.contains(x,z)&&shape.contains(x,z);}
}
