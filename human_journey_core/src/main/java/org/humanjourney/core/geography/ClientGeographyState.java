package org.humanjourney.core.geography;
import java.util.*;
/** Client mirror API with no client rendering classes. Snapshot batches publish atomically for future maps. */
public final class ClientGeographyState {
 private GeographyPackets.Context context=new GeographyPackets.Context(false,GeographyContext.EMPTY);
 private DiscoveryLedger.Snapshot discovery=new DiscoveryLedger.Snapshot(Set.of(),Set.of(),0);
 private Set<String> pendingMacros,pendingLocals;private long pendingRevision;
 public GeographyPackets.Context context(){return context;}public DiscoveryLedger.Snapshot discovery(){return discovery;}
 public HudPresentation hud(){return HudPresentation.of(context.participant(),context.geography());}
 public void accept(GeographyPackets.Context p){context=p;if(!p.participant())clear();}
 public void accept(GeographyPackets.Discovery p){
  if(p.revision()<discovery.revision())return;
  switch(p.kind()){
   case SINGLE->{discovery=new DiscoveryLedger.Snapshot(new HashSet<>(p.macros()),new HashSet<>(p.locals()),p.revision());pendingMacros=null;pendingLocals=null;}
   case START->{pendingMacros=new HashSet<>(p.macros());pendingLocals=new HashSet<>(p.locals());pendingRevision=p.revision();}
   case PART,END->{if(pendingMacros==null||p.revision()!=pendingRevision)return;pendingMacros.addAll(p.macros());pendingLocals.addAll(p.locals());if(p.kind()==GeographyPackets.Kind.END){discovery=new DiscoveryLedger.Snapshot(pendingMacros,pendingLocals,pendingRevision);pendingMacros=null;pendingLocals=null;}}
   case DELTA->{var macros=new HashSet<>(discovery.macros());var locals=new HashSet<>(discovery.locals());macros.addAll(p.macros());locals.addAll(p.locals());discovery=new DiscoveryLedger.Snapshot(macros,locals,p.revision());}
  }
 }
 public void clear(){context=new GeographyPackets.Context(false,GeographyContext.EMPTY);discovery=new DiscoveryLedger.Snapshot(Set.of(),Set.of(),0);pendingMacros=null;pendingLocals=null;}
}
