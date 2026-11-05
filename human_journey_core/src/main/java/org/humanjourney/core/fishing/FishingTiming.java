package org.humanjourney.core.fishing;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
public final class FishingTiming {
    public static int initialDraw(RandomSource random,int vanillaMin,int vanillaMax,int nativeLureTicks){
        var policy=FishingBalance.state().policy();
        if(policy==null)return Mth.nextInt(random,vanillaMin,vanillaMax);
        var bounds=policy.bounds(nativeLureTicks);
        int effective=Mth.nextInt(random,bounds.minimumTicks(),bounds.maximumTicks());
        // The immediately following vanilla ISUB still subtracts lureSpeed. Compensate once here.
        // Java int modular addition/subtraction also cancels safely for extreme custom effect values.
        return effective+nativeLureTicks;
    }
    private FishingTiming(){}
}
