package org.humanjourney.core.geography;
import java.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.*;
public final class SavedDiscovery extends SavedData {
 public static final Codec<SavedDiscovery> CODEC=RecordCodecBuilder.create(i->i.group(Codec.STRING.listOf().optionalFieldOf("discoveredMacroRegions",List.of()).forGetter(d->new ArrayList<>(d.snapshot().macros())),Codec.STRING.listOf().optionalFieldOf("discoveredLocalAreas",List.of()).forGetter(d->new ArrayList<>(d.snapshot().locals())),Codec.LONG.optionalFieldOf("revision",0L).forGetter(d->d.snapshot().revision())).apply(i,SavedDiscovery::new));
 public static final SavedDataType<SavedDiscovery> TYPE=new SavedDataType<>("human_journey_geography",SavedDiscovery::new,CODEC,null);
 private final DiscoveryLedger ledger;
 public SavedDiscovery(){ledger=new DiscoveryLedger();}
 private SavedDiscovery(List<String> macros,List<String> locals,long revision){ledger=new DiscoveryLedger(macros,locals,revision);}
 public static SavedDiscovery get(MinecraftServer server){return server.overworld().getDataStorage().computeIfAbsent(TYPE);}
 public DiscoveryLedger.Snapshot snapshot(){return ledger.snapshot();}
 public DiscoveryLedger.Delta discover(GeographyRoles role,GeographyContext view){var delta=ledger.discover(role,view);if(delta.changed())setDirty();return delta;}
 public DiscoveryLedger.Delta mark(DiscoveryLedger.Kind kind,String id){var delta=ledger.mark(kind,id);if(delta.changed())setDirty();return delta;}
 public void reset(){ledger.reset();setDirty();}
}
