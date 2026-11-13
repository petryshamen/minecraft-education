package org.humanjourney.core.geography;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.humanjourney.core.geography.client.GeographyClientConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Exercises Forge's real correction path; no client/world or user config file is created. */
class GeographyClientConfigTest {
 private final ForgeConfigSpec spec=GeographyClientConfig.SPEC;
 private ForgeConfigSpec.ValueSpec value(String name){return spec.getSpec().get("geography_hud."+name);}
 private void expected(CommentedConfig config,boolean enabled,int x,int y,double scale,double opacity){
  assertEquals(enabled,config.<Boolean>get("geography_hud.enabled"));
  assertEquals(x,config.<Number>get("geography_hud.x_offset").intValue());
  assertEquals(y,config.<Number>get("geography_hud.y_offset").intValue());
  assertEquals(scale,config.<Number>get("geography_hud.scale").doubleValue());
  assertEquals(opacity,config.<Number>get("geography_hud.background_opacity").doubleValue());
 }
 @Test void approvedDefaultsAreExact(){
  assertEquals(true,GeographyClientConfig.ENABLED.getDefault());assertEquals(85,GeographyClientConfig.X.getDefault());assertEquals(10,GeographyClientConfig.Y.getDefault());assertEquals(.8,GeographyClientConfig.SCALE.getDefault());assertEquals(.35,GeographyClientConfig.OPACITY.getDefault());
 }
 @Test void freshConfigReceivesApprovedDefaults(){var c=CommentedConfig.inMemory();spec.correct(c);expected(c,true,85,10,.8,.35);assertTrue(spec.isCorrect(c));}
 @Test void existingValidCustomValuesRemainRespected(){var c=CommentedConfig.inMemory();c.set("geography_hud.enabled",false);c.set("geography_hud.x_offset",230);c.set("geography_hud.y_offset",27);c.set("geography_hud.scale",1.5);c.set("geography_hud.background_opacity",.7);spec.correct(c);expected(c,false,230,27,1.5,.7);assertTrue(spec.isCorrect(c));}
 @Test void missingSettingGetsNewDefaultWithoutReplacingOldDefaults(){var c=CommentedConfig.inMemory();c.set("geography_hud.enabled",true);c.set("geography_hud.x_offset",170);c.set("geography_hud.y_offset",10);c.set("geography_hud.scale",1.0);spec.correct(c);expected(c,true,170,10,1.0,.35);c.set("geography_hud.background_opacity",.55);spec.correct(c);expected(c,true,170,10,1.0,.55);}
 @Test void allPreviousRangesRemainUnchanged(){for(String name:new String[]{"x_offset","y_offset"}){var v=value(name);assertTrue(v.test(-8192));assertTrue(v.test(8192));assertFalse(v.test(-8193));assertFalse(v.test(8193));}var scale=value("scale");assertTrue(scale.test(.5));assertTrue(scale.test(3.0));assertFalse(scale.test(.49));assertFalse(scale.test(3.01));var opacity=value("background_opacity");assertTrue(opacity.test(0.0));assertTrue(opacity.test(1.0));assertFalse(opacity.test(-.01));assertFalse(opacity.test(1.01));}
}
