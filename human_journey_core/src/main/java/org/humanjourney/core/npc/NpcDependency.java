package org.humanjourney.core.npc;
import net.minecraftforge.fml.ModList;
public final class NpcDependency {
    public static String problem(boolean installed,String version){return !installed?"Easy NPC dependency unavailable (requires 6.5.2)":!"6.5.2".equals(version)?"Unsupported Easy NPC version "+version+"; adapter requires 6.5.2":"";}
    public static String problem(){var mod=ModList.get().getModContainerById("easy_npc");return problem(mod.isPresent(),mod.map(m->m.getModInfo().getVersion().toString()).orElse(""));}
    public static void require(){String error=problem();if(!error.isEmpty())throw new IllegalArgumentException(error);}
    private NpcDependency(){}
}
