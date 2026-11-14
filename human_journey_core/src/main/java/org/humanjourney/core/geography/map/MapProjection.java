package org.humanjourney.core.geography.map;
import java.util.*;
import org.humanjourney.core.geography.*;
import org.humanjourney.core.npc.context.NpcGeography;
/** Server-only projection factory. It creates DTOs lazily for discovered IDs, never the hidden catalog. */
public final class MapProjection {
 public record View(Map<MapArea.Key,MapArea> areas,int staleMacros,int staleLocals){public View{areas=Map.copyOf(areas);}}
 public static final class Factory {
  private final Map<String,NpcGeography.Area> macros=new HashMap<>(),locals=new HashMap<>();
  private final Map<String,MapArea> cache=new HashMap<>();private final String era;
  public Factory(NpcGeography geo,String era){geo.macros().forEach(a->macros.put(a.id(),a));geo.locals().forEach(a->locals.put(a.id(),a));this.era=era;}
  private MapArea area(NpcGeography.Area a,MapArea.Kind kind,String parent){String key=kind+":"+a.id()+":"+parent;return cache.computeIfAbsent(key,k->new MapArea(kind,a.id(),GeographyContext.label(a,era),a.dimension(),a.priority(),parent,a.shape(),null));}
  public View project(DiscoveryLedger.Snapshot discovered){var result=new TreeMap<MapArea.Key,MapArea>();int staleM=0,staleL=0;for(String id:new TreeSet<>(discovered.macros())){var a=macros.get(id);if(a==null){staleM++;continue;}try{var dto=area(a,MapArea.Kind.MACRO,"");result.put(dto.key(),dto);}catch(IllegalArgumentException e){staleM++;}}
   for(String id:new TreeSet<>(discovered.locals())){var a=locals.get(id);if(a==null){staleL++;continue;}String parent=result.containsKey(new MapArea.Key(MapArea.Kind.MACRO,a.parent()))?a.parent():"";try{var dto=area(a,MapArea.Kind.LOCAL,parent);result.put(dto.key(),dto);}catch(IllegalArgumentException e){staleL++;}}return new View(result,staleM,staleL);}
 }
 private MapProjection(){}
}
