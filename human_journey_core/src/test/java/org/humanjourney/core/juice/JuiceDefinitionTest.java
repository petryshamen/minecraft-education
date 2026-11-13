package org.humanjourney.core.juice;
import com.google.gson.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class JuiceDefinitionTest {
 final String json="{\"schema_version\":1,\"id\":\"fixture\",\"bottle_item\":\"fixture:drink\",\"ingredient\":{\"item\":\"fixture:material\"},\"canteen_display_name\":\"Fixture Juice Canteen\",\"heal_per_drink\":1.0}";
 final JuiceDefinition.References refs=new JuiceDefinition.References(){public boolean bottleSupported(String id){return id.equals("fixture:drink")||id.equals("fixture:other_drink");}public Set<String> ingredient(String item,String tag){return item!=null?(item.equals("fixture:material")?Set.of(item):Set.of()):tag.equals("fixture:materials")?Set.of("fixture:material","fixture:other_material"):Set.of();}};
 JsonObject data(){return JsonParser.parseString(json).getAsJsonObject();}
 JuiceDefinition parse(JsonElement e){return JuiceDefinition.parse(e,refs);}
 @AfterEach void reset(){JuiceStore.reset();}
 @Test void validDefinitionLoads(){var d=parse(data());assertEquals("fixture",d.id());assertEquals(1,d.healPerDrink());assertEquals("Fixture Juice Canteen",d.canteenDisplayName());}
 @Test void tagIngredientLoads(){var d=data();d.add("ingredient",JsonParser.parseString("{\"tag\":\"fixture:materials\"}"));assertEquals(2,parse(d).ingredient().members().size());}
 @Test void duplicateIdRejected(){var d=parse(data());assertThrows(IllegalArgumentException.class,()->JuiceCatalog.of(List.of(d,d)));}
 @Test void unknownBottleRejected(){var d=data();d.addProperty("bottle_item","fixture:missing");assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void emptyIdRejected(){var d=data();d.addProperty("id","");assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void invalidLogicalIdRejected(){var d=data();d.addProperty("id","Bad ID");assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void invalidIngredientRejected(){for(String v:List.of("{}","{\"item\":\"fixture:unknown\"}","{\"tag\":\"fixture:empty\"}","{\"item\":\"fixture:material\",\"tag\":\"fixture:materials\"}","\"fixture:material\"")){var d=data();d.add("ingredient",JsonParser.parseString(v));assertThrows(IllegalArgumentException.class,()->parse(d));}}
 @Test void negativeHealingRejected(){var d=data();d.addProperty("heal_per_drink",-.1);assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void nonFiniteHealingRejected(){for(double x:new double[]{Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY}){var d=data();d.addProperty("heal_per_drink",x);assertThrows(IllegalArgumentException.class,()->parse(d));}}
 @Test void zeroAndLargeFiniteHealingSupported(){for(double x:new double[]{0,1,1e300}){var d=data();d.addProperty("heal_per_drink",x);assertEquals(x,parse(d).healPerDrink());}}
 @Test void stringHealingRejected(){var d=data();d.addProperty("heal_per_drink","1");assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void missingDisplayNameRejected(){var d=data();d.remove("canteen_display_name");assertThrows(IllegalArgumentException.class,()->parse(d));d.addProperty("canteen_display_name"," ");assertThrows(IllegalArgumentException.class,()->parse(d));}
 @Test void invalidSchemaAndUnknownFieldsRejected(){var d=data();d.addProperty("schema_version",2);assertThrows(IllegalArgumentException.class,()->parse(d));var b=data();b.addProperty("extra",true);assertThrows(IllegalArgumentException.class,()->parse(b));}
 @Test void duplicateBottleRejected(){var a=parse(data());var b=data();b.addProperty("id","other");assertThrows(IllegalArgumentException.class,()->JuiceCatalog.of(List.of(a,parse(b))));}
 @Test void ambiguousIngredientsRejected(){var a=parse(data());var b=data();b.addProperty("id","other");b.addProperty("bottle_item","fixture:other_drink");assertThrows(IllegalArgumentException.class,()->JuiceCatalog.of(List.of(a,parse(b))));}
 @Test void rejectedReloadRetainsLastValidCatalog(){var c=JuiceCatalog.of(List.of(parse(data())));JuiceStore.accept(c,"fixture");long revision=JuiceStore.state().revision();JuiceStore.reject("bad reload");assertSame(c,JuiceStore.state().catalog());assertEquals(revision,JuiceStore.state().revision());assertEquals("bad reload",JuiceStore.state().error());}
 private net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier barrier(){return new net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier(){public <T>java.util.concurrent.CompletableFuture<T> wait(T value){return java.util.concurrent.CompletableFuture.completedFuture(value);}};}
 @Test void absentResourcesDeactivateCatalog(){JuiceStore.accept(JuiceCatalog.of(List.of(parse(data()))),"old");var manager=mock(net.minecraft.server.packs.resources.ResourceManager.class);when(manager.listResources(anyString(),any())).thenReturn(Map.of());new JuiceReloadListener(mock(net.minecraft.core.HolderLookup.Provider.class)).reload(barrier(),manager,Runnable::run,Runnable::run).join();assertTrue(JuiceStore.state().catalog().definitions().isEmpty());assertEquals("absent",JuiceStore.state().source());}
 @Test void actualInvalidReloadReportsResourceAndKeepsCatalog()throws Exception{var c=JuiceCatalog.of(List.of(parse(data())));JuiceStore.accept(c,"old");var manager=mock(net.minecraft.server.packs.resources.ResourceManager.class);var resource=mock(net.minecraft.server.packs.resources.Resource.class);when(resource.openAsReader()).thenReturn(new java.io.BufferedReader(new java.io.StringReader("[]")));when(manager.listResources(anyString(),any())).thenReturn(Map.of(net.minecraft.resources.ResourceLocation.parse("human_journey:core/juice/bad.json"),resource));new JuiceReloadListener(mock(net.minecraft.core.HolderLookup.Provider.class)).reload(barrier(),manager,Runnable::run,Runnable::run).join();assertSame(c,JuiceStore.state().catalog());assertTrue(JuiceStore.state().error().contains("bad.json"));}
}
