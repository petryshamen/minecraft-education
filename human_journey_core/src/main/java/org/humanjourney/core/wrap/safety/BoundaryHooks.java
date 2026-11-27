package org.humanjourney.core.wrap.safety;
/** Structural readiness markers: every independently required guard must be installed. */
public final class BoundaryHooks {
 public interface LevelReads {}
 public interface Movement {}
 public interface Packets {}
 public interface Cancellation {}
 private BoundaryHooks(){}
}
