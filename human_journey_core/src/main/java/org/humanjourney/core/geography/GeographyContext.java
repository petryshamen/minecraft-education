package org.humanjourney.core.geography;
import java.util.*;
import org.humanjourney.core.npc.context.*;
/** Selected IDs/labels only, never polygon geometry. Presentation fallback does not change NPC names. */
public record GeographyContext(String macroId,String macroLabel,String localId,String localLabel){
 public static final GeographyContext EMPTY=new GeographyContext("","","","");
 public boolean visible(){return !macroLabel.isBlank()||!localLabel.isBlank();}
 public static String readable(String id){var words=id.replace('_',' ').replace('-',' ').replace('.',' ').trim().split(" +");var out=new ArrayList<String>();for(String word:words)if(!word.isEmpty())out.add(word.substring(0,1).toUpperCase(Locale.ROOT)+word.substring(1));return String.join(" ",out);}
 public static String label(NpcGeography.Area area,String era){String label=area.label(era);return label.isBlank()?readable(area.id()):label;}
 public static GeographyContext resolve(NpcContextStore.Data data,EraRules.Scores scores,String dimension,double x,double z){
  String era;try{era=data.eraRules().resolve(scores);}catch(IllegalArgumentException e){era="unknown";}
  var geo=data.geography();var location=geo.resolve(dimension,x,z,era);String currentEra=era;
  var macro=geo.macros().stream().filter(a->a.id().equals(location.macro())).findFirst();var local=geo.locals().stream().filter(a->a.id().equals(location.local())).findFirst();
  return new GeographyContext(macro.map(NpcGeography.Area::id).orElse(""),macro.map(a->label(a,currentEra)).orElse(""),local.map(NpcGeography.Area::id).orElse(""),local.map(a->label(a,currentEra)).orElse(""));
 }
}
