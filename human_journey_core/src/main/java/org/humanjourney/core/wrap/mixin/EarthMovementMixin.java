package org.humanjourney.core.wrap.mixin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.humanjourney.core.wrap.safety.SafetyRuntime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(Entity.class)
public abstract class EarthMovementMixin {
 @ModifyVariable(method="move",at=@At("HEAD"),argsOnly=true,ordinal=0)
 private Vec3 hjEarth$containMotion(Vec3 motion){return SafetyRuntime.limit((Entity)(Object)this,motion);}
}
