package org.humanjourney.core.water;
import com.google.gson.*;
import java.util.*;
import org.humanjourney.core.water.WaterPolicy.Quality;
import static org.humanjourney.core.water.WaterPolicy.*;
/** Additive schema-1 fields keep existing v0.3 balance packs compatible. */
public record WaterExtensions(Salt salt,Classification classification,Purifier purifier){
    public record Salt(int thirst,float hydration,float exhaustion,boolean nausea,int nauseaTicks,int amplifier){
        public Salt { if(thirst< -20||thirst>20||!Float.isFinite(hydration)||Math.abs(hydration)>20||!Float.isFinite(exhaustion)||exhaustion<0||exhaustion>1000||nauseaTicks<0||nauseaTicks>72000||amplifier<0||amplifier>4)throw new IllegalArgumentException("invalid salt drinking settings"); }
    }
    public record Classification(Set<String> saltTags,List<WaterGeography.Region> regions){
        public Classification {saltTags=Set.copyOf(saltTags);regions=List.copyOf(regions);if(saltTags.size()>64||regions.size()>128)throw new IllegalArgumentException("too many salt tags/regions");}
    }
    public record Purifier(boolean enabled,int cost,Set<Quality> sources){
        public Purifier {sources=Set.copyOf(sources);if(cost<1||cost>1000000||sources.isEmpty()||sources.contains(Quality.SALT))throw new IllegalArgumentException("invalid purifier cost/sources; salt cannot be desalinated");}
        public boolean accepts(Quality quality){return enabled&&quality!=null&&quality!=Quality.SALT&&sources.contains(quality);}
    }
    public static WaterExtensions defaults(){return new WaterExtensions(new Salt(0,0,12,true,160,0),new Classification(Set.of("minecraft:is_ocean"),List.of()),new Purifier(true,100,Set.of(Quality.NORMAL,Quality.DIRTY)));}
    public static WaterExtensions parse(JsonObject root){
        var d=defaults();JsonObject s=section(root,"salt"),n=section(s,"nausea"),c=section(root,"classification"),p=section(root,"purifier");
        keys(s,"thirst_restored","hydration_restored","additional_exhaustion","nausea");keys(n,"enabled","duration_ticks","amplifier");keys(c,"salt_biome_tags","regions");keys(p,"enabled","filter_cost_per_fill","allowed_source_types");
        Set<String> tags=c.has("salt_biome_tags")?strings(c.get("salt_biome_tags")):d.classification.saltTags;
        for(String tag:tags)id(tag);
        List<WaterGeography.Region> regions=new ArrayList<>();Set<String> ids=new HashSet<>();
        if(c.has("regions"))for(JsonElement e:array(c.get("regions"))){
            if(!e.isJsonObject())throw new IllegalArgumentException("region must be object");var r=e.getAsJsonObject();keys(r,"id","dimension","water_type","polygon");
            for(String required:List.of("id","dimension","water_type","polygon"))if(!r.has(required))throw new IllegalArgumentException("region missing "+required);
            String name=string(r,"id",""),dimension=string(r,"dimension","");id(dimension);if(!ids.add(name))throw new IllegalArgumentException("duplicate region id: "+name);
            List<WaterGeography.Point> points=new ArrayList<>();
            for(JsonElement v:array(r.get("polygon"))){var xy=array(v);if(xy.size()!=2)throw new IllegalArgumentException("polygon vertex must have X,Z");points.add(new WaterGeography.Point(coordinate(xy.get(0)),coordinate(xy.get(1))));}
            regions.add(new WaterGeography.Region(name,dimension,quality(string(r,"water_type","")),points));
        }
        Set<Quality> sources=d.purifier.sources;if(p.has("allowed_source_types")){sources=new HashSet<>();for(String q:strings(p.get("allowed_source_types")))sources.add(quality(q));}
        return new WaterExtensions(new Salt(integer(s,"thirst_restored",0),(float)number(s,"hydration_restored",0),(float)number(s,"additional_exhaustion",12),bool(n,"enabled",true),integer(n,"duration_ticks",160),integer(n,"amplifier",0)),new Classification(tags,regions),new Purifier(bool(p,"enabled",true),integer(p,"filter_cost_per_fill",100),sources));
    }
    private static void id(String s){if(!s.matches("[a-z0-9_.-]+:[a-z0-9_./-]+"))throw new IllegalArgumentException("invalid resource ID: "+s);}
    private static Quality quality(String s){try{return Quality.valueOf(s.toUpperCase(Locale.ROOT));}catch(Exception e){throw new IllegalArgumentException("unknown water type: "+s);}}
    private static JsonArray array(JsonElement e){if(!e.isJsonArray())throw new IllegalArgumentException("expected array");return e.getAsJsonArray();}
    private static Set<String> strings(JsonElement e){Set<String> result=new LinkedHashSet<>();for(JsonElement v:array(e)){if(!v.isJsonPrimitive()||!v.getAsJsonPrimitive().isString()||!result.add(v.getAsString()))throw new IllegalArgumentException("expected distinct string values");}return result;}
    private static double coordinate(JsonElement e){if(!e.isJsonPrimitive()||!e.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException("coordinate must be number");return e.getAsDouble();}
}
