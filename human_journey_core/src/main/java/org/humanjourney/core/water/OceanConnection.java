package org.humanjourney.core.water;

import java.util.*;
import java.util.function.Predicate;

/** Six-face water flood fill, limited by path length, never by proximity through land. */
public final class OceanConnection {
    public static final int MAX_RADIUS=16;
    public record Cell(int x,int y,int z){}
    private record Step(Cell cell,int distance){}
    private static final int[][] DIRECTIONS={{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
    public static boolean reachesOcean(Cell origin,int radius,Predicate<Cell> water,Predicate<Cell> ocean){
        if(radius<0||radius>MAX_RADIUS)throw new IllegalArgumentException("ocean connection radius must be 0.."+MAX_RADIUS);
        if(radius==0||!water.test(origin))return false;
        Set<Cell> seen=new HashSet<>();ArrayDeque<Step> queue=new ArrayDeque<>();
        seen.add(origin);queue.add(new Step(origin,0));
        while(!queue.isEmpty()){
            Step current=queue.removeFirst();Cell p=current.cell();
            if(ocean.test(p))return true;
            if(current.distance()==radius)continue;
            for(int[] d:DIRECTIONS){
                Cell next=new Cell(p.x()+d[0],p.y()+d[1],p.z()+d[2]);
                if(seen.add(next)&&water.test(next))queue.addLast(new Step(next,current.distance()+1));
            }
        }
        return false;
    }
    private OceanConnection(){}
}
