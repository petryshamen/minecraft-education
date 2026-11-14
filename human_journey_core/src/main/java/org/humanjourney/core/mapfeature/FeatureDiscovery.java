package org.humanjourney.core.mapfeature;
import java.util.*;
import org.humanjourney.core.geography.*;
public final class FeatureDiscovery {
 public record Snapshot(Set<String> ids,long revision){public Snapshot{ids=Set.copyOf(ids);}}
 private final Set<String> ids=new TreeSet<>();private long revision;
 public FeatureDiscovery(){}public FeatureDiscovery(Collection<String> values,long revision){values.stream().filter(DiscoveryLedger::validId).forEach(ids::add);this.revision=Math.max(0,revision);}
 public Snapshot snapshot(){return new Snapshot(ids,revision);}
 public Set<String> discover(GeographyRoles roles,FeatureCatalog.Data data,String dimension,double x,double z){if(!roles.canDiscover())return Set.of();var matches=new TreeSet<String>();for(var view:data.index().query(dimension,FeatureIndex.around(x,z,64))){var f=data.features().get(view.id());if(!ids.contains(view.id())&&f.view().geometry().near(x,z,f.discoveryRadius()))matches.add(view.id());}if(ids.addAll(matches))revision++;return Set.copyOf(matches);}
 public boolean mark(String id){if(!DiscoveryLedger.validId(id))throw new IllegalArgumentException("invalid feature ID");if(!ids.add(id))return false;revision++;return true;}
 public void reset(){ids.clear();revision++;}
}
