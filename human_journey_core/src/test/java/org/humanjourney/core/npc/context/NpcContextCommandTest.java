package org.humanjourney.core.npc.context;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.humanjourney.core.npc.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.nio.file.*;
import java.util.*;
class NpcContextCommandTest {
    @AfterEach void reset(){NpcContextStore.reset();NpcTemplates.reset();}
    @Test void realBrigadierSuggestionsSwitchWithEraAndRetainStory()throws Exception{
        Path root=Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-context-datapack/data/human_journey");
        var config=new NpcContextStore.Source("human_journey:config","test",Files.readString(root.resolve("core/npc_context.json")));List<NpcContextStore.Source> roles=new ArrayList<>();for(String id:new String[]{"hunter","fisher"})roles.add(new NpcContextStore.Source("human_journey:"+id,"test",Files.readString(root.resolve("npc_archetype/"+id+".json"))));
        NpcContextStore.accept(NpcContextStore.prepare(config,List.of(),roles,id->64));NpcTemplates.accept(NpcTemplates.prepare(List.of(new NpcTemplates.Source("human_journey:story_test","test",Files.readString(root.resolve("npc/story_test.json")))),id->64));
        var teacher=mock(CommandSourceStack.class);when(teacher.hasPermission(2)).thenReturn(true);var dispatcher=new CommandDispatcher<CommandSourceStack>();NpcCommands.register(new RegisterCommandsEvent(dispatcher,Commands.CommandSelection.ALL,mock(CommandBuildContext.class)));
        try(var runtime=mockStatic(NpcContextRuntime.class,CALLS_REAL_METHODS)){
            runtime.when(()->NpcContextRuntime.current(teacher)).thenReturn(new NpcContextRuntime.Resolution(new NpcContext("era_0","none","none","none","none"),"","fixture"));
            var era0=dispatcher.getCompletionSuggestions(dispatcher.parse("hj npc spawn ",teacher)).get().getList().stream().map(s->s.getText()).toList();assertTrue(era0.containsAll(List.of("hunter","fisher","story_test")));
            runtime.when(()->NpcContextRuntime.current(teacher)).thenReturn(new NpcContextRuntime.Resolution(new NpcContext("era_1","none","none","none","none"),"","fixture"));
            var era1=dispatcher.getCompletionSuggestions(dispatcher.parse("hj npc spawn ",teacher)).get().getList().stream().map(s->s.getText()).toList();assertFalse(era1.contains("hunter"));assertFalse(era1.contains("fisher"));assertTrue(era1.contains("story_test"));
        }
        assertNotNull(dispatcher.getRoot().getChild("hj").getChild("npc").getChild("context"));assertFalse(dispatcher.getRoot().getChild("hj").getChild("npc").canUse(mock(CommandSourceStack.class)));
    }
}
