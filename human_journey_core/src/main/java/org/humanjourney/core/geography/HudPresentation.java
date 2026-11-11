package org.humanjourney.core.geography;
/** Pure presentation model; client font/render classes are isolated elsewhere. */
public record HudPresentation(String primary,String secondary){
 public static HudPresentation of(boolean participant,GeographyContext context){if(!participant||!context.visible())return new HudPresentation("","");return context.localLabel().isBlank()?new HudPresentation(context.macroLabel(),""):new HudPresentation(context.localLabel(),context.macroLabel());}
 public boolean visible(){return !primary.isBlank();}
 public record Layout(int x,int y,int width,int height){}
 public static Layout layout(int screenWidth,int screenHeight,int textWidth,boolean twoLines,int x,int y,double scale){int width=textWidth+10,height=(twoLines?20:9)+10;int drawWidth=(int)Math.ceil(width*scale),drawHeight=(int)Math.ceil(height*scale);return new Layout(Math.max(0,Math.min(x,screenWidth-drawWidth)),Math.max(0,Math.min(y,screenHeight-drawHeight)),width,height);}
}
