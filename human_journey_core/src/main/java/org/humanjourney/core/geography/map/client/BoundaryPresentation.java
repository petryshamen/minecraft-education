package org.humanjourney.core.geography.map.client;
import java.util.*;
import org.humanjourney.core.geography.map.*;
/** Pure draw selection. The single native hover result supplies both tooltip and highlights. */
public final class BoundaryPresentation {
 public enum Style {MACRO(0x7EA8D8),LOCAL(0xD6E6CE),HOVER_MACRO(0x7FC3FF),HOVER_LOCAL(0xFFFFFF);public final int rgb;Style(int rgb){this.rgb=rgb;}}
 public record Layer(MapArea area,Style style,int component){public Layer(MapArea area,Style style){this(area,style,0);}}
 private static void persistent(List<Layer> out,MapSpatialIndex index,String dimension,MapArea.Bounds view,MapArea.Kind kind,Style style){var visible=index.queryParts(dimension,kind,view);visible.sort(Comparator.comparingInt((org.humanjourney.core.geography.AreaSpatialIndex.Entry<MapArea> e)->e.area().priority()).thenComparing(e->e.area().id()).thenComparingInt(e->e.component()));for(var e:visible)out.add(new Layer(e.area(),style,e.component()));}
 private static void selected(List<Layer> out,MapSpatialIndex index,String dimension,MapArea.Bounds view,MapArea area,Style style,int component){if(component>=0&&area!=null&&area.equals(index.area(area.key()))&&dimension.equals(area.dimension())&&area.shape().parts().get(component).bounds().intersects(view))out.add(new Layer(area,style,component));}
 public static List<Layer> plan(MapSpatialIndex index,String dimension,MapArea.Bounds view,BoundaryMode mode,MapSpatialIndex.Hover hover){if(mode==BoundaryMode.OFF)return List.of();var out=new ArrayList<Layer>();if(mode.macros())persistent(out,index,dimension,view,MapArea.Kind.MACRO,Style.MACRO);if(mode.locals())persistent(out,index,dimension,view,MapArea.Kind.LOCAL,Style.LOCAL);if(hover!=null){selected(out,index,dimension,view,hover.macroArea(),Style.HOVER_MACRO,hover.macroComponent());selected(out,index,dimension,view,hover.localArea(),Style.HOVER_LOCAL,hover.localComponent());}return List.copyOf(out);}
 private BoundaryPresentation(){}
}
