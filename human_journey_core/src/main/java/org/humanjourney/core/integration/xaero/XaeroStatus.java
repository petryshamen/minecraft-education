package org.humanjourney.core.integration.xaero;
/** No Xaero/client class references. Shared by the optional Mixin gate and client diagnostics. */
public final class XaeroStatus {
 public record State(boolean detected,String version,boolean supported,boolean active,String reason){}
 private static State state=new State(false,"absent",false,false,"Xaero World Map is not installed");private static boolean failed;
 public static boolean supported(boolean physicalClient,String version){return physicalClient&&"1.39.13".equals(version);}
 public static void detect(String version){failed=false;state=new State(version!=null,version==null?"absent":version,supported(true,version),false,version==null?"Xaero World Map is not installed":supported(true,version)?"Ready; waiting for fullscreen map hook":"Unsupported Xaero World Map version; only 1.39.13 is supported");if(version!=null&&!state.supported())com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] {}: {}",state.reason(),version);}
 public static State state(){return state;}
 public static boolean failed(){return failed;}
 public static void observed(){if(!failed)state=new State(state.detected(),state.version(),state.supported(),true,"");}
 public static void waiting(String why){if(!failed)state=new State(state.detected(),state.version(),state.supported(),false,why);}
 public static void disable(Throwable error){if(!failed){failed=true;String why="Xaero geography adapter disabled for session: "+error.getClass().getSimpleName()+": "+error.getMessage();state=new State(state.detected(),state.version(),state.supported(),false,why);com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] {}",why,error);}}
 private XaeroStatus(){}
}
