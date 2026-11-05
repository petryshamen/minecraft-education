package org.humanjourney.core.npc.context;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import java.util.*;
import java.util.function.Supplier;
/** Optional dependency gate; no Serene Seasons type appears in this class's signatures or static fields. */
public final class NpcSeasons {
    public record Result(String season,String detail){}
    public static Result resolve(boolean installed,String version,Supplier<String> lookup){
        if(!installed)return new Result("none","Serene Seasons absent");if(!"21.8.0.0".equals(version))return new Result("none","unsupported Serene Seasons version: "+version);
        try{String season=lookup.get();if(season==null||!Set.of("spring","summer","autumn","winter").contains(season))return new Result("none","season state unavailable");return new Result(season,"Serene Seasons 21.8.0.0");}
        catch(RuntimeException|LinkageError error){return new Result("none","season API unavailable: "+error.getClass().getSimpleName());}
    }
    public static Result current(Level level){var mod=ModList.get().getModContainerById("sereneseasons");return resolve(mod.isPresent(),mod.map(m->m.getModInfo().getVersion().toString()).orElse(""),()->org.humanjourney.core.npc.sereneseasons.SereneSeasons218Adapter.season(level));}
    private NpcSeasons(){}
}
