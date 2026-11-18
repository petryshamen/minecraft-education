package org.humanjourney.core.mapfeature;
import java.util.*;
/** Shared catalog/client limits; geometry itself remains independent of server policy. */
public final class GeometryBudget {
 public static void check(Collection<MapFeature.View> features){if(features.size()>2048)throw new IllegalArgumentException("maximum 2048 features");long vertices=0,rings=0;for(var f:features){vertices+=f.geometry().vertexCount();rings+=f.geometry().ringCount();if(vertices>FeatureGeometry.MAX_CATALOG_VERTICES||rings>FeatureGeometry.MAX_CATALOG_RINGS)throw new IllegalArgumentException("catalog geometry budget exceeded at "+f.id()+": vertices="+vertices+" rings="+rings);}}
 private GeometryBudget(){}
}
