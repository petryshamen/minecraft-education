package org.humanjourney.core.fishing;
public final class FishingBalance {
    /** Null policy is an intentional vanilla bypass, never an implicit Human Journey balance. */
    public record State(FishingPolicy policy,String source,long revision,String lastReload,String lastSuccess){}
    private static volatile State state=initial();
    private static State initial(){return new State(null,"vanilla fallback (no policy)",0,"not loaded","never");}
    public static State state(){return state;}
    public static void accept(FishingPolicy policy,String source){if(policy==null)throw new IllegalArgumentException("policy required");state=new State(policy,source,state.revision()+1,"OK",java.time.Instant.now().toString());}
    public static void missing(){state=new State(null,"vanilla fallback (file missing)",state.revision()+1,"OK",java.time.Instant.now().toString());}
    public static void reject(String error){var old=state;state=new State(old.policy(),old.source(),old.revision(),"REJECTED: "+error,old.lastSuccess());}
    public static void reset(){state=initial();}
    public static String describe(){var s=state;var p=s.policy();return "Fishing bite time: "+(p==null?"vanilla (no override)":p.minimumSeconds()+"–"+p.maximumSeconds()+"s | Lure reduction: "+p.lureReductionSecondsPerLevel()+"s/level")+" | source="+s.source()+" | revision="+s.revision()+" | reload="+s.lastReload();}
    private FishingBalance(){}
}
