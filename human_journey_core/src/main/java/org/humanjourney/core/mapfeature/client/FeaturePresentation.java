package org.humanjourney.core.mapfeature.client;
import java.util.*;
import org.humanjourney.core.mapfeature.*;
import org.humanjourney.core.geography.map.MapArea.Bounds;
public final class FeaturePresentation {
 public static final int NORMAL_RGB=0x609FB8,HOVER_RGB=0x76DAFF;public static final double HIT_GUI_PIXELS=5;
 public static List<MapFeature.View> persistent(FeatureIndex index,String dimension,Bounds view,FeatureMode mode){if(mode!=FeatureMode.ALL)return List.of();var out=index.query(dimension,view);out.sort(Comparator.comparingInt(MapFeature.View::priority).thenComparing(MapFeature.View::id));return out;}
 public static MapFeature.View selected(ClientFeatureState.Snapshot snapshot,String dimension,FeatureMode mode,FeatureIndex.Selection selected){if(mode==FeatureMode.OFF||selected==null||selected.feature()==null)return null;var f=selected.feature();return dimension.equals(f.dimension())&&f.equals(snapshot.features().get(f.id()))?f:null;}
 private FeaturePresentation(){}
}
