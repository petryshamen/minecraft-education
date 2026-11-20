package org.humanjourney.core.geography.map.client;
import java.util.*;
/** Bounded placement beside existing widgets; no unsafe overlapping fallback. */
public final class BoundaryControlLayout {
 public static final int WIDTH=118,HEIGHT=20,MARGIN=8;
 public record Rect(int x,int y,int width,int height){public boolean contains(double px,double py){return px>=x&&px<x+width&&py>=y&&py<y+height;}public boolean overlaps(Rect b){return x<b.x+b.width+4&&x+width+4>b.x&&y<b.y+b.height+4&&y+height+4>b.y;}}
 public static final int COMPASS_WIDTH=112,COMPASS_HEIGHT=104;
 public static Rect compass(int width,int height){return new Rect(Math.max(0,width-COMPASS_WIDTH),0,Math.min(width,COMPASS_WIDTH),Math.min(height,COMPASS_HEIGHT));}
 private static Optional<Rect> find(int width,int height,List<Rect> occupied,int groupHeight){if(width<WIDTH+MARGIN*2||height<groupHeight+MARGIN*2)return Optional.empty();var blocked=new ArrayList<>(occupied);blocked.add(compass(width,height));for(int row=0;row<8;row++){int y=MARGIN+row*(groupHeight+4);if(y+groupHeight>height-MARGIN)break;int inset=y<COMPASS_HEIGHT?COMPASS_WIDTH:0;for(int column=0;column<32;column++){int x=width-WIDTH-MARGIN-inset-column*(WIDTH+4);if(x<MARGIN)break;var r=new Rect(x,y,WIDTH,groupHeight);if(blocked.stream().noneMatch(r::overlaps))return Optional.of(r);}}return Optional.empty();}
 public static Optional<Rect> place(int width,int height,List<Rect> occupied){return find(width,height,occupied,HEIGHT);}
 public static Optional<List<Rect>> placePair(int width,int height,List<Rect> occupied){
  // Strict far-right rail: never fall back left into Xaero's centred coordinates.
  int x=width-WIDTH-2,groupHeight=HEIGHT*2+4;if(x<MARGIN)return Optional.empty();for(int y=COMPASS_HEIGHT+4;y+groupHeight<=height-MARGIN;y+=groupHeight+4){var r=new Rect(x,y,WIDTH,groupHeight);if(!r.overlaps(compass(width,height))&&occupied.stream().noneMatch(r::overlaps))return Optional.of(List.of(new Rect(x,y,WIDTH,HEIGHT),new Rect(x,y+HEIGHT+4,WIDTH,HEIGHT)));}return Optional.empty();}
 private BoundaryControlLayout(){}
}
