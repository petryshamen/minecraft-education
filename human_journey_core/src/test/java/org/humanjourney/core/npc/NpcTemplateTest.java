package org.humanjourney.core.npc;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
class NpcTemplateTest {
    JsonObject hunter()throws Exception{return JsonParser.parseString(Files.readString(Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-datapack/data/human_journey/npc/hunter.json"))).getAsJsonObject();}
    NpcTemplate parse(JsonObject json){return NpcTemplate.parse("human_journey:hunter",json);}
    @AfterEach void reset(){NpcTemplates.reset();}
    @Test void hunterContentAndTradeCounts()throws Exception{var t=parse(hunter());assertEquals("Kito — Hunter",t.displayName(null));assertEquals("Ada — Hunter",t.displayName("Ada"));assertTrue(t.stationary());assertTrue(t.invulnerable());assertEquals(2,t.trades().size());assertEquals(0,t.trades().getFirst().uses());assertEquals(4,t.trades().getFirst().maxUses());assertNotNull(t.trades().get(1).second());assertEquals(0,t.resetMinutes());}
    @Test void invalidSkinAndUnknownFieldsReject()throws Exception{for(String url:List.of("file:///test.png","not a URL","https://user:secret@example.org/test.png")){var j=hunter();j.addProperty("skin_url",url);assertThrows(IllegalArgumentException.class,()->parse(j));}var j=hunter();j.addProperty("profession","hunter");assertThrows(IllegalArgumentException.class,()->parse(j));}
    @Test void invalidCountsUsesAndMaxReject()throws Exception{for(int value:new int[]{0,-1,1000001}){var j=hunter();j.getAsJsonObject("trading").getAsJsonArray("offers").get(0).getAsJsonObject().addProperty("maxUses",value);assertThrows(IllegalArgumentException.class,()->parse(j));}var j=hunter();j.getAsJsonObject("trading").getAsJsonArray("offers").get(0).getAsJsonObject().addProperty("uses",5);assertThrows(IllegalArgumentException.class,()->parse(j));}
    @Test void itemRegistryValidationRejectsUnknownAndOversized()throws Exception{var t=parse(hunter());assertThrows(IllegalArgumentException.class,()->t.validateItems(id->0));assertThrows(IllegalArgumentException.class,()->t.validateItems(id->1));assertDoesNotThrow(()->t.validateItems(id->64));}
    @Test void missingAndDuplicateDialogAndReferencesReject()throws Exception{
        var j=hunter();var page=j.getAsJsonObject("dialogue").getAsJsonArray("pages").get(0);j.getAsJsonObject("dialogue").getAsJsonArray("pages").add(page.deepCopy());assertThrows(IllegalArgumentException.class,()->parse(j));
        var bad=hunter();bad.getAsJsonObject("dialogue").addProperty("main","missing");assertThrows(IllegalArgumentException.class,()->parse(bad));
        var ref=hunter();var action=ref.getAsJsonObject("dialogue").getAsJsonArray("pages").get(0).getAsJsonObject().getAsJsonArray("buttons").get(0).getAsJsonObject().getAsJsonArray("actions").get(0).getAsJsonObject();action.addProperty("type","OPEN_NAMED_DIALOG");action.addProperty("dialogue","missing");assertThrows(IllegalArgumentException.class,()->parse(ref));
    }
    @Test void duplicateTemplateIdsAndDeclaredMismatchReject()throws Exception{String json=hunter().toString();var source=new NpcTemplates.Source("human_journey:hunter","test",json);assertThrows(IllegalArgumentException.class,()->NpcTemplates.prepare(List.of(source,source),id->64));var j=hunter();j.addProperty("id","human_journey:other");assertThrows(IllegalArgumentException.class,()->parse(j));}
    @Test void atomicReloadRetentionRemovalAndSuggestions()throws Exception{
        var source=new NpcTemplates.Source("human_journey:hunter","pack",hunter().toString());NpcTemplates.accept(NpcTemplates.prepare(List.of(source),id->64));assertEquals(List.of("human_journey:hunter","hunter"),NpcTemplates.suggestions());var old=NpcTemplates.state();NpcTemplates.reject("bad item");assertSame(old.templates().get("human_journey:hunter"),NpcTemplates.state().templates().get("human_journey:hunter"));assertEquals(old.revision(),NpcTemplates.state().revision());assertEquals(old.lastSuccess(),NpcTemplates.state().lastSuccess());NpcTemplates.accept(NpcTemplates.prepare(List.of(),id->64));assertTrue(NpcTemplates.suggestions().isEmpty());assertEquals("human_journey:hunter",NpcTemplates.resolve("hunter"));
    }
    @Test void freshIdentityAndLabelsAreIsolated(){Set<UUID> entities=new HashSet<>(),presets=new HashSet<>();Set<String> dialogs=new HashSet<>();for(int i=0;i<100;i++){var identity=NpcIdentity.fresh();assertTrue(entities.add(identity.entity()));assertTrue(presets.add(identity.preset()));assertNotEquals(identity.entity(),identity.preset());assertTrue(dialogs.add(identity.label("dialog","main")));assertEquals(32,identity.label("dialog","main").length());assertEquals(identity.label("dialog","main"),identity.label("dialog","main"));assertNotEquals(identity.label("dialog","main"),identity.label("button","main"));}}
    @Test void dependencyCheckIsExact(){assertFalse(NpcDependency.problem(false,"").isEmpty());assertFalse(NpcDependency.problem(true,"6.5.1").isEmpty());assertFalse(NpcDependency.problem(true,"7.0.0").isEmpty());assertEquals("",NpcDependency.problem(true,"6.5.2"));}
}
