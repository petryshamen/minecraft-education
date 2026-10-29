package org.humanjourney.core.water;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.util.*;
import org.humanjourney.core.water.WaterPolicy.Quality;
import static org.junit.jupiter.api.Assertions.*;
class SaltPolicyTest {
    WaterPolicy parse(String s){return WaterPolicy.parse(JsonParser.parseString(s));}
    WaterGeography.Region region(){return new WaterGeography.Region("caspian_sea","minecraft:overworld",Quality.SALT,List.of(new WaterGeography.Point(3404,-2758),new WaterGeography.Point(3433,-3041),new WaterGeography.Point(3537,-3208),new WaterGeography.Point(3564,-2497)));}
    Quality classify(boolean water,String dim,int x,int z,boolean purified,boolean dirty,Set<String> tags,List<WaterGeography.Region> regions){return WaterGeography.classify(water,dim,x,z,purified,dirty,tags::contains,new WaterExtensions.Classification(Set.of("minecraft:is_ocean"),regions));}
    @Test void biomePriorityOceanRiverSwampsAndPurified(){
        assertEquals(Quality.SALT,classify(true,"minecraft:overworld",0,0,false,false,Set.of("minecraft:is_ocean"),List.of()));
        assertEquals(Quality.NORMAL,classify(true,"minecraft:overworld",0,0,false,false,Set.of("minecraft:is_river"),List.of()));
        assertEquals(Quality.DIRTY,classify(true,"minecraft:overworld",0,0,false,true,Set.of("minecraft:is_ocean"),List.of()));
        assertEquals(Quality.PURIFIED,classify(true,"minecraft:overworld",0,0,true,true,Set.of("minecraft:is_ocean"),List.of()));
    }
    @Test void polygonOverridesRiverOnlyInCorrectDimensionAndWater(){
        assertEquals(Quality.SALT,classify(true,"minecraft:overworld",3490,-2900,false,false,Set.of(),List.of(region())));
        assertEquals(Quality.NORMAL,classify(true,"minecraft:the_nether",3490,-2900,false,false,Set.of(),List.of(region())));
        assertEquals(Quality.NORMAL,classify(true,"minecraft:overworld",3000,-2900,false,false,Set.of(),List.of(region())));
        assertNull(classify(false,"minecraft:overworld",3490,-2900,false,false,Set.of(),List.of(region())));
        assertTrue(region().contains(3404,-2758));assertFalse(region().contains(0,0));
    }
    @Test void firstMatchingRegionWinsAndConcavePolygonsWork(){
        var fresh=new WaterGeography.Region("fresh","minecraft:overworld",Quality.NORMAL,region().polygon());
        assertEquals(Quality.NORMAL,classify(true,"minecraft:overworld",3490,-2900,true,true,Set.of(),List.of(fresh,region())));
        var concave=new WaterGeography.Region("L","minecraft:overworld",Quality.SALT,List.of(new WaterGeography.Point(0,0),new WaterGeography.Point(4,0),new WaterGeography.Point(4,1),new WaterGeography.Point(1,1),new WaterGeography.Point(1,4),new WaterGeography.Point(0,4)));
        assertTrue(concave.contains(.5,3));assertFalse(concave.contains(3,3));
    }
    @Test void saltExcludedFromAllTreatmentPolicies(){
        assertFalse(WaterPolicy.canFilter(Quality.SALT));assertFalse(WaterPolicy.canBoil(Quality.SALT));
        var p=WaterExtensions.defaults().purifier();assertFalse(p.accepts(Quality.SALT));assertTrue(p.accepts(Quality.NORMAL));assertTrue(p.accepts(Quality.DIRTY));
        assertFalse(new WaterPurifier.Intake(true,true).usable());assertFalse(new WaterPurifier.Intake(false,false).usable());assertTrue(new WaterPurifier.Intake(true,false).usable());
    }
    @Test void extendedFieldsReloadWithoutChangingOldBalance(){
        var p=parse("{\"salt\":{\"thirst_restored\":-2,\"hydration_restored\":-1,\"additional_exhaustion\":20,\"nausea\":{\"enabled\":false}},\"purifier\":{\"filter_cost_per_fill\":250,\"allowed_source_types\":[\"dirty\"]},\"classification\":{\"salt_biome_tags\":[]}}");
        assertEquals(-2,p.extensions().salt().thirst());assertEquals(-1,p.extensions().salt().hydration());assertFalse(p.extensions().salt().nausea());assertEquals(250,p.extensions().purifier().cost());assertFalse(p.extensions().purifier().accepts(Quality.NORMAL));assertTrue(p.extensions().classification().saltTags().isEmpty());assertEquals(.6,p.normal().chance());assertEquals(500,p.campfireTicks());
    }
    @Test void invalidExtendedPolicyDoesNotReplaceLastGoodSnapshot(){
        WaterBalance.reset();WaterBalance.accept(parse("{\"purifier\":{\"filter_cost_per_fill\":250}}"),"test");var old=WaterBalance.state();
        for(String s:List.of("{\"purifier\":{\"allowed_source_types\":[\"salt\"]}}","{\"purifier\":{\"filter_cost_per_fill\":0}}","{\"salt\":{\"additional_exhaustion\":-1}}","{\"salt\":{\"nausea\":{\"amplifier\":5}}}","{\"classification\":{\"salt_biome_tags\":[\"bad TAG\"]}}","{\"classification\":{\"regions\":[{}]}}")){
            assertThrows(IllegalArgumentException.class,()->parse(s));WaterBalance.reject("test invalid");assertSame(old.policy(),WaterBalance.current());assertEquals(old.lastSuccess(),WaterBalance.state().lastSuccess());
        }WaterBalance.reset();
    }
    @Test void rejectsSelfCrossingDegenerateAndDuplicateVertices(){
        var a=new WaterGeography.Point(0,0);var b=new WaterGeography.Point(3,0);var c=new WaterGeography.Point(3,3);var d=new WaterGeography.Point(0,3);
        assertThrows(IllegalArgumentException.class,()->new WaterGeography.Region("x","minecraft:overworld",Quality.SALT,List.of(a,c,b,d)));
        assertThrows(IllegalArgumentException.class,()->new WaterGeography.Region("x","minecraft:overworld",Quality.SALT,List.of(a,b,a)));
        assertThrows(IllegalArgumentException.class,()->new WaterGeography.Region("x","minecraft:overworld",Quality.SALT,List.of(a,b,new WaterGeography.Point(6,0))));
    }
    @Test void filterUnitsAreChargedPerFillAndRemaindersCarry(){
        for(int capacity:new int[]{100,200,400,800,1600}){
            var plan=FilterCharge.plan(0,0,capacity,1,100);assertNotNull(plan);assertEquals(1,plan.consumed());assertEquals(capacity-100,plan.remaining());
            int fills=1,remaining=plan.remaining();while(remaining>=100){var p=FilterCharge.plan(remaining,capacity,0,0,100);assertEquals(0,p.consumed());remaining=p.remaining();fills++;}assertEquals(capacity/100,fills);assertEquals(0,remaining);
        }
        assertNull(FilterCharge.plan(50,100,100,1,200));
        var combined=FilterCharge.plan(50,100,100,2,200);assertEquals(2,combined.consumed());assertEquals(50,combined.remaining());
        assertNull(FilterCharge.plan(0,0,0,64,100));
    }
}
