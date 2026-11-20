package org.humanjourney.core.geography.map;
import java.util.*;
import org.humanjourney.core.geography.GeographyGeometry;
public final class MapGeometryBudget {
 public static void check(Collection<MapArea> areas){long macros=areas.stream().filter(a->a.kind()==MapArea.Kind.MACRO).count();if(macros>512||areas.size()-macros>4096)throw new IllegalArgumentException("map catalog count exceeded");GeographyGeometry.catalog(areas.stream().map(MapArea::shape).toList());}
 private MapGeometryBudget(){}
}
