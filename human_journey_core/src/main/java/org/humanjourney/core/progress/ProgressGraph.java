package org.humanjourney.core.progress;

import com.google.gson.*;
import java.util.*;
import java.util.function.Predicate;

/** Server-owned authoring model. No Minecraft, client, progression mutation or optional-mod API. */
public record ProgressGraph(String title,List<Era> eras,List<Node> nodes,List<Edge> connections) {
 public static final int MAX_NODES=512,MAX_EDGES=2048,MAX_ERAS=32,MAX_JSON=1024*1024;
 public static final int NODE_WIDTH=156,NODE_HEIGHT=52;
 public enum Category {MAIN,SIDE,DISCOVERY}
 public enum EdgeKind {PREREQUISITE,VISUAL}
 public interface Scores {OptionalInt read(String objective,String holder);}
 public record Ref(String type,String objective,String holder,int min,int max) {
  public boolean test(Scores scores){return type.equals("always")||type.equals("score")&&value(scores).stream().anyMatch(v->v>=min&&v<=max);}
  public OptionalInt value(Scores scores){return type.equals("score")?scores.read(objective,holder):OptionalInt.empty();}
 }
 public record Point(double x,double y){}
 public record Era(String id,String title,int color,int background,double start,double end,int order,List<Ref> visibleWhen){public Era{visibleWhen=List.copyOf(visibleWhen);}}
 public record Node(String id,String era,String title,String description,String icon,double x,double y,Category category,Ref completion,List<String> requires,List<Ref> availableWhen,List<Ref> visibleWhen,boolean placeholder){public Node{requires=List.copyOf(requires);availableWhen=List.copyOf(availableWhen);visibleWhen=List.copyOf(visibleWhen);}}
 public record Edge(String from,String to,EdgeKind kind,List<Point> route){public Edge{route=List.copyOf(route);}}
 public ProgressGraph{eras=List.copyOf(eras);nodes=List.copyOf(nodes);connections=List.copyOf(connections);}
 public static ProgressGraph empty(){return new ProgressGraph("Civilization Progress",List.of(),List.of(),List.of());}
 public Map<String,Node> nodeMap(){var result=new LinkedHashMap<String,Node>();for(var n:nodes)result.put(n.id,n);return Collections.unmodifiableMap(result);}
 public Set<Ref> references(){var refs=new HashSet<Ref>();for(var e:eras)refs.addAll(e.visibleWhen);for(var n:nodes){refs.add(n.completion);refs.addAll(n.availableWhen);refs.addAll(n.visibleWhen);}return Set.copyOf(refs);}
 public static boolean all(List<Ref> refs,Scores scores){return refs.stream().allMatch(r->r.test(scores));}
 static void need(boolean valid,String message){if(!valid)throw new IllegalArgumentException(message);}
 static JsonObject object(JsonElement e,String context){need(e!=null&&e.isJsonObject(),context+" must be object");return e.getAsJsonObject();}
 static void keys(JsonObject o,String... allowed){var set=Set.of(allowed);for(String key:o.keySet())need(set.contains(key),"unknown field: "+key);}
 static String text(JsonObject o,String key,int max){var e=o.get(key);need(e!=null&&e.isJsonPrimitive()&&e.getAsJsonPrimitive().isString(),key+" must be text");String s=e.getAsString();need(!s.isBlank()&&s.length()<=max&&s.chars().noneMatch(c->c<32&&c!='\n'),"invalid "+key);return s;}
 static String id(JsonObject o,String key){String s=text(o,key,96);need(s.matches("[a-z0-9_.-]+(:[a-z0-9_./-]+)?"),"invalid "+key+": "+s);return s;}
 static double number(JsonObject o,String key){var e=o.get(key);need(e!=null&&e.isJsonPrimitive()&&e.getAsJsonPrimitive().isNumber(),key+" must be numeric");double d=e.getAsDouble();need(Double.isFinite(d)&&Math.abs(d)<=100000,key+" outside ±100000");return d;}
 static int integer(JsonObject o,String key){var e=o.get(key);need(e!=null&&e.isJsonPrimitive()&&e.getAsJsonPrimitive().isNumber(),key+" must be integer");try{return e.getAsBigDecimal().intValueExact();}catch(ArithmeticException ex){throw new IllegalArgumentException(key+" must be int32");}}
 static boolean flag(JsonObject o,String key){if(!o.has(key))return false;var e=o.get(key);need(e.isJsonPrimitive()&&e.getAsJsonPrimitive().isBoolean(),key+" must be boolean");return e.getAsBoolean();}
 static JsonArray array(JsonObject o,String key,int max,boolean required){if(!o.has(key)&&!required)return new JsonArray();var e=o.get(key);need(e!=null&&e.isJsonArray()&&e.getAsJsonArray().size()<=max,"invalid "+key+" count (limit "+max+")");return e.getAsJsonArray();}
 static int color(JsonObject o,String key){String s=text(o,key,7);need(s.matches("#[0-9a-fA-F]{6}"),key+" must be #RRGGBB");return Integer.parseInt(s.substring(1),16);}
 static Ref ref(JsonElement e){var o=object(e,"progress reference");String type=text(o,"type",16);if(type.equals("always")||type.equals("never")){keys(o,"type");return new Ref(type,"","",0,0);}need(type.equals("score"),"unsupported progress reference: "+type+"; use shared score/always/never");keys(o,"type","objective","holder","min","max");String objective=text(o,"objective",16),holder=text(o,"holder",40);need(objective.matches("[A-Za-z0-9_.-]+"),"invalid objective");need(holder.matches("#[A-Za-z0-9_.-]+"),"holder must be an explicit shared #fake_player (no selectors/player UUIDs)");int min=integer(o,"min"),max=integer(o,"max");need(min<=max,"reference min exceeds max");return new Ref(type,objective,holder,min,max);}
 static List<Ref> refs(JsonObject o,String key){var result=new ArrayList<Ref>();for(var e:array(o,key,16,false))result.add(ref(e));return List.copyOf(result);}
 public static ProgressGraph parse(JsonElement input,Predicate<String> itemExists){var o=object(input,"civilization graph");keys(o,"schema_version","title","eras","nodes","connections");need(integer(o,"schema_version")==1,"unsupported civilization graph schema_version");String title=text(o,"title",128);var eras=new ArrayList<Era>();var eraIds=new HashSet<String>();
  for(var e:array(o,"eras",MAX_ERAS,true)){var a=object(e,"Era");keys(a,"id","title","color","background","x_start","x_end","order","visible_when");String id=id(a,"id");need(eraIds.add(id),"duplicate Era "+id);double start=number(a,"x_start"),end=number(a,"x_end");need(start<end,"Era x_start must be less than x_end");eras.add(new Era(id,text(a,"title",128),color(a,"color"),color(a,"background"),start,end,integer(a,"order"),refs(a,"visible_when")));}
  eras.sort(Comparator.comparingInt(Era::order).thenComparing(Era::id));var nodes=new ArrayList<Node>();var ids=new HashSet<String>();
  for(var e:array(o,"nodes",MAX_NODES,true)){var a=object(e,"node");keys(a,"id","era","title","description","icon","x","y","category","completion","requires","available_when","visible_when","placeholder");String id=id(a,"id"),era=id(a,"era");need(ids.add(id),"duplicate node "+id);need(eraIds.contains(era),"unknown Era "+era+" in "+id);String icon=text(a,"icon",128);need(icon.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")&&itemExists.test(icon),"unknown icon item "+icon+" in "+id);var req=new ArrayList<String>();for(var r:array(a,"requires",32,false)){need(r.isJsonPrimitive()&&r.getAsJsonPrimitive().isString(),"requires must contain node IDs");need(!req.contains(r.getAsString()),"duplicate prerequisite");req.add(r.getAsString());}Category category;try{category=Category.valueOf(text(a,"category",16).toUpperCase(Locale.ROOT));}catch(IllegalArgumentException ex){throw new IllegalArgumentException("unknown node category in "+id);}var completion=ref(a.get("completion"));var availability=refs(a,"available_when");boolean placeholder=flag(a,"placeholder");need(!placeholder||completion.type.equals("never")&&availability.stream().anyMatch(r->r.type.equals("never")),"placeholder must have never completion and never availability");nodes.add(new Node(id,era,text(a,"title",128),text(a,"description",2048),icon,number(a,"x"),number(a,"y"),category,completion,req,availability,refs(a,"visible_when"),placeholder));}
  var edges=new ArrayList<Edge>();var edgeIds=new HashSet<String>();var nodeMap=new HashMap<String,Node>();for(var n:nodes)nodeMap.put(n.id,n);
  for(var n:nodes)for(String r:n.requires)need(nodeMap.containsKey(r)&&!r.equals(n.id),"unknown/self prerequisite "+r+" in "+n.id);
  for(var e:array(o,"connections",MAX_EDGES,true)){var a=object(e,"connection");keys(a,"from","to","kind","route");String from=id(a,"from"),to=id(a,"to");need(ids.contains(from)&&ids.contains(to)&&!from.equals(to),"unknown/self connection endpoint");EdgeKind kind;try{kind=EdgeKind.valueOf(text(a,"kind",20).toUpperCase(Locale.ROOT));}catch(IllegalArgumentException ex){throw new IllegalArgumentException("unknown connection kind");}need(edgeIds.add(from+"|"+to+"|"+kind),"duplicate connection");need(kind!=EdgeKind.PREREQUISITE||nodeMap.get(to).requires.contains(from),"prerequisite connection must also be explicitly declared in target.requires");var points=new ArrayList<Point>();for(var p:array(a,"route",16,false)){need(p.isJsonArray()&&p.getAsJsonArray().size()==2,"route point must be[X,Y]");var pair=p.getAsJsonArray();var point=new JsonObject();point.add("x",pair.get(0));point.add("y",pair.get(1));points.add(new Point(number(point,"x"),number(point,"y")));}edges.add(new Edge(from,to,kind,points));}
  for(var n:nodes)for(String r:n.requires)need(edges.stream().anyMatch(e->e.from.equals(r)&&e.to.equals(n.id)&&e.kind==EdgeKind.PREREQUISITE),"missing prerequisite connection "+r+" -> "+n.id);
  var adjacency=new HashMap<String,List<String>>();for(var n:nodes)adjacency.put(n.id,new ArrayList<>(n.requires));for(var e:edges)adjacency.get(e.to).add(e.from);var visiting=new HashSet<String>();var done=new HashSet<String>();for(String id:ids)acyclic(id,adjacency,visiting,done);
  var graph=new ProgressGraph(title,eras,nodes,edges);ProgressView.checkCapacity(graph);return graph;
 }
 private static void acyclic(String id,Map<String,List<String>> adjacency,Set<String> visiting,Set<String> done){if(done.contains(id))return;need(visiting.add(id),"graph dependency/connection cycle at "+id);for(String p:adjacency.get(id))acyclic(p,adjacency,visiting,done);visiting.remove(id);done.add(id);}
}
