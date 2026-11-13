package org.humanjourney.core.juice;
public final class JuiceStore {
 public record State(JuiceCatalog catalog,String source,String error,long revision){}
 private static volatile State state=new State(JuiceCatalog.empty(),"absent",null,0);
 public static State state(){return state;}
 public static void accept(JuiceCatalog catalog,String source){state=new State(catalog,source,null,state.revision()+1);}
 public static void reject(String error){state=new State(state.catalog(),state.source(),error,state.revision());}
 public static void reset(){state=new State(JuiceCatalog.empty(),"absent",null,0);}
 private JuiceStore(){}
}
