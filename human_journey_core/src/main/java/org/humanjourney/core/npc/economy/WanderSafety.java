package org.humanjourney.core.npc.economy;
import java.util.*;
import java.util.function.*;
/** Pure route decisions shared by actual native-node inspection and synthetic tests. */
public final class WanderSafety {
 public static final int ATTEMPTS=16;
 public record Point(int x,int y,int z){}
 public static boolean land(boolean fluid,boolean supported,boolean clear,boolean harmless){return !fluid&&supported&&clear&&harmless;}
 public static boolean transition(Point a,Point b){return Math.abs(a.x()-b.x())<=1&&Math.abs(a.z()-b.z())<=1&&Math.abs(a.y()-b.y())<=1;}
 private static boolean diagonal(Point a,Point b,Predicate<Point> safe){if(a.x()==b.x()||a.z()==b.z())return true;int y=Math.max(a.y(),b.y());return safe.test(new Point(a.x(),y,b.z()))&&safe.test(new Point(b.x(),y,a.z()));}
 public static boolean route(Point start,List<Point> nodes,Predicate<Point> safe){if(nodes.isEmpty()||!safe.test(start))return false;Point previous=start,before=null;for(var node:nodes){if(!safe.test(node)||!transition(previous,node)||!diagonal(previous,node,safe))return false;if(before!=null&&Math.abs(before.x()-node.x())==1&&Math.abs(before.z()-node.z())==1&&!diagonal(before,node,safe))return false;before=previous;previous=node;}return true;}
 public static boolean recovery(boolean inside,boolean onSafeLand,int currentY,int homeY){return !inside||!onSafeLand||currentY<homeY-1;}
 public static <T> Optional<T> firstSafe(IntFunction<T> candidate,Predicate<T> safe){for(int i=0;i<ATTEMPTS;i++){T value=candidate.apply(i);if(value!=null&&safe.test(value))return Optional.of(value);}return Optional.empty();}
 private WanderSafety(){}
}
