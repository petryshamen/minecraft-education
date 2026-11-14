package org.humanjourney.core.integration.xaero.client;
import net.minecraftforge.fml.ModList;
import org.humanjourney.core.integration.xaero.XaeroStatus;
public final class XaeroClient {
 public static void register(){String version=ModList.get().getMods().stream().filter(m->m.getModId().equals("xaeroworldmap")).map(m->m.getVersion().toString()).findFirst().orElse(null);XaeroStatus.detect(version);if(!XaeroStatus.state().supported())return;try{Class.forName("org.humanjourney.core.integration.xaero.client.Xaero13913Adapter").getMethod("prepare").invoke(null);}catch(ReflectiveOperationException|RuntimeException|LinkageError error){XaeroStatus.disable(error);return;}try{Class.forName("org.humanjourney.core.integration.xaero.client.XaeroBoundaryControl").getMethod("register").invoke(null);}catch(ReflectiveOperationException|RuntimeException|LinkageError error){XaeroStatus.control("Map control registration failed; config-based boundaries remain usable: "+error.getClass().getSimpleName());com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] {}",XaeroStatus.control(),error);}}
 private XaeroClient(){}
}
