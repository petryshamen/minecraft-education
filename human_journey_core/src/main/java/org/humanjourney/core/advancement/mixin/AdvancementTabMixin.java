package org.humanjourney.core.advancement.mixin;
import java.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.*;
import org.humanjourney.core.advancement.client.*;
@Mixin(value=AdvancementTab.class,remap=false)
public abstract class AdvancementTabMixin implements NativeTabAccess {
 @Shadow @Final private Map<AdvancementHolder,AdvancementWidget> widgets;
 @Shadow private double scrollX,scrollY;
 @Shadow private int minX,minY,maxX,maxY;
 public Map<AdvancementHolder,AdvancementWidget> hjWidgets(){return Collections.unmodifiableMap(widgets);}
 public double hjScrollX(){return scrollX;}public double hjScrollY(){return scrollY;}
 public void hjScroll(double x,double y){scrollX=x;scrollY=y;}
 public void hjBounds(int a,int b,int c,int d){minX=a;minY=b;maxX=c;maxY=d;}
 @Inject(method="drawContents",at=@At("HEAD"),require=1)
 private void hjPrepare(GuiGraphics g,int x,int y,CallbackInfo ci){NativeAdapter.prepare((AdvancementTab)(Object)this);}
 @Inject(method="drawContents",at=@At(value="INVOKE",target="Lnet/minecraft/client/gui/screens/advancements/AdvancementWidget;drawConnectivity(Lnet/minecraft/client/gui/GuiGraphics;IIZ)V",ordinal=0),require=1)
 private void hjBehindNativeWidgets(GuiGraphics g,int x,int y,CallbackInfo ci){NativeAdapter.draw((AdvancementTab)(Object)this,g);}
}
