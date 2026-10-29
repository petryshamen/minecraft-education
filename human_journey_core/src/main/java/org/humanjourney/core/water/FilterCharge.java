package org.humanjourney.core.water;
/** Atomic capacity calculation. No idle drain, no spending until the complete fill is affordable. */
public final class FilterCharge {
    public record Plan(int consumed,int remaining,int duration){}
    public static Plan plan(int remaining,int duration,int perItem,int count,int cost){
        remaining=Math.max(0,remaining);if(cost<1||perItem<0||count<0)return null;
        long missing=Math.max(0L,(long)cost-remaining),needed=missing==0?0:perItem==0?Long.MAX_VALUE:(missing+perItem-1)/perItem;
        if(needed>count)return null;
        long available=(long)remaining+needed*perItem;if(available>Integer.MAX_VALUE)return null;
        return new Plan((int)needed,(int)available-cost,needed>0?(int)available:Math.max(duration,remaining));
    }
    private FilterCharge(){}
}
