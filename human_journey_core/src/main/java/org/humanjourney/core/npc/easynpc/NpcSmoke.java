package org.humanjourney.core.npc.easynpc;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import de.markusbordihn.easynpc.data.dialog.DialogDataSet;
import de.markusbordihn.easynpc.data.skin.SkinDataEntry;
import de.markusbordihn.easynpc.data.trading.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraft.data.*;
import org.humanjourney.core.npc.*;
import java.util.concurrent.CompletableFuture;
import java.nio.file.*;
/** Opt-in real-library data checks, without constructing an NPC, Level, game server or client. */
public final class NpcSmoke {
    public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
        public String getName(){return "Human Journey NPC template adapter structural smoke";}
        public CompletableFuture<?> run(CachedOutput output){return event.getLookupProvider().thenAccept(lookup->{
            try{
                NpcDependency.require();check(BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse("easy_npc:humanoid")),"registered humanoid type");
                for(String id:new String[]{"hunter","story_test"}){
                    String json=Files.readString(Path.of("../../example-npc-datapack/data/human_journey/npc/"+id+".json"));
                    var template=NpcTemplate.parse("human_journey:"+id,JsonParser.parseString(json));template.validateItems(item->{var key=ResourceLocation.parse(item);return BuiltInRegistries.ITEM.containsKey(key)?BuiltInRegistries.ITEM.getValue(key).getDefaultInstance().getMaxStackSize():0;});
                    var a=NpcIdentity.fresh();var b=NpcIdentity.fresh();var dialogs=EasyNpc652Adapter.dialogs(template,a);var other=EasyNpc652Adapter.dialogs(template,b);
                    var restored=new DialogDataSet(dialogs.createTag());check(restored.getDialogsByLabel().size()==template.pages().size(),"dialogue NBT round trip");
                    for(var page:template.pages()){
                        var dialog=dialogs.getDialog(a.label("dialog",page.id()));var loaded=restored.getDialog(dialog.getId());
                        check(loaded!=null&&loaded.getId().equals(dialog.getId())&&loaded.getText().equals(page.text()),"stable saved instance dialogue identity/text");
                        check(!dialog.getId().equals(other.getDialog(b.label("dialog",page.id())).getId()),"distinct NPC dialogue IDs");
                        for(var button:page.buttons())for(var action:button.actions()){
                            var first=EasyNpc652Adapter.action(action,a);var second=EasyNpc652Adapter.action(action,b);
                            check(!first.getId().equals(second.getId()),"distinct NPC action IDs");check(first.getId().equals(new de.markusbordihn.easynpc.data.action.ActionDataEntry(first.createTag()).getId()),"action identity survives native NBT");
                            if(action.type().equals("OPEN_NAMED_DIALOG"))check(dialogs.hasDialog(first.command()),"named dialogue runtime reference");
                        }
                    }
                    var offers=EasyNpc652Adapter.offers(template);var ops=RegistryOps.create(NbtOps.INSTANCE,lookup);
                    var saved=MerchantOffers.CODEC.encodeStart(ops,offers).getOrThrow();var loaded=MerchantOffers.CODEC.parse(ops,saved).getOrThrow();
                    check(loaded.size()==template.trades().size(),"merchant NBT offers");
                    for(int i=0;i<loaded.size();i++){check(loaded.get(i).getUses()==template.trades().get(i).uses()&&loaded.get(i).getMaxUses()==template.trades().get(i).maxUses(),"uses/maxUses initialized and encoded");}
                    var config=new TradingDataSet();config.setType(TradingType.ADVANCED);config.setResetsEveryMin(template.resetMinutes());check(new TradingDataSet(config.createTag()).getResetsEveryMin()==template.resetMinutes(),"reset configuration NBT");
                    var skin=SkinDataEntry.createRemoteSkin(template.skinUrl());check(new SkinDataEntry(skin.createTag()).url().equals(template.skinUrl()),"remote skin configuration NBT");
                }
                LogUtils.getLogger().info("[HJ Core] NPC_TEMPLATE_SMOKE_OK: exact Easy NPC 6.5.2 loaded; two templates, item IDs, dialogues/actions/skin/trades codecs and instance isolation; no entity/world created");
            }catch(Exception e){throw new IllegalStateException("NPC structural smoke failed",e);}
        });}
    }));}
    private static void check(boolean value,String message){if(!value)throw new IllegalStateException(message);}
    private NpcSmoke(){}
}
