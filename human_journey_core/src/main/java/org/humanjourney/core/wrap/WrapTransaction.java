package org.humanjourney.core.wrap;
import java.util.*;

/** Preflight ALL members, mutate ALL on the server thread, then publish. No detach/recreate. */
public final class WrapTransaction {
 public interface Member {
  String problem(double dx);
  void move(double dx);
  void restore();
  void publish();
 }
 public record Result(boolean moved,String reason){}
 public static Result run(List<? extends Member> members,double dx){
  if(members.isEmpty()||members.size()>16||!Double.isFinite(dx)||dx==0)return new Result(false,"invalid group/offset");
  for(var member:members){String problem=member.problem(dx);if(problem!=null)return new Result(false,problem);}
  try{for(var member:members)member.move(dx);}
  catch(RuntimeException failure){
   // Restore even the member whose move threw after a possible position callback.
   RuntimeException rollback=null;
   for(var member:members)try{member.restore();}catch(RuntimeException e){if(rollback==null)rollback=e;else rollback.addSuppressed(e);}
   if(rollback!=null)throw new IllegalStateException("World-wrap rollback failed; disable feature",rollback);
   return new Result(false,"position update failed; group restored: "+failure.getClass().getSimpleName());
  }
  // Publishing does not change server identity or mount topology. A disconnected client
  // is resynchronised by normal join/tracking; never roll server state back after packets.
  RuntimeException publication=null;
  for(var member:members)try{member.publish();}catch(RuntimeException e){if(publication==null)publication=e;else publication.addSuppressed(e);}
  if(publication!=null)throw new IllegalStateException("World-wrap positions committed; client resynchronisation failed",publication);
  return new Result(true,"wrapped");
 }
 private WrapTransaction(){}
}
