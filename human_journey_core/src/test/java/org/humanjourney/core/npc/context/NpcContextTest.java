package org.humanjourney.core.npc.context;
import com.google.gson.*;
import org.humanjourney.core.npc.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
class NpcContextTest {
    Path example(){return Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-context-datapack/data/human_journey");}
    String config()throws Exception{return Files.readString(example().resolve("core/npc_context.json"));}
    JsonObject role(String id)throws Exception{return JsonParser.parseString(Files.readString(example().resolve("npc_archetype/"+id+".json"))).getAsJsonObject();}
    NpcContextStore.Source source(String id,String json){return new NpcContextStore.Source(id,"test",json);}
    NpcContextStore.Data data(List<NpcContextStore.Source> geo,List<NpcContextStore.Source> roles)throws Exception{return NpcContextStore.prepare(source("human_journey:core/npc_context",config()),geo,roles,id->64);}
    NpcContextStore.Data examples()throws Exception{return data(List.of(),List.of(source("human_journey:hunter",role("hunter").toString()),source("human_journey:fisher",role("fisher").toString())));}
    NpcContext context(String era,String season){return new NpcContext(era,season,"none","none","none");}
    String geography(){return """
        {"schema_version":1,"macro_regions":[{"id":"test_macro","dimension":"minecraft:overworld","polygon":[[0,0],[10,0],[10,10],[0,10]]}],
         "local_areas":[{"id":"test_basin","macro_region":"test_macro","polygon":[[2,2],[7,2],[7,7],[2,7]],"display_label":"River","era_labels":{"era_0":"River Test","era_1":"Later Test"}}]}
        """;}
    @AfterEach void reset(){NpcContextStore.reset();NpcTemplates.reset();}
    @Test void binaryEraFlagMapsLogicalIdsAndUnknownNeverDefaults()throws Exception{
        var eras=EraRules.parse(JsonParser.parseString(config()));assertEquals("era_0",eras.resolve((o,h)->OptionalInt.of(0)));assertEquals("era_1",eras.resolve((o,h)->OptionalInt.of(1)));
        assertThrows(IllegalArgumentException.class,()->eras.resolve((o,h)->OptionalInt.of(2)));assertThrows(IllegalArgumentException.class,()->eras.resolve((o,h)->OptionalInt.empty()));
    }
    @Test void futureEraUsesIndependentConditionsAndExplicitPriority(){
        var rules=EraRules.parse(JsonParser.parseString("""
        {"eras":[{"id":"era_1","conditions":[{"objective":"hj_civ","holder":"#era1","operator":"EQUALS","value":1}]},
        {"id":"bronze_phase","priority":10,"conditions":[{"objective":"hj_future","holder":"#bronze","operator":"GREATER_THAN_OR_EQUALS","value":4},{"objective":"hj_other","holder":"#ready","operator":"EQUALS","value":9}]}]}
        """));assertEquals("bronze_phase",rules.resolve((o,h)->OptionalInt.of(o.equals("hj_civ")?1:o.equals("hj_future")?4:9)));
    }
    @Test void ambiguousEraFailsAndMalformedResolverRejects(){
        var rules=EraRules.parse(JsonParser.parseString("{\"eras\":[{\"id\":\"a\",\"conditions\":[{\"objective\":\"x\",\"holder\":\"#a\",\"operator\":\"EQUALS\",\"value\":0}]},{\"id\":\"b\",\"conditions\":[{\"objective\":\"x\",\"holder\":\"#a\",\"operator\":\"EQUALS\",\"value\":0}]}]}"));
        assertThrows(IllegalArgumentException.class,()->rules.resolve((o,h)->OptionalInt.of(0)));
        for(String bad:List.of("{}","{\"eras\":[]}","{\"eras\":[{\"id\":\"era_0\",\"conditions\":[]}]}"))assertThrows(IllegalArgumentException.class,()->EraRules.parse(JsonParser.parseString(bad)));
    }
    @Test void eraSpecificDiscoveryAndUnavailableRoleDoNotFallback()throws Exception{
        var data=examples();var suggestions=NpcContextRuntime.suggestions(data,NpcTemplates.state(),"era_0");assertTrue(suggestions.containsAll(List.of("hunter","fisher")));assertTrue(NpcContextRuntime.suggestions(data,NpcTemplates.state(),"era_1").isEmpty());
        var error=assertThrows(IllegalArgumentException.class,()->data.archetypes().get("human_journey:hunter").compose(context("era_1","none"),id->64));assertEquals("Hunter is not available in era_1.",error.getMessage());
    }
    @Test void absentGeographyWorksAndGenericNamesDoNotUsePersonalNames()throws Exception{
        var data=examples();var resolved=NpcContextRuntime.resolve(data,(o,h)->OptionalInt.of(0),"minecraft:overworld",3000,-900,new NpcSeasons.Result("none","absent"));assertEquals("none",resolved.context().macroRegion());assertEquals("none",resolved.context().localArea());assertEquals("none",resolved.context().locationLabel());
        assertEquals("Hunter",data.archetypes().get("human_journey:hunter").compose(resolved.context(),id->64).displayName(null));assertEquals("Fisher",data.archetypes().get("human_journey:fisher").compose(resolved.context(),id->64).name());
    }
    @Test void polygonHierarchyDimensionAndEraLabels()throws Exception{
        var geo=NpcGeography.parse(List.of(JsonParser.parseString(geography())),Set.of("era_0","era_1"));var a=geo.resolve("minecraft:overworld",3,3,"era_0");assertEquals("test_macro",a.macro());assertEquals("test_basin",a.local());assertEquals("River Test",a.label());assertEquals("Later Test",geo.resolve("minecraft:overworld",3,3,"era_1").label());assertEquals("River",geo.resolve("minecraft:overworld",3,3,"later").label());assertEquals("none",geo.resolve("minecraft:the_nether",3,3,"era_0").local());assertEquals("none",geo.resolve("minecraft:overworld",20,20,"era_0").macro());
        var data=data(List.of(source("human_journey:geo",geography())),List.of(source("human_journey:fisher",role("fisher").toString())));assertEquals("River Test Fisher",data.archetypes().get("human_journey:fisher").compose(NpcContext.of("era_0","summer",a),id->64).name());
        assertEquals("Fisher",data.archetypes().get("human_journey:fisher").compose(new NpcContext("era_0","summer","test_macro","none","none"),id->64).name());
    }
    @Test void malformedAndDuplicateGeographyReject(){
        var bad=JsonParser.parseString(geography()).getAsJsonObject();bad.getAsJsonArray("local_areas").get(0).getAsJsonObject().addProperty("macro_region","missing");assertThrows(IllegalArgumentException.class,()->NpcGeography.parse(List.of(bad),Set.of("era_0","era_1")));
        assertThrows(IllegalArgumentException.class,()->NpcGeography.parse(List.of(JsonParser.parseString(geography()),JsonParser.parseString(geography())),Set.of("era_0","era_1")));
        var shape=JsonParser.parseString(geography()).getAsJsonObject();shape.getAsJsonArray("macro_regions").get(0).getAsJsonObject().add("polygon",JsonParser.parseString("[[0,0],[10,10],[10,0],[0,10]]"));assertThrows(IllegalArgumentException.class,()->NpcGeography.parse(List.of(shape),Set.of("era_0","era_1")));
    }
    @Test void equalPriorityLocationAmbiguityFailsAtResolvedPosition(){
        var json=JsonParser.parseString(geography()).getAsJsonObject();var duplicate=json.getAsJsonArray("macro_regions").get(0).deepCopy().getAsJsonObject();duplicate.addProperty("id","second");json.getAsJsonArray("macro_regions").add(duplicate);
        var geo=NpcGeography.parse(List.of(json),Set.of("era_0","era_1"));assertThrows(IllegalArgumentException.class,()->geo.resolve("minecraft:overworld",1,1,"era_0"));duplicate.addProperty("priority",10);assertEquals("second",NpcGeography.parse(List.of(json),Set.of("era_0","era_1")).resolve("minecraft:overworld",1,1,"era_0").macro());
    }
    @Test void recursiveMergeReplacesScalarsAndWholeArrays(){
        var lower=JsonParser.parseString("{\"x\":1,\"nested\":{\"a\":1,\"b\":2},\"array\":[1,2]}").getAsJsonObject();var higher=JsonParser.parseString("{\"x\":3,\"nested\":{\"a\":4},\"array\":[9]}").getAsJsonObject();var result=NpcArchetype.merge(lower,higher);assertEquals(3,result.get("x").getAsInt());assertEquals(2,result.getAsJsonObject("nested").get("b").getAsInt());assertEquals(4,result.getAsJsonObject("nested").get("a").getAsInt());assertEquals("[9]",result.getAsJsonArray("array").toString());assertEquals(1,lower.get("x").getAsInt());
    }
    @Test void exactLayerOrderAndSeasonOverrideSelection()throws Exception{
        var definition=role("hunter");var e=definition.getAsJsonObject("eras").getAsJsonObject("era_0");e.add("content",JsonParser.parseString("{\"role_label\":\"Era Hunter\"}"));e.add("macro_regions",JsonParser.parseString("{\"test_macro\":{\"role_label\":\"Macro Hunter\"}}"));e.add("local_areas",JsonParser.parseString("{\"test_basin\":{\"role_label\":\"Local Hunter\"}}"));e.add("seasons",JsonParser.parseString("{\"winter\":{\"role_label\":\"Winter Hunter\",\"trading\":{\"reset_minutes\":7}},\"none\":{\"role_label\":\"Fallback Hunter\"}}"));
        var data=data(List.of(source("human_journey:geo",geography())),List.of(source("human_journey:hunter",definition.toString())));var role=data.archetypes().get("human_journey:hunter");
        assertEquals("Era Hunter",role.compose(context("era_0","summer"),id->64).name());assertEquals("Macro Hunter",role.compose(new NpcContext("era_0","summer","test_macro","none","none"),id->64).name());assertEquals("River Local Hunter",role.compose(new NpcContext("era_0","summer","test_macro","test_basin","River"),id->64).name());
        var winter=role.compose(new NpcContext("era_0","winter","test_macro","test_basin","River"),id->64);assertEquals("River Winter Hunter",winter.name());assertEquals(7,winter.resetMinutes());assertEquals(2,winter.trades().size());assertEquals("Fallback Hunter",role.compose(context("era_0","none"),id->64).name());
    }
    @Test void invalidUnusedSeasonItemAndUnknownOverridesRejectWholeSet()throws Exception{
        var definition=role("hunter");definition.getAsJsonObject("eras").getAsJsonObject("era_0").add("seasons",JsonParser.parseString("{\"winter\":{\"trading\":{\"offers\":[{\"input\":{\"item\":\"minecraft:missing\"},\"output\":{\"item\":\"minecraft:stick\"},\"maxUses\":1}]}}}"));
        assertThrows(IllegalArgumentException.class,()->NpcContextStore.prepare(source("human_journey:config",config()),List.of(),List.of(source("human_journey:hunter",definition.toString())),id->id.endsWith(":missing")?0:64));
        var unknown=role("hunter");unknown.getAsJsonObject("eras").getAsJsonObject("era_0").add("macro_regions",JsonParser.parseString("{\"missing\":{}}"));assertThrows(IllegalArgumentException.class,()->data(List.of(),List.of(source("human_journey:hunter",unknown.toString()))));
        var skin=role("hunter");skin.getAsJsonObject("base").addProperty("skin_url","file:///invalid.png");assertThrows(IllegalArgumentException.class,()->data(List.of(),List.of(source("human_journey:hunter",skin.toString()))));
    }
    @Test void contextReloadIsAtomicAndStaticStoryStillAvailable()throws Exception{
        var data=examples();NpcContextStore.accept(data);var old=NpcContextStore.state();NpcContextStore.reject("invalid patch");assertSame(old.data(),NpcContextStore.state().data());assertEquals(old.revision(),NpcContextStore.state().revision());assertEquals(old.lastSuccess(),NpcContextStore.state().lastSuccess());
        String json=Files.readString(example().resolve("npc/story_test.json"));NpcTemplates.accept(NpcTemplates.prepare(List.of(new NpcTemplates.Source("human_journey:story_test","test",json)),id->64));assertTrue(NpcContextRuntime.suggestions(data,NpcTemplates.state(),"era_1").contains("story_test"));assertEquals("Story Test",NpcTemplates.state().templates().get("human_journey:story_test").name());
        NpcContextStore.accept(NpcContextStore.prepare(null,List.of(),List.of(),id->64));assertTrue(NpcContextStore.state().data().archetypes().isEmpty());assertTrue(NpcContextRuntime.suggestions(NpcContextStore.state().data(),NpcTemplates.state(),"unknown").contains("story_test"));
    }
    @Test void sameIdStaticTemplateCannotBypassEraAvailability()throws Exception{
        var data=examples();String old=Files.readString(Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-datapack/data/human_journey/npc/hunter.json"));NpcTemplates.accept(NpcTemplates.prepare(List.of(new NpcTemplates.Source("human_journey:hunter","old",old)),id->64));assertFalse(NpcContextRuntime.suggestions(data,NpcTemplates.state(),"era_1").contains("hunter"));
    }
}
