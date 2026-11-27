package org.humanjourney.core.wrap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class WrapMathTest {
 @Test void east(){assertEquals(-12288,WrapMath.canonical(12288));}
 @Test void west(){assertEquals(12287.75,WrapMath.canonical(-12288.25));}
 @Test void fraction(){assertEquals(-12287.875,WrapMath.canonical(12288.125));}
 @Test void westIsIncluded(){assertFalse(WrapMath.outside(-12288));assertEquals(-12288,WrapMath.canonical(-12288));}
 @Test void eastIsExcluded(){assertTrue(WrapMath.outside(12288));}
 @Test void immediateReturn(){double arrived=WrapMath.canonical(12288.125);assertEquals(12287.875,WrapMath.canonical(arrived-0.25));}
 @Test void noStationaryOscillation(){double x=WrapMath.canonical(12288);assertFalse(WrapMath.outside(x));assertEquals(x,WrapMath.canonical(x));}
 @Test void fastMovementMultiplePeriods(){assertEquals(-12287.5,WrapMath.canonical(12288.5+3*24576));}
 @Test void negativeMultiplePeriods(){assertEquals(12287.5,WrapMath.canonical(-12288.5-3*24576));}
 @Test void invalidNumbers(){for(double x:new double[]{Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,30_000_001})assertThrows(IllegalArgumentException.class,()->WrapMath.canonical(x));}
 @Test void allLatitudesDoNotAffectLongitude(){for(int z:new int[]{-6144,-3000,0,3000,6144})assertEquals(-12287.5,WrapMath.canonical(12288.5));}
 @Test void identityInside(){for(double x:new double[]{-12000.25,-1,0,0.125,12287.999})assertEquals(x,WrapMath.canonical(x));}
 @Test void closedOverCanonicalRange(){for(int i=-100000;i<100000;i+=97){double x=WrapMath.canonical(i+0.125);assertTrue(x>=-12288&&x<12288);assertEquals(0,(i+0.125-x)%24576);}}
 @Test void exactVersionOnly(){assertTrue(WrapMixinPlugin.supported("1.21.8","58.1.18"));assertFalse(WrapMixinPlugin.supported("1.21.9","58.1.18"));assertFalse(WrapMixinPlugin.supported("1.21.8","58.1.19"));}
}
