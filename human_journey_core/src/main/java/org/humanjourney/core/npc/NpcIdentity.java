package org.humanjourney.core.npc;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
/** Runtime labels fit Easy NPC's 32-character persistence limit. Content IDs never become mutable identity. */
public record NpcIdentity(UUID entity,UUID preset){
    public static NpcIdentity fresh(){return new NpcIdentity(UUID.randomUUID(),UUID.randomUUID());}
    public String label(String kind,String local){return UUID.nameUUIDFromBytes((entity+"/"+kind+"/"+local).getBytes(StandardCharsets.UTF_8)).toString().replace("-","");}
}
