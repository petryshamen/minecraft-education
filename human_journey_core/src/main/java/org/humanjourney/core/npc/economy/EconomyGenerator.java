package org.humanjourney.core.npc.economy;
import java.util.*;
import org.humanjourney.core.npc.context.NpcContext;
public final class EconomyGenerator {
 public record Offer(EconomyCatalog.Commodity output,String concreteOutput,BarterSolver.Result barter){}
 public record Plan(int matchingSellers,List<Offer> offers,Map<String,String> skipped){public Plan{offers=List.copyOf(offers);skipped=Map.copyOf(skipped);}}
 public static Plan generate(EconomyCatalog catalog,String role,NpcContext context,GeoProfile profile,EconomySettings settings,EconomyCatalog.Items items){
  var eraGoods=catalog.commodities().values().stream().filter(c->c.era().equals(context.era())).sorted(Comparator.comparing(EconomyCatalog.Commodity::id)).toList();
  var buyers=eraGoods.stream().filter(c->c.buyers().contains(role)).map(c->new BarterSolver.Good(c.id(),c.value(),catalog.inputMaximum(c,items))).toList();
  List<Offer> offers=new ArrayList<>();Map<String,String> skipped=new TreeMap<>();int sellers=0;
  for(var output:eraGoods)if(output.sellers().contains(role)){
   if(!output.availability().matches(profile)){skipped.put(output.id(),"sell geography: "+output.availability().explain(profile));continue;}sellers++;
   String concrete=catalog.output(output,context,settings);int max=items.maximum(concrete,output.components());var solved=BarterSolver.solve(new BarterSolver.Good(output.id(),output.value(),max),buyers);
   if(solved.isEmpty())skipped.put(output.id(),"no legal payment within 10% using two slots and actual stack limits");else offers.add(new Offer(output,concrete,solved.get()));
  }return new Plan(sellers,offers,skipped);
 }
 private EconomyGenerator(){}
}
