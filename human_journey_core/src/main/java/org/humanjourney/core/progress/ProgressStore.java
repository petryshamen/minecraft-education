package org.humanjourney.core.progress;
/** Transient authoring cache only. No SavedData, advancement or score ledger. */
public final class ProgressStore {
 public record State(ProgressGraph graph,String source,String error,long revision){}
 private static volatile State state=new State(ProgressGraph.empty(),"missing","",0);
 public static State state(){return state;}
 public static void accept(ProgressGraph graph,String source){state=new State(graph,source,"",state.revision+1);}
 public static void reject(String error){state=new State(state.graph,state.source,error,state.revision);}
 public static void missing(){accept(ProgressGraph.empty(),"missing");}
 public static void reset(){state=new State(ProgressGraph.empty(),"missing","",0);}
 private ProgressStore(){}
}
