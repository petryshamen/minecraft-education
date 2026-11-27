package org.humanjourney.core.wrap;

/** Local to the opt-in crossing engine. Never rewrites stored geography coordinates. */
public final class WrapMath {
 public static final double WEST=-12288, EAST=12288, WIDTH=24576;
 public static boolean outside(double x){return Double.isFinite(x)&&(x<WEST||x>=EAST);}
 public static double canonical(double x){
  if(!Double.isFinite(x)||Math.abs(x)>30_000_000)throw new IllegalArgumentException("Invalid world X");
  double result=x-WIDTH*Math.floor((x-WEST)/WIDTH);
  return result>=EAST?WEST:result;
 }
 private WrapMath(){}
}
