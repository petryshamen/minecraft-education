package org.humanjourney.core.integration.xaero.mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.GuiGraphics;
import org.humanjourney.core.integration.xaero.client.Xaero13913Adapter;
/** Additive render-only hook, before native controls/dropdowns. No input or terrain hooks. */
@Pseudo @Mixin(targets="xaero.map.gui.GuiMap",remap=false)
public class GuiMapMixin {
 @Inject(method="renderPreDropdown",at=@At("HEAD"),require=0)
 private void hjGeography(GuiGraphics graphics,int mouseX,int mouseY,float partial,CallbackInfo ci){try{Xaero13913Adapter.render((xaero.map.gui.GuiMap)(Object)this,graphics,mouseX,mouseY);}catch(RuntimeException|LinkageError error){org.humanjourney.core.integration.xaero.XaeroStatus.disable(error);}}
}
