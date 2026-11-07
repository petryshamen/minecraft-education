package org.humanjourney.core.npc.context;
import com.google.gson.*;
import org.humanjourney.core.npc.economy.GeoProfile;
import java.util.*;
import org.humanjourney.core.water.WaterGeography;
import org.humanjourney.core.water.WaterPolicy;
import static org.humanjourney.core.npc.context.ContextJson.*;
/** NPC polygons reuse only the existing pure geometry validator, never the water classifier. */
public record NpcGeography(List<Area> macros,List<Area> locals){
    public record Area(String id,String dimension,int priority,String parent,String label,Map<String,String> eraLabels,WaterGeography.Region shape,GeoProfile profile){
        public Area{eraLabels=Map.copyOf(eraLabels);}
        public String label(String era){return eraLabels.getOrDefault(era,label);}
    }
    public record Location(String macro,String local,String label){}
    public NpcGeography{macros=List.copyOf(macros);locals=List.copyOf(locals);}
    public static NpcGeography empty(){return new NpcGeography(List.of(),List.of());}
    public static NpcGeography parse(List<JsonElement> resources,Set<String> eras){return parse(resources,eras,GeoProfile.vocabulary(null));}
    public static NpcGeography parse(List<JsonElement> resources,Set<String> eras,Map<String,Set<String>> vocabulary){
        List<Area> macros=new ArrayList<>(),locals=new ArrayList<>();Set<String> macroIds=new HashSet<>(),localIds=new HashSet<>();
        for(var e:resources){var root=object(e,"NPC geography");keys(root,"schema_version","macro_regions","local_areas");schema(root);
            parseAreas(root,"macro_regions",false,macros,macroIds,eras,vocabulary);parseAreas(root,"local_areas",true,locals,localIds,eras,vocabulary);
        }
        if(macros.size()>512||locals.size()>4096)throw new IllegalArgumentException("at most 512 macro regions and 4096 local areas supported");
        for(var local:locals)if(!local.parent().equals("none")){
            var parent=macros.stream().filter(a->a.id().equals(local.parent())).findFirst().orElseThrow(()->new IllegalArgumentException("unknown macro_region for "+local.id()));
            if(!parent.dimension().equals(local.dimension()))throw new IllegalArgumentException("local area/parent dimension mismatch: "+local.id());
        }return new NpcGeography(macros,locals);
    }
    private static void parseAreas(JsonObject root,String key,boolean local,List<Area> out,Set<String> ids,Set<String> eras,Map<String,Set<String>> vocabulary){
        for(var e:array(root,key,0,local?4096:512)){
            var o=object(e,key);if(local)keys(o,"id","dimension","priority","macro_region","display_label","era_labels","polygon","profile");else keys(o,"id","dimension","priority","polygon","profile");
            String id=logical(text(o,"id",null,64));if(!ids.add(id))throw new IllegalArgumentException("duplicate "+key+" ID: "+id);
            String dim=resource(text(o,"dimension","minecraft:overworld",256));List<WaterGeography.Point> points=new ArrayList<>();
            for(var p:array(o,"polygon",3,256)){if(!p.isJsonArray()||p.getAsJsonArray().size()!=2)throw new IllegalArgumentException("polygon vertex must be [X,Z]");var pair=p.getAsJsonArray();for(var n:pair)if(!n.isJsonPrimitive()||!n.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException("polygon coordinates must be numeric");points.add(new WaterGeography.Point(pair.get(0).getAsDouble(),pair.get(1).getAsDouble()));}
            Map<String,String> labels=new TreeMap<>();if(local)for(var label:section(o,"era_labels").entrySet()){
                if(!eras.contains(label.getKey()))throw new IllegalArgumentException("unknown era in location labels: "+label.getKey());labels.put(label.getKey(),text(section(o,"era_labels"),label.getKey(),"",48));
            }
            String parent=local?text(o,"macro_region","none",64):"none";if(!parent.equals("none"))logical(parent);
            out.add(new Area(id,dim,integer(o,"priority",0,-1000000,1000000),parent,local?text(o,"display_label","",48):"",labels,new WaterGeography.Region(id,dim,WaterPolicy.Quality.NORMAL,points),o.has("profile")?GeoProfile.parse(o.get("profile"),vocabulary):null));
        }
    }
    public GeoProfile profile(String macro,String local){if(!local.equals("none"))return locals.stream().filter(a->a.id().equals(local)).findFirst().map(a->a.profile()==null?GeoProfile.none():a.profile()).orElse(GeoProfile.none());return macros.stream().filter(a->a.id().equals(macro)).findFirst().map(a->a.profile()==null?GeoProfile.none():a.profile()).orElse(GeoProfile.none());}
    private Area match(List<Area> areas,String dimension,double x,double z){
        var matches=areas.stream().filter(a->a.dimension().equals(dimension)&&a.shape().contains(x,z)).sorted(Comparator.comparingInt(Area::priority).reversed()).toList();
        if(matches.isEmpty())return null;if(matches.size()>1&&matches.get(0).priority()==matches.get(1).priority())throw new IllegalArgumentException("Ambiguous NPC location: "+matches.get(0).id()+" and "+matches.get(1).id());return matches.getFirst();
    }
    public Location resolve(String dimension,double x,double z,String era){
        Area macro=match(macros,dimension,x,z),local=match(locals,dimension,x,z);
        if(local!=null&&!local.parent().equals("none")){
            if(macro!=null&&!macro.id().equals(local.parent()))throw new IllegalArgumentException("Conflicting NPC location: "+local.id()+" references "+local.parent()+" but position matches "+macro.id());
            if(macro==null)macro=macros.stream().filter(a->a.id().equals(local.parent())).findFirst().orElseThrow();
        }
        String label=local==null?"":local.label(era);return new Location(macro==null?"none":macro.id(),local==null?"none":local.id(),label.isBlank()?"none":label);
    }
    public List<Location> validationLocations(String era){
        List<Location> result=new ArrayList<>();result.add(new Location("none","none","none"));for(var macro:macros)result.add(new Location(macro.id(),"none","none"));
        for(var local:locals){String label=local.label(era);if(label.isBlank())label="none";
            if(!local.parent().equals("none"))result.add(new Location(local.parent(),local.id(),label));
            else{result.add(new Location("none",local.id(),label));for(var macro:macros)if(macro.dimension().equals(local.dimension()))result.add(new Location(macro.id(),local.id(),label));}
        }return List.copyOf(result);
    }
}
