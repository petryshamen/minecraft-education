package org.humanjourney.core.geography;
import java.util.*;
import org.humanjourney.core.mapfeature.FeatureGeometry;
import org.humanjourney.core.geography.map.MapArea.Bounds;
/** Immutable per-dimension/kind component BVH shared by authoritative geography and discovered map. */
public final class AreaSpatialIndex<T> {
 public record Entry<T>(T area,String dimension,String kind,int component,FeatureGeometry.Part part){public Bounds bounds(){return part.bounds();}public boolean contains(double x,double z){return part.contains(x,z);}}
 private record Group(String dimension,String kind){}
 private static final class Node<T>{final Bounds bounds;final Node<T> left,right;final List<Entry<T>> leaf;Node(List<Entry<T>> entries){Bounds b=entries.getFirst().bounds();for(var e:entries)b=b.union(e.bounds());bounds=b;if(entries.size()<=12){leaf=List.copyOf(entries);left=right=null;}else{var sorted=new ArrayList<>(entries);boolean x=b.maxX()-b.minX()>=b.maxZ()-b.minZ();sorted.sort(Comparator.comparingDouble(e->x?e.bounds().minX()+e.bounds().maxX():e.bounds().minZ()+e.bounds().maxZ()));int m=sorted.size()/2;left=new Node<>(sorted.subList(0,m));right=new Node<>(sorted.subList(m,sorted.size()));leaf=null;}}void query(Bounds b,List<Entry<T>> out){if(!bounds.intersects(b))return;if(leaf!=null){for(var e:leaf)if(e.bounds().intersects(b))out.add(e);}else{left.query(b,out);right.query(b,out);}}}
 private final Map<Group,Node<T>> roots;
 public AreaSpatialIndex(Collection<Entry<T>> entries){var groups=new HashMap<Group,List<Entry<T>>>();for(var e:entries)groups.computeIfAbsent(new Group(e.dimension(),e.kind()),k->new ArrayList<>()).add(e);var built=new HashMap<Group,Node<T>>();groups.forEach((k,v)->built.put(k,new Node<>(v)));roots=Map.copyOf(built);}
 public List<Entry<T>> query(String dimension,String kind,Bounds box){var out=new ArrayList<Entry<T>>();var root=roots.get(new Group(dimension,kind));if(root!=null)root.query(box,out);return out;}
 public List<Entry<T>> point(String dimension,String kind,double x,double z){if(!Double.isFinite(x)||!Double.isFinite(z)||Math.abs(x)>30000000||Math.abs(z)>30000000)return List.of();return query(dimension,kind,new Bounds(x,z,x,z));}
}
