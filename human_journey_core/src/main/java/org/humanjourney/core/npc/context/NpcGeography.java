package org.humanjourney.core.npc.context;
import com.google.gson.*;
import org.humanjourney.core.npc.economy.GeoProfile;
import java.util.*;
import org.humanjourney.core.water.WaterGeography;
import org.humanjourney.core.water.WaterPolicy;
import static org.humanjourney.core.npc.context.ContextJson.*;
/** NPC polygons reuse only the existing pure geometry validator, never the water classifier. */
public final class NpcGeography {
    public record Area(String id,String dimension,int priority,String parent,String label,Map<String,String> eraLabels,org.humanjourney.core.mapfeature.FeatureGeometry shape,GeoProfile profile){
        public Area{eraLabels=Map.copyOf(eraLabels);org.humanjourney.core.geography.GeographyGeometry.check(shape);}
        public Area(String id,String dimension,int priority,String parent,String label,Map<String,String> labels,WaterGeography.Region shape,GeoProfile profile){this(id,dimension,priority,parent,label,labels,new org.humanjourney.core.mapfeature.FeatureGeometry(org.humanjourney.core.mapfeature.FeatureGeometry.Kind.POLYGON,shape.polygon(),null),profile);}
        public String label(String era){return eraLabels.getOrDefault(era,label);}
    }
    public record Location(String macro,String local,String label){}
    private final List<Area> macros,locals;private final Map<String,Area> macroIds,localIds;private final org.humanjourney.core.geography.AreaSpatialIndex<Area> index;private final long indexBuildNanos;
    public List<Area> macros(){return macros;}public List<Area> locals(){return locals;}public Map<String,Area> macroIds(){return macroIds;}public Map<String,Area> localIds(){return localIds;}public long indexBuildNanos(){return indexBuildNanos;}
    public NpcGeography(List<Area> macros,List<Area> locals){this.macros=List.copyOf(macros);this.locals=List.copyOf(locals);if(macros.size()>512||locals.size()>4096)throw new IllegalArgumentException("at most512macros/4096locals");var m=new HashMap<String,Area>();var l=new HashMap<String,Area>();for(var a:macros)if(m.putIfAbsent(a.id(),a)!=null)throw new IllegalArgumentException("duplicate macro ID");for(var a:locals)if(l.putIfAbsent(a.id(),a)!=null)throw new IllegalArgumentException("duplicate local ID");macroIds=Map.copyOf(m);localIds=Map.copyOf(l);org.humanjourney.core.geography.GeographyGeometry.catalog(java.util.stream.Stream.concat(macros.stream(),locals.stream()).map(Area::shape).toList());for(var a:locals)if(!a.parent().equals("none")&&(!m.containsKey(a.parent())||!m.get(a.parent()).dimension().equals(a.dimension())))throw new IllegalArgumentException("invalid local macro parent: "+a.id());long start=System.nanoTime();var entries=new ArrayList<org.humanjourney.core.geography.AreaSpatialIndex.Entry<Area>>();for(String kind:List.of("MACRO","LOCAL"))for(var a:kind.equals("MACRO")?macros:locals)for(int i=0;i<a.shape().parts().size();i++)entries.add(new org.humanjourney.core.geography.AreaSpatialIndex.Entry<>(a,a.dimension(),kind,i,a.shape().parts().get(i)));index=new org.humanjourney.core.geography.AreaSpatialIndex<>(entries);indexBuildNanos=System.nanoTime()-start;}
    public static NpcGeography empty(){return new NpcGeography(List.of(),List.of());}
    public static NpcGeography parse(List<JsonElement> resources,Set<String> eras){return parse(resources,eras,GeoProfile.vocabulary(null));}
    public static NpcGeography parse(List<JsonElement> resources,Set<String> eras,Map<String,Set<String>> vocabulary){
        return parse(resources,eras,vocabulary,java.util.stream.IntStream.range(0,resources.size()).mapToObj(i->"geography resource["+i+"]").toList());
    }
    public static NpcGeography parse(List<JsonElement> resources,Set<String> eras,Map<String,Set<String>> vocabulary,List<String> sources){
        List<Area> macros=new ArrayList<>(),locals=new ArrayList<>();Set<String> macroIds=new HashSet<>(),localIds=new HashSet<>();int resourceIndex=0;long[] totals={0,0};
        for(var e:resources){String source=sources.get(resourceIndex++);try{var root=object(e,"NPC geography");keys(root,"schema_version","macro_regions","local_areas");int version=integer(root,"schema_version",1,1,2);
            parseAreas(root,"macro_regions",false,macros,macroIds,eras,vocabulary,version,totals);parseAreas(root,"local_areas",true,locals,localIds,eras,vocabulary,version,totals);
            if(macros.size()>512||locals.size()>4096)throw new IllegalArgumentException("area count exceeded");}catch(RuntimeException error){throw new IllegalArgumentException(source+": "+error.getMessage(),error);}
        }
        if(macros.size()>512||locals.size()>4096)throw new IllegalArgumentException("at most 512 macro regions and 4096 local areas supported");
        return new NpcGeography(macros,locals);
    }
    private static void parseAreas(JsonObject root,String key,boolean local,List<Area> out,Set<String> ids,Set<String> eras,Map<String,Set<String>> vocabulary,int version,long[] totals){
        for(var e:array(root,key,0,local?4096:512)){
            var o=object(e,key);if(local)keys(o,"id","dimension","priority","macro_region","display_label","era_labels","polygon","geometry","profile");else keys(o,"id","dimension","priority","polygon","geometry","profile","display_label","era_labels");
            String id=logical(text(o,"id",null,64));if(!ids.add(id))throw new IllegalArgumentException("duplicate "+key+" ID: "+id);
            String dim=resource(text(o,"dimension","minecraft:overworld",256));org.humanjourney.core.mapfeature.FeatureGeometry shape;
            try{if(version==1){if(o.has("geometry"))throw new IllegalArgumentException("v1 requires polygon, not geometry");var points=org.humanjourney.core.geography.GeographyGeometry.points(o.get("polygon"),3,256);new WaterGeography.Region(id,dim,WaterPolicy.Quality.NORMAL,points);shape=new org.humanjourney.core.mapfeature.FeatureGeometry(org.humanjourney.core.mapfeature.FeatureGeometry.Kind.POLYGON,points,null);}else{if(o.has("polygon"))throw new IllegalArgumentException("v2 requires geometry, not polygon");shape=org.humanjourney.core.geography.GeographyGeometry.parse(o.get("geometry"),id);}}catch(RuntimeException error){throw new IllegalArgumentException(id+": "+error.getMessage(),error);}
            totals[0]+=shape.vertexCount();totals[1]+=shape.ringCount();if(totals[0]>1179648||totals[1]>65536)throw new IllegalArgumentException(id+": aggregate geography geometry budget exceeded");
            Map<String,String> labels=new TreeMap<>();for(var label:section(o,"era_labels").entrySet()){
                if(!eras.contains(label.getKey()))throw new IllegalArgumentException("unknown era in location labels: "+label.getKey());labels.put(label.getKey(),text(section(o,"era_labels"),label.getKey(),"",48));
            }
            String parent=local?text(o,"macro_region","none",64):"none";if(!parent.equals("none"))logical(parent);
            out.add(new Area(id,dim,integer(o,"priority",0,-1000000,1000000),parent,text(o,"display_label","",48),labels,shape,o.has("profile")?GeoProfile.parse(o.get("profile"),vocabulary):null));
        }
    }
    public GeoProfile profile(String macro,String local){var area=!local.equals("none")?localIds.get(local):macroIds.get(macro);return area==null||area.profile()==null?GeoProfile.none():area.profile();}
    private Area match(String kind,String dimension,double x,double z){Area best=null;boolean tie=false;var seen=new HashSet<String>();for(var e:index.point(dimension,kind,x,z)){var area=e.area();if(!e.contains(x,z)||area.shape().parts().size()>1&&!seen.add(area.id()))continue;if(best==null||area.priority()>best.priority()){best=area;tie=false;}else if(area.priority()==best.priority())tie=true;}if(tie)throw new IllegalArgumentException("Ambiguous NPC location in "+kind);return best;}
    public int candidateCount(String dimension,double x,double z){return index.point(dimension,"MACRO",x,z).size()+index.point(dimension,"LOCAL",x,z).size();}
    public Location resolve(String dimension,double x,double z,String era){
        Area macro=match("MACRO",dimension,x,z),local=match("LOCAL",dimension,x,z);
        if(local!=null&&!local.parent().equals("none")){if(macro!=null&&!macro.id().equals(local.parent()))throw new IllegalArgumentException("Conflicting NPC location: "+local.id()+" references "+local.parent()+" but position matches "+macro.id());if(macro==null)macro=macroIds.get(local.parent());}
        String label=local==null?"":local.label(era);return new Location(macro==null?"none":macro.id(),local==null?"none":local.id(),label.isBlank()?"none":label);
    }
    @Override public boolean equals(Object value){return value instanceof NpcGeography other&&macros.equals(other.macros)&&locals.equals(other.locals);}
    @Override public int hashCode(){return Objects.hash(macros,locals);}
    public List<Location> validationLocations(String era){
        List<Location> result=new ArrayList<>();result.add(new Location("none","none","none"));for(var macro:macros)result.add(new Location(macro.id(),"none","none"));
        for(var local:locals){String label=local.label(era);if(label.isBlank())label="none";
            if(!local.parent().equals("none"))result.add(new Location(local.parent(),local.id(),label));
            else{result.add(new Location("none",local.id(),label));for(var macro:macros)if(macro.dimension().equals(local.dimension()))result.add(new Location(macro.id(),local.id(),label));}
        }return List.copyOf(result);
    }
}
