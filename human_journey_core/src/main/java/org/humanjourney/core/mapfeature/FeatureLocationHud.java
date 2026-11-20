package org.humanjourney.core.mapfeature;
import org.humanjourney.core.geography.GeographyContext;
import org.humanjourney.core.geography.HudPresentation;
/** Read-only physical location from the discovered-only client mirror; independent of map modes. */
public final class FeatureLocationHud {
 public static final double POLYLINE_TOLERANCE_BLOCKS=2.0;
 public static HudPresentation resolve(boolean participant,GeographyContext geography,ClientFeatureState.Snapshot discovered,String dimension,double x,double z){
  var fallback=HudPresentation.of(participant,geography);
  if(!participant)return fallback;
  var selected=discovered.index().hover(dimension,x,z,POLYLINE_TOLERANCE_BLOCKS);
  if(selected.feature()==null)return fallback;
  String secondary=geography.localLabel().isBlank()?geography.macroLabel():geography.localLabel();
  return new HudPresentation(selected.feature().label(),secondary);
 }
 private FeatureLocationHud(){}
}
