package org.humanjourney.core.npc.economy;
import java.util.*;
import org.humanjourney.core.npc.context.NpcContext;
public final class EconomyGenerator {
 public record Offer(EconomyCatalog.Commodity output,String concreteOutput,BarterSolver.Result barter,int legalCombinations,List<String> paymentCommodities,long priorUsage){public Offer{paymentCommodities=List.copyOf(paymentCommodities);}}
 public record Plan(int matchingSellers,List<Offer> offers,Map<String,String> skipped,Map<String,Integer> buyerUsage){public Plan{offers=List.copyOf(offers);skipped=Map.copyOf(skipped);buyerUsage=Map.copyOf(buyerUsage);}}
 public static Plan generate(EconomyCatalog catalog,String role,NpcContext context,GeoProfile profile,EconomySettings settings,EconomyCatalog.Items items){
  return generate(catalog,role,context,profile,settings,items,java.util.concurrent.ThreadLocalRandom.current());
 }
 public static Plan generate(EconomyCatalog catalog,String role,NpcContext context,GeoProfile profile,EconomySettings settings,EconomyCatalog.Items items,java.util.random.RandomGenerator random){
  var eraGoods=catalog.commodities().values().stream().filter(c->c.era().equals(context.era())).sorted(Comparator.comparing(EconomyCatalog.Commodity::id)).toList();
  var buyers=eraGoods.stream().filter(c->c.buyers().contains(role)).map(c->new BarterSolver.Good(c.id(),c.value(),catalog.inputMaximum(c,items))).toList();
  List<Offer> offers=new ArrayList<>();Map<String,String> skipped=new TreeMap<>();int sellers=0;Map<String,Integer> usage=new TreeMap<>();
  for(var output:eraGoods)if(output.sellers().contains(role)){
   if(!output.availability().matches(profile)){skipped.put(output.id(),"sell geography: "+output.availability().explain(profile));continue;}sellers++;
   String concrete=catalog.output(output,context,settings);int max=items.maximum(concrete,output.components());var candidates=BarterSolver.candidates(new BarterSolver.Good(output.id(),output.value(),max),buyers);
   if(candidates.isEmpty())skipped.put(output.id(),"no legal payment within 10% using two slots and actual stack limits");else{var chosen=PaymentDiversity.select(candidates,usage,random);long prior=PaymentDiversity.usage(chosen,usage);PaymentDiversity.record(chosen,usage);offers.add(new Offer(output,concrete,chosen.recipe(),candidates.size(),chosen.commodities(),prior));}
  }return new Plan(sellers,offers,skipped,usage);
 }
 public static String trace(Plan plan){String text="payments="+plan.offers().stream().limit(8).map(o->o.output().id()+" pool="+o.legalCombinations()+" selected="+o.paymentCommodities()+" prior usage="+o.priorUsage()+" quantities="+o.barter().first().count()+"/"+(o.barter().second()==null?0:o.barter().second().count())+" -> "+o.barter().outputCount()).toList()+" | buyer usage="+plan.buyerUsage().entrySet().stream().sorted(Map.Entry.comparingByKey()).limit(16).toList()+" ("+plan.offers().size()+" offers, "+plan.buyerUsage().size()+" buyers total; trace bounded)";return text.length()>12000?text.substring(0,12000)+" [trace truncated]":text;}
 private EconomyGenerator(){}
}
