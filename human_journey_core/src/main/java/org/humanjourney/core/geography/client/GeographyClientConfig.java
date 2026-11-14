package org.humanjourney.core.geography.client;
import net.minecraftforge.common.ForgeConfigSpec;
public final class GeographyClientConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.BooleanValue ENABLED;
 public static final ForgeConfigSpec.IntValue X,Y;
 public static final ForgeConfigSpec.DoubleValue SCALE,OPACITY;
 public static final ForgeConfigSpec.BooleanValue MAP_ENABLED,MAP_OUTLINES,MAP_HOVER;
 public static final ForgeConfigSpec.DoubleValue MAP_OPACITY,MAP_WIDTH;
 static{var b=new ForgeConfigSpec.Builder();b.push("geography_hud");ENABLED=b.comment("Show server-authoritative geography for Human Journey participants").define("enabled",true);X=b.comment("GUI-scaled pixels from left; default beside a normal top-left minimap").defineInRange("x_offset",85,-8192,8192);Y=b.defineInRange("y_offset",10,-8192,8192);SCALE=b.defineInRange("scale",.8D,.5D,3D);OPACITY=b.defineInRange("background_opacity",.35D,0D,1D);b.pop();b.push("world_map_geography");MAP_ENABLED=b.define("world_map_geography_enabled",true);MAP_OUTLINES=b.define("world_map_local_outlines_enabled",true);MAP_HOVER=b.define("world_map_hover_enabled",true);MAP_OPACITY=b.defineInRange("world_map_outline_opacity",.4D,0D,1D);MAP_WIDTH=b.defineInRange("world_map_outline_width",1D,.5D,4D);b.pop();SPEC=b.build();}
 private GeographyClientConfig(){}
}
