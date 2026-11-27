package org.humanjourney.core.advancement;
public final class NativeStore {
 public record State(NativePresentation model,String source,String error,long revision){}
 private static volatile State state=new State(NativePresentation.empty(),"missing","",0);
 public static State state(){return state;}
 public static void accept(NativePresentation value,String source){state=new State(value,source,"",state.revision+1);}
 public static void reject(String source,String error){state=new State(NativePresentation.empty(),source,error,state.revision+1);}
 public static void reset(){state=new State(NativePresentation.empty(),"missing","",0);}
 private NativeStore(){}
}
