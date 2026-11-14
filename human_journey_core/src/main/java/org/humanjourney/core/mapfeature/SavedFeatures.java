package org.humanjourney.core.mapfeature;
import java.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.*;
import org.humanjourney.core.geography.GeographyRoles;
public final class SavedFeatures extends SavedData {
 public static final Codec<SavedFeatures> CODEC=RecordCodecBuilder.create(i->i.group(Codec.STRING.listOf().optionalFieldOf("discoveredMapFeatures",List.of()).forGetter(d->new ArrayList<>(d.snapshot().ids())),Codec.LONG.optionalFieldOf("revision",0L).forGetter(d->d.snapshot().revision())).apply(i,SavedFeatures::new));
 public static final SavedDataType<SavedFeatures> TYPE=new SavedDataType<>("human_journey_map_features",SavedFeatures::new,CODEC,null);
 private final FeatureDiscovery ledger;
 public SavedFeatures(){ledger=new FeatureDiscovery();}private SavedFeatures(List<String> ids,long revision){ledger=new FeatureDiscovery(ids,revision);}
 public static SavedFeatures get(MinecraftServer server){return server.overworld().getDataStorage().computeIfAbsent(TYPE);}
 public FeatureDiscovery.Snapshot snapshot(){return ledger.snapshot();}
 public Set<String> discover(GeographyRoles role,FeatureCatalog.Data data,String dimension,double x,double z){var added=ledger.discover(role,data,dimension,x,z);if(!added.isEmpty())setDirty();return added;}
 public boolean mark(String id){boolean changed=ledger.mark(id);if(changed)setDirty();return changed;}public void reset(){ledger.reset();setDirty();}
}
