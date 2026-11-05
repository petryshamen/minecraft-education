package org.humanjourney.core.npc.context;
import com.mojang.logging.LogUtils;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import org.humanjourney.core.npc.easynpc.EasyNpc652Adapter;
import org.humanjourney.core.npc.NpcIdentity;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.nio.file.*;
/** Data-only integration test. Never constructs a Level or NPC, queries a world season, or changes progression. */
public final class NpcContextSmoke {
    public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
        public String getName(){return "Human Journey spawn context composition data checks";}
        public CompletableFuture<?> run(CachedOutput output){return event.getLookupProvider().thenAccept(lookup->{try{
            Path root=Path.of("../../example-npc-context-datapack/data/human_journey");
            var config=new NpcContextStore.Source("human_journey:core/npc_context","example",Files.readString(root.resolve("core/npc_context.json")));List<NpcContextStore.Source> roles=new ArrayList<>();
            for(String role:new String[]{"hunter","fisher"})roles.add(new NpcContextStore.Source("human_journey:"+role,"example",Files.readString(root.resolve("npc_archetype/"+role+".json"))));
            var data=NpcContextStore.prepare(config,List.of(),roles,NpcContextReloadListener::itemLimit);
            for(String role:new String[]{"hunter","fisher"})for(String season:new TreeSet<>(NpcArchetype.SEASONS)){
                var context=NpcContextRuntime.resolve(data,(objective,holder)->OptionalInt.of(0),"minecraft:overworld",0,0,new NpcSeasons.Result(season,"fixture")).context();
                var template=data.archetypes().get("human_journey:"+role).compose(context,NpcContextReloadListener::itemLimit);
                if(!template.name().equals(role.equals("hunter")?"Hunter":"Fisher")||!context.era().equals("era_0")||!context.localArea().equals("none"))throw new IllegalStateException("context/name mismatch");
                if(EasyNpc652Adapter.offers(template).isEmpty()||!EasyNpc652Adapter.dialogs(template,NpcIdentity.fresh()).hasDialog())throw new IllegalStateException("native adapter data missing");
            }
            Class.forName("org.humanjourney.core.npc.sereneseasons.SereneSeasons218Adapter");
            LogUtils.getLogger().info("[HJ Core] NPC_CONTEXT_SMOKE_OK: example era rules, two generic roles, all season/fallback layers, optional geography and native offer/dialogue data; no NPC/world created");
        }catch(Exception error){throw new IllegalStateException("NPC context data smoke failed",error);}});}
    }));}
    private NpcContextSmoke(){}
}
