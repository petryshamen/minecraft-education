package org.humanjourney.core.wrap;
import net.minecraftforge.common.ForgeConfigSpec;

public final class WrapConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.BooleanValue ENABLED,LOG_CROSSINGS;
 static{
  var b=new ForgeConfigSpec.Builder();b.push("world_wrap");
  ENABLED=b.comment("Experimental physical wrap, Overworld only. Disabled until manual test-world acceptance.",
   "Requires loaded destination chunks; never loads/generates terrain. Exact Earth interval [-12288,12288).")
   .define("enabled",false);
  LOG_CROSSINGS=b.comment("Log successful crossings. Deferred/failing groups are rate-limited independently.").define("log_crossings",true);
  b.pop();SPEC=b.build();
 }
 private WrapConfig(){}
}
