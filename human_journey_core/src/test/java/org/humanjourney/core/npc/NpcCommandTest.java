package org.humanjourney.core.npc;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.*;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import java.nio.file.*;
class NpcCommandTest {
    @AfterEach void reset(){NpcTemplates.reset();}
    @Test void liveBrigadierSuggestionsFollowReloadedSetAndPermission()throws Exception{
        String json=Files.readString(Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-datapack/data/human_journey/npc/hunter.json"));
        NpcTemplates.accept(NpcTemplates.prepare(List.of(new NpcTemplates.Source("human_journey:hunter","test",json)),id->64));
        var dispatcher=new CommandDispatcher<CommandSourceStack>();NpcCommands.register(new RegisterCommandsEvent(dispatcher,Commands.CommandSelection.ALL,mock(CommandBuildContext.class)));
        var teacher=mock(CommandSourceStack.class);when(teacher.hasPermission(2)).thenReturn(true);
        var suggestions=dispatcher.getCompletionSuggestions(dispatcher.parse("hj npc spawn h",teacher)).get().getList().stream().map(s->s.getText()).toList();
        assertTrue(suggestions.contains("hunter"));assertTrue(suggestions.contains("human_journey:hunter"));
        var student=mock(CommandSourceStack.class);assertFalse(dispatcher.getRoot().getChild("hj").getChild("npc").canUse(student));assertTrue(dispatcher.getRoot().getChild("hj").getChild("npc").canUse(teacher));
        NpcTemplates.accept(NpcTemplates.prepare(List.of(),id->64));assertTrue(dispatcher.getCompletionSuggestions(dispatcher.parse("hj npc spawn h",teacher)).get().getList().isEmpty());
    }
}
