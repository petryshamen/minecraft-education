package org.humanjourney.core.mapfeature;
import java.util.*;
import net.minecraft.commands.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import com.mojang.brigadier.arguments.*;
public final class FeatureCommands {
 public static void register(RegisterCommandsEvent event){
  var discovery=Commands.literal("discovery")
   .then(Commands.literal("list").executes(c->list(c.getSource(),1)).then(Commands.argument("page",IntegerArgumentType.integer(1)).executes(c->list(c.getSource(),IntegerArgumentType.getInteger(c,"page")))))
   .then(Commands.literal("mark").then(Commands.argument("id",StringArgumentType.word()).suggests((c,b)->SharedSuggestionProvider.suggest(FeatureCatalog.state().data().features().keySet(),b)).executes(c->mark(c.getSource(),StringArgumentType.getString(c,"id")))))
   .then(Commands.literal("reset").executes(c->{SavedFeatures.get(c.getSource().getServer()).reset();FeatureServer.sync(c.getSource().getServer());c.getSource().sendSuccess(()->Component.literal("Shared map feature discovery reset. Macro/Local knowledge unchanged; move to re-evaluate features."),true);return 1;}));
  event.getDispatcher().register(Commands.literal("hj").then(Commands.literal("mapfeature").requires(s->s.hasPermission(2)).then(Commands.literal("status").executes(c->{c.getSource().sendSuccess(()->Component.literal(FeatureServer.describe(c.getSource().getServer())),false);return 1;})).then(discovery)));
 }
 private static int mark(CommandSourceStack source,String id){if(!FeatureCatalog.state().data().features().containsKey(id)){source.sendFailure(Component.literal("Unknown map feature ID: "+id));return 0;}boolean changed=SavedFeatures.get(source.getServer()).mark(id);FeatureServer.sync(source.getServer());source.sendSuccess(()->Component.literal((changed?"Marked map feature: ":"Already discovered: ")+id),true);return 1;}
 private static int list(CommandSourceStack source,int page){var ids=new ArrayList<>(new TreeSet<>(SavedFeatures.get(source.getServer()).snapshot().ids()));int pages=Math.max(1,(ids.size()+11)/12);if(page>pages){source.sendFailure(Component.literal("Page out of range: 1.."+pages));return 0;}var known=FeatureCatalog.state().data().features();var entries=ids.subList((page-1)*12,Math.min(page*12,ids.size())).stream().map(id->id+(known.containsKey(id)?"":" (stale)")).toList();source.sendSuccess(()->Component.literal("Map feature discoveries="+ids.size()+" page="+page+"/"+pages+" "+entries),false);return 1;}
 private FeatureCommands(){}
}
