package org.humanjourney.core.geography.client;
import net.minecraftforge.common.ForgeConfigSpec;
public final class GeographyClientConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.BooleanValue ENABLED;
 public static final ForgeConfigSpec.IntValue X,Y;
 public static final ForgeConfigSpec.DoubleValue SCALE,OPACITY;
 public static final ForgeConfigSpec.BooleanValue MAP_ENABLED,MAP_OUTLINES,MAP_HOVER;
 public static final ForgeConfigSpec.DoubleValue MAP_OPACITY,MAP_WIDTH;
 public static final ForgeConfigSpec.ConfigValue<String> MAP_MODE;
 public static final ForgeConfigSpec.DoubleValue MACRO_OPACITY,MACRO_WIDTH,HOVER_LOCAL_OPACITY,HOVER_LOCAL_WIDTH,HOVER_MACRO_OPACITY,HOVER_MACRO_WIDTH;
 public static org.humanjourney.core.geography.map.client.BoundaryMode mode(){return org.humanjourney.core.geography.map.client.BoundaryMode.parse(MAP_MODE.get());}
 public static synchronized void mode(org.humanjourney.core.geography.map.client.BoundaryMode next){String old=MAP_MODE.get();try{MAP_MODE.set(next.id());SPEC.save();}catch(RuntimeException error){MAP_MODE.set(old);throw error;}}

 static{var b=new ForgeConfigSpec.Builder();b.push("geography_hud");ENABLED=b.comment("Show server-authoritative geography for Human Journey participants").define("enabled",true);X=b.comment("GUI-scaled pixels from left; default beside a normal top-left minimap").defineInRange("x_offset",85,-8192,8192);Y=b.defineInRange("y_offset",10,-8192,8192);SCALE=b.defineInRange("scale",.8D,.5D,3D);OPACITY=b.defineInRange("background_opacity",.35D,0D,1D);b.pop();b.push("world_map_geography");MAP_ENABLED=b.define("world_map_geography_enabled",true);MAP_OUTLINES=b.comment("Legacy migration input only; world_map_boundary_mode is authoritative after upgrade").define("world_map_local_outlines_enabled",true);MAP_HOVER=b.define("world_map_hover_enabled",true);MAP_OPACITY=b.defineInRange("world_map_outline_opacity",.25D,0D,1D);MAP_WIDTH=b.defineInRange("world_map_outline_width",1D,.5D,4D);MAP_MODE=b.comment("Boundary display: hover, locals, all, off. Tooltip is configured independently").define("world_map_boundary_mode","hover",org.humanjourney.core.geography.map.client.BoundaryMode::valid);
 MACRO_OPACITY=b.defineInRange("world_map_macro_opacity",.4D,0D,1D);MACRO_WIDTH=b.defineInRange("world_map_macro_width",1.5D,.5D,4D);
 HOVER_LOCAL_OPACITY=b.defineInRange("world_map_hover_local_opacity",.9D,0D,1D);HOVER_LOCAL_WIDTH=b.defineInRange("world_map_hover_local_width",2D,.5D,4D);
 HOVER_MACRO_OPACITY=b.defineInRange("world_map_hover_macro_opacity",.6D,0D,1D);HOVER_MACRO_WIDTH=b.defineInRange("world_map_hover_macro_width",2D,.5D,4D);b.pop();SPEC=b.build();}
 private GeographyClientConfig(){}
}
