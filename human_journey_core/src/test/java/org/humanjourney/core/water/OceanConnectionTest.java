package org.humanjourney.core.water;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import org.humanjourney.core.water.OceanConnection.Cell;
import org.humanjourney.core.water.WaterPolicy.Quality;
class OceanConnectionTest {
    final Cell origin=new Cell(0,64,0);
    @Test void shorelineReachesOceanThroughWater(){
        assertTrue(OceanConnection.reachesOcean(origin,16,p->p.y()==64&&p.z()==0&&p.x()>=0&&p.x()<=16,p->p.x()==16));
        assertFalse(OceanConnection.reachesOcean(origin,15,p->p.y()==64&&p.z()==0&&p.x()>=0&&p.x()<=16,p->p.x()==16));
    }
    @Test void landBarrierKeepsNearbyLakeFresh(){
        assertFalse(OceanConnection.reachesOcean(origin,16,p->p.y()==64&&p.z()==0&&p.x()>=0&&p.x()<=4&&p.x()!=2,p->p.x()==4));
    }
    @Test void diagonalContactIsNotConnection(){
        Set<Cell> water=Set.of(origin,new Cell(1,64,1));
        assertFalse(OceanConnection.reachesOcean(origin,16,water::contains,p->p.x()==1));
    }
    @Test void riverBendsAndVerticalWaterWorkButPathLengthCounts(){
        Set<Cell> water=Set.of(origin,new Cell(1,64,0),new Cell(1,64,1),new Cell(2,64,1),new Cell(2,65,1));
        assertTrue(OceanConnection.reachesOcean(origin,4,water::contains,p->p.y()==65));
        assertFalse(OceanConnection.reachesOcean(origin,3,water::contains,p->p.y()==65));
    }
    @Test void zeroRadiusDisablesSearchAndDryOriginDoesNotSearch(){
        AtomicInteger checks=new AtomicInteger();
        assertFalse(OceanConnection.reachesOcean(origin,0,p->{checks.incrementAndGet();return true;},p->true));assertEquals(0,checks.get());
        assertFalse(OceanConnection.reachesOcean(origin,16,p->false,p->{fail("ocean lookup on dry block");return true;}));
    }
    @Test void fullyWetSearchHasStrictWorkBound(){
        AtomicInteger water=new AtomicInteger(),biomes=new AtomicInteger();
        assertFalse(OceanConnection.reachesOcean(origin,16,p->{water.incrementAndGet();return true;},p->{biomes.incrementAndGet();return false;}));
        assertEquals(6017,water.get());assertEquals(6017,biomes.get());
        assertThrows(IllegalArgumentException.class,()->OceanConnection.reachesOcean(origin,17,p->true,p->false));
    }
    @Test void geographicAndTanOverridesBypassConnectionSearch(){
        var region=new WaterGeography.Region("fresh","minecraft:overworld",Quality.NORMAL,List.of(new WaterGeography.Point(-1,-1),new WaterGeography.Point(1,-1),new WaterGeography.Point(1,1),new WaterGeography.Point(-1,1)));
        var rules=new WaterExtensions.Classification(Set.of("minecraft:is_ocean"),List.of(region));
        assertEquals(Quality.NORMAL,WaterGeography.classify(true,"minecraft:overworld",0,0,false,false,t->true,rules,()->{fail("explicit freshwater override must win");return true;}));
        var noRegions=WaterExtensions.defaults().classification();
        for(Quality q:new Quality[]{Quality.DIRTY,Quality.PURIFIED})assertEquals(q,WaterGeography.classify(true,"minecraft:overworld",0,0,q==Quality.PURIFIED,q==Quality.DIRTY,t->false,noRegions,()->{fail("TAN water classification must win");return true;}));
        assertEquals(Quality.SALT,WaterGeography.classify(true,"minecraft:overworld",0,0,false,false,t->true,noRegions,()->{fail("direct ocean must bypass search");return false;}));
    }
    @Test void connectedNormalBecomesSaltWithoutChangingPurifierRules(){
        var ext=WaterExtensions.defaults();
        var q=WaterGeography.classify(true,"minecraft:overworld",0,0,false,false,t->false,ext.classification(),()->true);
        assertEquals(Quality.SALT,q);assertFalse(ext.purifier().accepts(q));assertTrue(ext.purifier().accepts(Quality.NORMAL));
        assertEquals(Quality.NORMAL,WaterGeography.classify(true,"minecraft:overworld",0,0,false,false,t->false,ext.classification(),()->false));
    }
    @Test void radiusIsReloadableAndOldPolicyStillWorks(){
        assertEquals(16,WaterPolicy.parse(JsonParser.parseString("{}")).extensions().classification().oceanConnectionRadius());
        var changed=WaterPolicy.parse(JsonParser.parseString("{\"classification\":{\"ocean_connection_radius\":4}}"));
        WaterBalance.accept(changed,"test");assertEquals(4,WaterBalance.current().extensions().classification().oceanConnectionRadius());
        for(String bad:List.of("-1","17","1.5","\"16\""))assertThrows(IllegalArgumentException.class,()->WaterPolicy.parse(JsonParser.parseString("{\"classification\":{\"ocean_connection_radius\":"+bad+"}}")));
        var old=WaterBalance.current();WaterBalance.reject("bad radius");assertSame(old,WaterBalance.current());WaterBalance.reset();
    }
}
