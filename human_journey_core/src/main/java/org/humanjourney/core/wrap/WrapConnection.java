package org.humanjourney.core.wrap;
import net.minecraft.world.entity.Entity;
/** Implemented only by the exact-version common-side connection Mixin. */
public interface WrapConnection {
 boolean hjWrap$pending();
 void hjWrap$resetVehicle(Entity root);
}
