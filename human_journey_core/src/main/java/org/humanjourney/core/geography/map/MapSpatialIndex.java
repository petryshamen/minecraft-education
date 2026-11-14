package org.humanjourney.core.geography.map;
import java.util.*;
/** Immutable per-dimension/kind BVH. Both viewport and hover visit bounding candidates only. */
public final class MapSpatialIndex {
 private record Group(String dimension,MapArea.Kind kind){}
 private final Map<Group,Node> roots=new HashMap<>();private final Map<MapArea.Key,MapArea> areas;
 private static final class Node {
  final MapArea.Bounds box;final Node left,right;final List<MapArea> leaf;
  Node(List<MapArea> entries){MapArea.Bounds b=entries.getFirst().bounds();for(var a:entries)b=b.union(a.bounds());box=b;if(entries.size()<=12){leaf=List.copyOf(entries);left=right=null;}else{boolean x=b.maxX()-b.minX()>=b.maxZ()-b.minZ();entries=new ArrayList<>(entries);entries.sort(Comparator.comparingDouble(a->x?(a.bounds().minX()+a.bounds().maxX()):(a.bounds().minZ()+a.bounds().maxZ())));int m=entries.size()/2;left=new Node(entries.subList(0,m));right=new Node(entries.subList(m,entries.size()));leaf=null;}}
  void query(MapArea.Bounds bounds,List<MapArea> out){if(!box.intersects(bounds))return;if(leaf!=null){for(var a:leaf)if(a.bounds().intersects(bounds))out.add(a);}else{left.query(bounds,out);right.query(bounds,out);}}
 }
 public record Hover(String local,String macro,int candidates,boolean ambiguous){public boolean visible(){return !local.isEmpty()||!macro.isEmpty();}public String primary(){return local.isEmpty()?macro:local;}public String secondary(){return local.isEmpty()?"":macro;}}
 public MapSpatialIndex(Map<MapArea.Key,MapArea> areas){this.areas=Map.copyOf(areas);var groups=new HashMap<Group,List<MapArea>>();for(var a:areas.values())groups.computeIfAbsent(new Group(a.dimension(),a.kind()),k->new ArrayList<>()).add(a);groups.forEach((g,list)->roots.put(g,new Node(list)));}
 public List<MapArea> query(String dimension,MapArea.Kind kind,MapArea.Bounds bounds){var out=new ArrayList<MapArea>();var root=roots.get(new Group(dimension,kind));if(root!=null)root.query(bounds,out);return out;}
 private static MapArea best(List<MapArea> candidates,double x,double z){MapArea best=null;boolean tie=false;for(var a:candidates)if(a.contains(x,z)){if(best==null||a.priority()>best.priority()){best=a;tie=false;}else if(a.priority()==best.priority())tie=true;}if(tie)throw new IllegalArgumentException("ambiguous discovered map geography");return best;}
 public Hover hover(String dimension,double x,double z){if(!Double.isFinite(x)||!Double.isFinite(z)||Math.abs(x)>30000000||Math.abs(z)>30000000)return new Hover("","",0,false);var box=new MapArea.Bounds(x,z,x,z);var locals=query(dimension,MapArea.Kind.LOCAL,box);var macros=query(dimension,MapArea.Kind.MACRO,box);MapArea local=null,macro=null;boolean ambiguous=false;try{local=best(locals,x,z);}catch(IllegalArgumentException e){ambiguous=true;}if(local!=null&&!local.parent().isEmpty()){var parent=areas.get(new MapArea.Key(MapArea.Kind.MACRO,local.parent()));if(parent!=null&&parent.dimension().equals(dimension))macro=parent;}if(macro==null)try{macro=best(macros,x,z);}catch(IllegalArgumentException e){ambiguous=true;}return new Hover(local==null?"":local.label(),macro==null?"":macro.label(),locals.size()+macros.size(),ambiguous);}
}
