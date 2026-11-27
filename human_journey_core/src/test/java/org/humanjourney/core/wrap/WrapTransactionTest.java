package org.humanjourney.core.wrap;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class WrapTransactionTest {
 static class Body implements WrapTransaction.Member {
  double x=12288.125;final double y=63.5,z=-345.25,yaw=123,pitch=-12,vx=0.5,vy=-0.1,vz=0.25;
  final UUID identity=UUID.randomUUID();final Map<String,Integer> inventory=new HashMap<>(Map.of("fixture",17));
  String invalid;boolean fail,failPublish;int moved,published,restored;
  public String problem(double dx){return invalid;}
  public void move(double dx){x+=dx;moved++;if(fail)throw new IllegalStateException("fixture");}
  public void restore(){x=12288.125;restored++;}
  public void publish(){published++;if(failPublish)throw new IllegalStateException("disconnected");}
 }
 @Test void singlePlayer(){var b=new Body();assertTrue(WrapTransaction.run(List.of(b),-24576).moved());assertEquals(-12287.875,b.x);assertEquals(1,b.published);}
 @Test void boatWithOneRider(){var boat=new Body();var rider=new Body();assertTrue(WrapTransaction.run(List.of(boat,rider),-24576).moved());assertEquals(boat.x,rider.x);}
 @Test void boatWithTwoRiders(){var bodies=List.of(new Body(),new Body(),new Body());assertTrue(WrapTransaction.run(bodies,-24576).moved());assertTrue(bodies.stream().allMatch(b->b.x==-12287.875&&b.published==1));}
 @Test void identitiesInventoriesAndDirectionUnchanged(){var b=new Body();var id=b.identity;var inventory=Map.copyOf(b.inventory);WrapTransaction.run(List.of(b),-24576);assertEquals(id,b.identity);assertEquals(inventory,b.inventory);assertEquals(63.5,b.y);assertEquals(-345.25,b.z);assertEquals(123,b.yaw);assertEquals(-12,b.pitch);assertEquals(.5,b.vx);assertEquals(-.1,b.vy);assertEquals(.25,b.vz);}
 @Test void anyRejectedRiderPreventsEveryMove(){var a=new Body();var b=new Body();b.invalid="rider unavailable";assertFalse(WrapTransaction.run(List.of(a,b),-24576).moved());assertEquals(0,a.moved);assertEquals(0,b.moved);assertEquals(0,a.published);}
 @Test void unloadedDestinationDeferred(){var a=new Body();a.invalid="chunk not loaded";assertFalse(WrapTransaction.run(List.of(a),-24576).moved());assertEquals(12288.125,a.x);}
 @Test void obstructedDestinationDeferred(){var a=new Body();a.invalid="collision";assertFalse(WrapTransaction.run(List.of(a),-24576).moved());assertEquals(0,a.published);}
 @Test void positionFailureRestoresWholeGroup(){var a=new Body();var b=new Body();b.fail=true;assertFalse(WrapTransaction.run(List.of(a,b),-24576).moved());assertEquals(12288.125,a.x);assertEquals(12288.125,b.x);assertEquals(1,a.restored);assertEquals(1,b.restored);assertEquals(0,a.published);}
 @Test void publishOnlyAfterAllPositions(){var a=new Body();var b=new Body(){public void publish(){assertEquals(-12287.875,a.x);super.publish();}};WrapTransaction.run(List.of(a,b),-24576);assertEquals(1,b.published);}
 @Test void publicationFailureDoesNotSplitOrUndoGroup(){var a=new Body();a.failPublish=true;var b=new Body();assertThrows(IllegalStateException.class,()->WrapTransaction.run(List.of(a,b),-24576));assertEquals(-12287.875,a.x);assertEquals(-12287.875,b.x);assertEquals(1,b.published);}
 @Test void simultaneousIndependentGroups(){var a=new Body();var b=new Body();WrapTransaction.run(List.of(a),-24576);WrapTransaction.run(List.of(b),-24576);assertEquals(a.x,b.x);assertNotEquals(a.identity,b.identity);}
 @Test void boundedGroup(){assertFalse(WrapTransaction.run(Collections.nCopies(17,new Body()),-24576).moved());assertFalse(WrapTransaction.run(List.of(),-24576).moved());}
 @Test void invalidOffsetNeverMoves(){var a=new Body();for(double dx:new double[]{0,Double.NaN,Double.POSITIVE_INFINITY})assertFalse(WrapTransaction.run(List.of(a),dx).moved());assertEquals(0,a.moved);}
}
