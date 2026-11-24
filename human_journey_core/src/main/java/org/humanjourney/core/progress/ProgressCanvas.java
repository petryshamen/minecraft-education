package org.humanjourney.core.progress;
import java.util.*;
/** Pure deterministic layout/routing/viewport mathematics shared with structural tests. */
public final class ProgressCanvas {
 public record Box(double x,double y,double width,double height){public boolean contains(double px,double py){return px>=x&&py>=y&&px<x+width&&py<y+height;}}
 public record Layout(Box canvas,Box details){public static Layout of(int width,int height){double w=Math.max(1,width-16),h=Math.max(1,height-68);if(width>=640){double panel=Math.min(280,width*.3);return new Layout(new Box(8,40,Math.max(1,w-panel-8),h),new Box(width-panel-8,40,panel,h));}double detail=Math.min(104,Math.max(60,h*.42));return new Layout(new Box(8,40,w,Math.max(1,h-detail-8)),new Box(8,40+h-detail,w,detail));}}
 public record Bounds(double left,double top,double right,double bottom){}
 public static Bounds bounds(ProgressView view){if(view.nodes().isEmpty())return new Bounds(0,0,400,200);double x=Double.POSITIVE_INFINITY,y=x,right=Double.NEGATIVE_INFINITY,bottom=right;for(var n:view.nodes()){x=Math.min(x,n.x());y=Math.min(y,n.y());right=Math.max(right,n.x()+ProgressGraph.NODE_WIDTH);bottom=Math.max(bottom,n.y()+ProgressGraph.NODE_HEIGHT);}for(var era:view.eras()){x=Math.min(x,era.start());right=Math.max(right,era.end());}return new Bounds(x-24,y-48,right+24,bottom+24);}
 public static List<ProgressGraph.Point> route(ProgressGraph.Edge edge,Map<String,ProgressView.Node> nodes,int lane){var a=nodes.get(edge.from());var b=nodes.get(edge.to());var start=new ProgressGraph.Point(a.x()+ProgressGraph.NODE_WIDTH,a.y()+ProgressGraph.NODE_HEIGHT/2D);var end=new ProgressGraph.Point(b.x(),b.y()+ProgressGraph.NODE_HEIGHT/2D);var points=new ArrayList<ProgressGraph.Point>();points.add(start);if(!edge.route().isEmpty())points.addAll(edge.route());else{double mid=(start.x()+end.x())/2+(lane%5-2)*5;points.add(new ProgressGraph.Point(mid,start.y()));points.add(new ProgressGraph.Point(mid,end.y()));}points.add(end);return List.copyOf(points);}
 public static final class Viewport {
  public static final double MIN_ZOOM=.35,MAX_ZOOM=2;private double zoom=1,panX,panY;private Bounds bounds=new Bounds(0,0,400,200);private Box box=new Box(0,0,400,200);
  public double zoom(){return zoom;}public double panX(){return panX;}public double panY(){return panY;}
  public void configure(Box box,Bounds bounds){this.box=box;this.bounds=bounds;clamp();}
  public void start(){zoom=.85;panX=12-bounds.left*zoom;panY=12-bounds.top*zoom;clamp();}
  public void fit(){zoom=Math.max(MIN_ZOOM,Math.min(MAX_ZOOM,Math.min(box.width/(bounds.right-bounds.left),box.height/(bounds.bottom-bounds.top))));panX=(box.width-(bounds.right-bounds.left)*zoom)/2-bounds.left*zoom;panY=(box.height-(bounds.bottom-bounds.top)*zoom)/2-bounds.top*zoom;clamp();}
  public ProgressGraph.Point screen(double x,double y){return new ProgressGraph.Point(box.x+panX+x*zoom,box.y+panY+y*zoom);}
  public ProgressGraph.Point world(double x,double y){return new ProgressGraph.Point((x-box.x-panX)/zoom,(y-box.y-panY)/zoom);}
  public void pan(double dx,double dy){if(Double.isFinite(dx)&&Double.isFinite(dy)){panX+=dx;panY+=dy;clamp();}}
  public void zoom(double delta,double x,double y){if(!Double.isFinite(delta)||!Double.isFinite(x)||!Double.isFinite(y))return;var before=world(x,y);zoom=Math.max(MIN_ZOOM,Math.min(MAX_ZOOM,zoom*Math.pow(1.15,Math.max(-8,Math.min(8,delta)))));panX=x-box.x-before.x()*zoom;panY=y-box.y-before.y()*zoom;clamp();}
  private void clamp(){double margin=48;panX=Math.max(margin-bounds.right*zoom,Math.min(box.width-margin-bounds.left*zoom,panX));panY=Math.max(margin-bounds.bottom*zoom,Math.min(box.height-margin-bounds.top*zoom,panY));}
 }
 public static final List<String> TABS=List.of("civilization_progress");
 private ProgressCanvas(){}
}
