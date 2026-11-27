package org.humanjourney.core.wrap;
import net.minecraftforge.common.ForgeConfigSpec;

public final class WrapConfig {
 public static final ForgeConfigSpec SPEC;
 public static final ForgeConfigSpec.BooleanValue ENABLED,LOG_CROSSINGS,PROTECTION;
 public static final ForgeConfigSpec.IntValue ARRIVAL_INSET;
 static{
  var b=new ForgeConfigSpec.Builder();b.push("world_wrap");
  ENABLED=b.comment("Experimental physical wrap, Overworld only. Disabled until manual test-world acceptance.",
   "Requires active verified Earth protection; loads only existing complete destination data. Desired Earth interval [-12288,12288).")
   .define("enabled",false);
  LOG_CROSSINGS=b.comment("Log successful crossings. Deferred/failing groups are rate-limited independently.").define("log_crossings",true);
  ARRIVAL_INSET=b.comment("Explicit optional inward arrival fallback, in blocks. 0 preserves the exact seam target; 32 can avoid missing edge dependencies. Actual adjustment is reported.").defineInRange("arrival_inset_blocks",0,0,64);
  b.pop();b.push("earth_protection");
  PROTECTION=b.comment("Existing imported Overworld only. Independently contains players and denies ALL missing/incomplete chunk generation, including interior holes. Requires restart. Never enable in a blank world.").define("enabled",false);
  b.pop();SPEC=b.build();
 }
 private WrapConfig(){}
}
