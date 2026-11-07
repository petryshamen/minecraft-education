package org.humanjourney.core.npc.economy;
import java.util.*;
/** Exhaustive legal slot quantities, indexed complementary values. No price randomization or truncation. */
public final class BarterSolver {
 public record Good(String id,long value,int stack){public Good{if(value<=0||value>1_000_000_000_000_000L||stack<1||stack>99)throw new IllegalArgumentException("invalid solver good");}}
 public record Payment(Good good,int count){public long value(){return good.value()*count;}}
 public record Result(Payment first,Payment second,int outputCount,long outputValue,int priority){
  public Result{if(second!=null&&first.good().id().equals(second.good().id())&&first.count()<second.count()){var swap=first;first=second;second=swap;}}
  public long inputValue(){return first.value()+(second==null?0:second.value());}public long error(){return Math.abs(inputValue()-outputValue);}public int totalItems(){return first.count()+(second==null?0:second.count())+outputCount;}
 }
 private static final Comparator<Result> ORDER=Comparator.comparingLong(Result::error).thenComparingInt(Result::totalItems).thenComparingInt(r->r.second()==null||r.first().good().id().equals(r.second().good().id())?1:2).thenComparingInt(r->r.second()==null?1:2).thenComparingInt(r->r.inputValue()>=r.outputValue()?0:1).thenComparing(r->r.first().good().id()).thenComparing(r->r.second()==null?"":r.second().good().id()).thenComparingInt(r->-r.first().count());
 private static Result best(Result a,Result b){return a==null||ORDER.compare(b,a)<0?b:a;}
 public static Optional<Result> solve(Good output,List<Good> inputs){
  List<Payment> payments=new ArrayList<>();for(var input:inputs)if(!input.id().equals(output.id()))for(int q=1;q<=input.stack();q++)payments.add(new Payment(input,q));
  payments.sort(Comparator.comparing((Payment p)->p.good().id()).thenComparingInt(Payment::count));
  var index=new TreeMap<Long,List<Payment>>();for(var p:payments){var bucket=index.computeIfAbsent(p.value(),v->new ArrayList<>());if(bucket.isEmpty()||bucket.getFirst().count()>p.count()){bucket.clear();bucket.add(p);}else if(bucket.getFirst().count()==p.count())bucket.add(p);}
  for(int phase=0;phase<4;phase++){
   Result found=null;boolean two=phase%2==1,exact=phase<2;
   for(int out=1;out<=output.stack();out++){long target=output.value()*out;
    for(var a:payments){
     if(!two){long error=Math.abs(a.value()-target);if(exact?error==0:error*10<=target)found=best(found,new Result(a,null,out,target,phase+1));}
     else{long complement=target-a.value();if(exact){var bucket=index.get(complement);if(bucket!=null){var b=choose(bucket,a);found=best(found,new Result(a,b,out,target,phase+1));}}
      else{var lower=index.floorEntry(complement);var upper=index.ceilingEntry(complement);for(var entry:Arrays.asList(lower,upper))if(entry!=null){var b=choose(entry.getValue(),a);long error=Math.abs(a.value()+b.value()-target);if(error*10<=target)found=best(found,new Result(a,b,out,target,phase+1));}}
     }
    }
   }if(found!=null)return Optional.of(found);
  }return Optional.empty();
 }
 private static Payment choose(List<Payment> bucket,Payment a){return bucket.stream().filter(b->b.good().id().equals(a.good().id())).findFirst().orElse(bucket.getFirst());}
 private BarterSolver(){}
}
