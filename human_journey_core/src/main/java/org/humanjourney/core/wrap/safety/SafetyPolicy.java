package org.humanjourney.core.wrap.safety;
/** Pure canonical bounds, including the complete horizontal bounding footprint. */
public final class SafetyPolicy {
 public static final int MIN_X=-768,MAX_X=768,MIN_Z=-384,MAX_Z=384;
 public static boolean chunkInside(int x,int z){return x>=MIN_X&&x<MAX_X&&z>=MIN_Z&&z<MAX_Z;}
 public static boolean inside(double minX,double maxX,double minZ,double maxZ){return Double.isFinite(minX)&&Double.isFinite(maxX)&&Double.isFinite(minZ)&&Double.isFinite(maxZ)&&minX>=-12288&&maxX<12288&&minZ>=-6144&&maxZ<6144;}
 public static double clamp(double proposed,double lo,double hi){if(!Double.isFinite(proposed)||lo>hi)throw new IllegalArgumentException("Invalid containment bounds");return Math.max(lo,Math.min(hi,proposed));}
 private SafetyPolicy(){}
}
