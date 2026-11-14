package org.humanjourney.core.geography.map.client;
import java.util.*;
/** Bounded placement beside existing widgets; no unsafe overlapping fallback. */
public final class BoundaryControlLayout {
 public static final int WIDTH=118,HEIGHT=20,MARGIN=8;
 public record Rect(int x,int y,int width,int height){public boolean contains(double px,double py){return px>=x&&px<x+width&&py>=y&&py<y+height;}public boolean overlaps(Rect b){return x<b.x+b.width+4&&x+width+4>b.x&&y<b.y+b.height+4&&y+height+4>b.y;}}
 public static Optional<Rect> place(int width,int height,List<Rect> occupied){if(width<WIDTH+MARGIN*2||height<HEIGHT+MARGIN*2)return Optional.empty();for(int row=0;row<4;row++){int y=MARGIN+row*(HEIGHT+4);if(y+HEIGHT>height-MARGIN)break;for(int column=0;column<32;column++){int x=width-WIDTH-MARGIN-column*(WIDTH+4);if(x<MARGIN)break;var r=new Rect(x,y,WIDTH,HEIGHT);if(occupied.stream().noneMatch(r::overlaps))return Optional.of(r);}}return Optional.empty();}
 private BoundaryControlLayout(){}
}
