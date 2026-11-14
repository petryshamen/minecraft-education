package org.humanjourney.core.geography.map;
/** Xaero's inspected raw-pixel camera transform, expressed without a Xaero or client type. */
public record MapViewTransform(int rawWidth,int rawHeight,double guiScale,double cameraX,double cameraZ,double rawScale){
 public record Point(double x,double y){}
 public record Segment(Point a,Point b){}
 public MapViewTransform{if(rawWidth<=0||rawHeight<=0||!Double.isFinite(guiScale)||guiScale<=0||!Double.isFinite(cameraX)||!Double.isFinite(cameraZ)||!Double.isFinite(rawScale)||rawScale<1e-12||rawScale>1e12)throw new IllegalArgumentException("unavailable map camera/scale");}
 public Point screen(double x,double z){return new Point((Math.floor(rawWidth/2D)+(x-cameraX)*rawScale)/guiScale,(Math.floor(rawHeight/2D)+(z-cameraZ)*rawScale)/guiScale);}
 public Point world(double guiX,double guiY){return new Point(cameraX+(guiX*guiScale-Math.floor(rawWidth/2D))/rawScale,cameraZ+(guiY*guiScale-Math.floor(rawHeight/2D))/rawScale);}
 public MapArea.Bounds viewport(double guiWidth,double guiHeight){var a=world(0,0);var b=world(guiWidth,guiHeight);return new MapArea.Bounds(a.x,a.y,b.x,b.y);}
 /** Clip before conversion so distant geometry never reaches float renderer coordinates. No simplification. */
 public Segment segment(double x1,double z1,double x2,double z2,MapArea.Bounds view){double dx=x2-x1,dz=z2-z1,lo=0,hi=1;double[] p={-dx,dx,-dz,dz},q={x1-view.minX(),view.maxX()-x1,z1-view.minZ(),view.maxZ()-z1};for(int i=0;i<4;i++){if(p[i]==0){if(q[i]<0)return null;}else{double t=q[i]/p[i];if(p[i]<0)lo=Math.max(lo,t);else hi=Math.min(hi,t);if(lo>hi)return null;}}return new Segment(screen(x1+lo*dx,z1+lo*dz),screen(x1+hi*dx,z1+hi*dz));}
}
