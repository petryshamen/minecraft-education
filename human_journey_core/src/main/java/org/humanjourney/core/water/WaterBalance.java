package org.humanjourney.core.water;

public final class WaterBalance {
    public record State(WaterPolicy policy,String source,long revision,String lastReload) {}
    private static volatile State state=initial();
    private static State initial(){return new State(WaterPolicy.defaults(),"safety defaults",0,"not loaded");}
    public static State state(){return state;}
    public static WaterPolicy current(){return state.policy;}
    public static void accept(WaterPolicy policy,String source){state=new State(policy,source,state.revision+1,"OK");}
    public static void missing(){accept(WaterPolicy.defaults(),"safety defaults (file missing)");}
    public static void reject(String error){var old=state;state=new State(old.policy,old.source,old.revision,"REJECTED: "+error);}
    public static void reset(){state=initial();}
    private WaterBalance(){}
}
