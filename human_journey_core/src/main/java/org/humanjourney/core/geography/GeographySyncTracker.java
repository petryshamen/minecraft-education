package org.humanjourney.core.geography;
/** Per-connection transient traffic cache, never personal discovery progression. */
public final class GeographySyncTracker {
 public record Input(int entityId,String dimension,double x,double z,String era,long geographyRevision,GeographyRoles roles){}
 private Input observed;private GeographyContext resolved=GeographyContext.EMPTY;private GeographyPackets.Context lastSent;private long sentRevision=-1;
 public boolean evaluate(Input next){return !next.equals(observed);}
 public boolean joining(Input next){return next.roles().participant()&&(observed==null||!observed.roles().participant()||observed.entityId()!=next.entityId());}
 public void observed(Input next,GeographyContext view){observed=next;resolved=view;}
 public GeographyContext resolved(){return resolved;}
 public boolean contextChanged(GeographyPackets.Context next){return !next.equals(lastSent);}
 public void sent(GeographyPackets.Context packet){lastSent=packet;}
 public boolean needsSnapshot(long revision){return sentRevision!=revision;}
 public void synced(long revision){sentRevision=revision;}
}
