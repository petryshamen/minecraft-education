package org.humanjourney.core.npc.easynpc;
import org.humanjourney.core.npc.*;
import de.markusbordihn.easynpc.data.action.*;
import de.markusbordihn.easynpc.data.condition.*;
import de.markusbordihn.easynpc.data.dialog.*;
import de.markusbordihn.easynpc.data.skin.SkinDataEntry;
import de.markusbordihn.easynpc.data.trading.*;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPCBase;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;
import net.minecraft.world.level.block.Blocks;
import java.util.*;

/** The only layer importing Easy NPC APIs. No preset import, custom renderer, ticking adapter or stock workaround. */
public final class EasyNpc652Adapter {
    public static String spawn(ServerPlayer player,NpcTemplate template,String name){
        NpcDependency.require();var level=player.level();var key=ResourceLocation.parse("easy_npc:humanoid");
        if(!BuiltInRegistries.ENTITY_TYPE.containsKey(key))throw new IllegalArgumentException("Easy NPC humanoid entity type is unavailable");
        var entity=BuiltInRegistries.ENTITY_TYPE.getValue(key).create(level,EntitySpawnReason.COMMAND);
        if(!(entity instanceof Mob mob)||!(entity instanceof EasyNPCBase<?> npc))throw new IllegalArgumentException("Easy NPC humanoid API is incompatible");
        var identity=NpcIdentity.fresh();mob.setUUID(identity.entity());BlockPos safe=findSafe(player,mob);
        if(safe==null){mob.discard();throw new IllegalArgumentException("no safe spawn position within four blocks; use open, solid ground");}
        try{
            mob.snapTo(safe.getX()+.5,safe.getY(),safe.getZ()+.5,player.getYRot()+180,0);
            mob.finalizeSpawn(level,level.getCurrentDifficultyAt(safe),EntitySpawnReason.COMMAND,null);
            mob.setUUID(identity.entity());npc.setPresetUUID(identity.preset());npc.setNPCOwnerUUID(player.getUUID());npc.setActionPermissionLevel(2);
            mob.setCustomName(Component.literal(template.displayName(name)));mob.setCustomNameVisible(true);mob.setInvulnerable(template.invulnerable());mob.setPersistenceRequired();mob.setNoAi(template.stationary());
            npc.setHomePosition(safe);npc.setSkinDataEntry(SkinDataEntry.createRemoteSkin(template.skinUrl()));npc.setDialogDataSet(dialogs(template,identity));
            var trading=new TradingDataSet();trading.setType(TradingType.ADVANCED);trading.setResetsEveryMin(template.resetMinutes());trading.setLastReset(System.currentTimeMillis());npc.setTradingDataSet(trading);npc.setTradingOffers(offers(template));
            // Only open dialogue on interaction. Trading is an explicit template button, avoiding two simultaneous screens.
            var interaction=new ActionDataSet();interaction.add(new ActionDataEntry(ActionDataType.OPEN_DEFAULT_DIALOG,identity.entity(),""));
            var events=new ActionEventSet();events.setActionEvent(ActionEventType.ON_INTERACTION,interaction);npc.setActionEventSet(events);
            mob.addTag("human_journey.npc");mob.getPersistentData().putString("human_journey:template",template.id());mob.getPersistentData().putString("human_journey:role",template.role());
            if(!level.addFreshEntity(mob))throw new IllegalArgumentException("server rejected the new NPC entity");
            return mob.getCustomName().getString();
        }catch(RuntimeException error){mob.discard();throw error;}
    }
    public static DialogDataSet dialogs(NpcTemplate template,NpcIdentity identity){
        var set=new DialogDataSet();
        for(var page:template.pages()){
            Set<DialogButtonEntry> buttons=new LinkedHashSet<>();
            for(var button:page.buttons()){
                var actions=new ActionDataSet();for(var action:button.actions())actions.add(action(action,identity));
                buttons.add(new DialogButtonEntry(button.text(),identity.label("button",page.id()+"/"+button.id()),DialogButtonType.DEFAULT,actions,conditions(button.conditions())));
            }
            var dialog=new DialogDataEntry(identity.label("dialog",page.id()),template.name(),page.text(),buttons);dialog.setPriority(page.id().equals(template.main())?10:-1);dialog.setConditions(conditions(page.conditions()));
            if(!set.addDialog(dialog))throw new IllegalArgumentException("Easy NPC rejected dialogue "+page.id());
        }return set;
    }
    private static Set<ConditionDataEntry> conditions(List<NpcTemplate.Condition> source){
        Set<ConditionDataEntry> result=new LinkedHashSet<>();for(var c:source)result.add(new ConditionDataEntry(ConditionType.SCOREBOARD,ConditionOperationType.valueOf(c.operator()),c.objective(),c.value()));return result;
    }
    public static ActionDataEntry action(NpcTemplate.Action source,NpcIdentity identity){
        String argument=switch(source.type()){
            case "OPEN_NAMED_DIALOG"->identity.label("dialog",source.argument());
            case "SCOREBOARD"->source.operation().toLowerCase(Locale.ROOT)+":"+source.argument()+":"+source.value();
            default->source.argument();
        };
        return new ActionDataEntry(ActionDataType.valueOf(source.type()),new ConditionDataSet(conditions(source.conditions())),argument,identity.entity(),BlockPos.ZERO,source.asPlayer(),false,source.permission());
    }
    public static MerchantOffers offers(NpcTemplate template){
        var result=new MerchantOffers();for(var trade:template.trades()){
            var a=cost(trade.input());var b=trade.second()==null?Optional.<ItemCost>empty():Optional.of(cost(trade.second()));var out=new ItemStack(item(trade.output().id()),trade.output().count());
            result.add(new MerchantOffer(a,b,out,trade.uses(),trade.maxUses(),0,0F));
        }return result;
    }
    private static Item item(String id){var key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key))throw new IllegalArgumentException("invalid item ID: "+id);return BuiltInRegistries.ITEM.getValue(key);}
    private static ItemCost cost(NpcTemplate.Item stack){return new ItemCost(item(stack.id()),stack.count());}
    private static BlockPos findSafe(ServerPlayer player,Mob mob){
        var level=player.level();var origin=player.blockPosition();
        for(int radius=2;radius<=4;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++){
            if(Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
            for(int dy:new int[]{0,1,-1,2,-2}){
                var pos=origin.offset(dx,dy,dz);if(!level.hasChunkAt(pos)||!level.hasChunkAt(pos.above(2))||!level.hasChunkAt(pos.below()))continue;
                if(pos.getY()<level.getMinY()+1||pos.getY()+mob.getBbHeight()>=level.getMaxY())continue;
                var floor=level.getBlockState(pos.below());if(!floor.isFaceSturdy(level,pos.below(),Direction.UP)||floor.is(Blocks.MAGMA_BLOCK)||floor.is(Blocks.CACTUS)||floor.is(Blocks.CAMPFIRE)||floor.is(Blocks.SOUL_CAMPFIRE))continue;
                mob.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);var box=mob.getBoundingBox();
                if(level.getWorldBorder().isWithinBounds(box)&&!level.containsAnyLiquid(box)&&level.noCollision(mob,box)&&level.getBlockState(pos).isAir()&&level.getBlockState(pos.above()).isAir())return pos;
            }
        }return null;
    }
    private EasyNpc652Adapter(){}
}
