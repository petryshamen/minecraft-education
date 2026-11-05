package org.humanjourney.core.npc.context;
public record NpcContext(String era,String season,String macroRegion,String localArea,String locationLabel){
    public static NpcContext of(String era,String season,NpcGeography.Location location){return new NpcContext(era,season,location.macro(),location.local(),location.label());}
}
