package org.humanjourney.core.npc.economy;
import java.util.*;
import java.util.random.RandomGenerator;
public final class PaymentDiversity {
 public static long usage(BarterSolver.Candidate candidate,Map<String,Integer> counts){return candidate.commodities().stream().mapToLong(id->counts.getOrDefault(id,0)).sum();}
 public static BarterSolver.Candidate select(List<BarterSolver.Candidate> candidates,Map<String,Integer> counts,RandomGenerator random){if(candidates.isEmpty())throw new IllegalArgumentException("no legal payment candidates");long minimum=candidates.stream().mapToLong(c->usage(c,counts)).min().orElseThrow();var preferred=candidates.stream().filter(c->usage(c,counts)==minimum).toList();return preferred.get(random.nextInt(preferred.size()));}
 public static void record(BarterSolver.Candidate selected,Map<String,Integer> counts){for(String id:selected.commodities())counts.merge(id,1,Integer::sum);}
 private PaymentDiversity(){}
}
