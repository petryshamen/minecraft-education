package org.humanjourney.core.progress;
/** Per-connection transient delivery metadata, never progress storage. */
public final class ProgressDelivery {
 private int entity=Integer.MIN_VALUE;private boolean participant;private long revision=-1;
 public boolean needs(int entity,boolean participant,long revision){return this.entity!=entity||this.participant!=participant||participant&&this.revision!=revision;}
 public void sent(int entity,boolean participant,long revision){this.entity=entity;this.participant=participant;this.revision=revision;}
}
