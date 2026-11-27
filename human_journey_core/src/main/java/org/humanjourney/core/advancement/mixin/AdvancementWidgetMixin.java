package org.humanjourney.core.advancement.mixin;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.advancements.*;
import org.humanjourney.core.advancement.client.NativeWidgetAccess;
@Mixin(value=AdvancementWidget.class,remap=false)
public abstract class AdvancementWidgetMixin implements NativeWidgetAccess {
 @Shadow @Final @Mutable private int x;
 @Shadow @Final @Mutable private int y;
 @Shadow @Final private AdvancementNode advancementNode;
 @Shadow @Final private DisplayInfo display;
 @Shadow private AdvancementProgress progress;
 @Shadow private AdvancementWidget parent;
 public String hjId(){return advancementNode.holder().id().toString();}
 public String hjParent(){return parent==null?"":((NativeWidgetAccess)parent).hjId();}
 public boolean hjDrawn(){return !display.isHidden()||progress!=null&&progress.isDone();}
 public int hjX(){return x;}public int hjY(){return y;}
 public void hjPosition(int nx,int ny){x=nx;y=ny;}
}
