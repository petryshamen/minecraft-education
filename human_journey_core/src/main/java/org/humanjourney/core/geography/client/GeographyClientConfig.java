package org.humanjourney.core.geography.client;
import net.minecraftforge.common.ForgeConfigSpec;
public final class GeographyClientConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.BooleanValue ENABLED;
 public static final ForgeConfigSpec.IntValue X,Y;
 public static final ForgeConfigSpec.DoubleValue SCALE,OPACITY;
 static{var b=new ForgeConfigSpec.Builder();b.push("geography_hud");ENABLED=b.comment("Show server-authoritative geography for Human Journey participants").define("enabled",true);X=b.comment("GUI-scaled pixels from left; default beside a normal top-left minimap").defineInRange("x_offset",85,-8192,8192);Y=b.defineInRange("y_offset",10,-8192,8192);SCALE=b.defineInRange("scale",.8D,.5D,3D);OPACITY=b.defineInRange("background_opacity",.35D,0D,1D);b.pop();SPEC=b.build();}
 private GeographyClientConfig(){}
}
