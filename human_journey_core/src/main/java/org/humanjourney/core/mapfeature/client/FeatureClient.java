package org.humanjourney.core.mapfeature.client;
import net.minecraft.client.Minecraft;
import org.humanjourney.core.mapfeature.*;
import org.humanjourney.core.geography.client.GeographyClientConfig;
public final class FeatureClient {
 private static final ClientFeatureState STATE=new ClientFeatureState();private static long lastRequest;private static String control="Waiting for feature control";
 public static ClientFeatureState state(){return STATE;}
 public static void register(){FeatureNetwork.receiver((packet,connection)->{var current=Minecraft.getInstance().getConnection();if(current==null||current.getConnection()!=connection)return;STATE.accept(packet);if(STATE.needsResync())request();});}
 public static void role(boolean participant){STATE.role(participant);if(participant&&STATE.needsResync())request();}
 public static void clear(){STATE.clear();lastRequest=0;}
 public static void request(){long now=System.nanoTime();if(lastRequest==0||now-lastRequest>=3000000000L){lastRequest=now;FeatureNetwork.request();}}
 public static void control(String s){control=s;}
 public static String describe(){var h=STATE.snapshot().header();return "Features: enabled="+GeographyClientConfig.FEATURES_ENABLED.get()+" mode="+GeographyClientConfig.featureMode().id()+" received="+STATE.snapshot().count()+" revision="+(h==null?"none":h.sequence()+" catalog="+h.catalog()+" discovery="+h.discovery())+" sync="+STATE.problem()+" control="+control;}
 private FeatureClient(){}
}
