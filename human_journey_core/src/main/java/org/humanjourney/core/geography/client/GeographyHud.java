package org.humanjourney.core.geography.client;
import org.humanjourney.core.geography.HudPresentation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
public final class GeographyHud {
 public static void render(GuiGraphics graphics,DeltaTracker partialTick){
  var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.options.hideGui||!GeographyClientConfig.ENABLED.get())return;var hud=GeographyClient.state().hud();if(!hud.visible())return;
  double scale=GeographyClientConfig.SCALE.get();int limit=Math.max(1,(int)(graphics.guiWidth()/scale)-16);String primary=fit(hud.primary(),Math.max(1,limit-12));String secondary=fit(hud.secondary(),limit);var first=Component.literal(primary).withStyle(ChatFormatting.BOLD);var second=Component.literal(secondary);
  int width=Math.max(mc.font.width(first),secondary.isBlank()?0:mc.font.width(second));var layout=HudPresentation.layout(graphics.guiWidth(),graphics.guiHeight(),width,!secondary.isBlank(),GeographyClientConfig.X.get(),GeographyClientConfig.Y.get(),scale);
  var pose=graphics.pose();pose.pushMatrix();try{pose.translate(layout.x(),layout.y());pose.scale((float)scale,(float)scale);int alpha=(int)Math.round(255*GeographyClientConfig.OPACITY.get());graphics.fill(0,0,layout.width(),layout.height(),alpha<<24);graphics.drawString(mc.font,first,5,5,0xFFFFFFFF,true);if(!secondary.isBlank())graphics.drawString(mc.font,second,5,16,0xFFB8C6D8,true);}finally{pose.popMatrix();}
 }
 private static String fit(String text,int width){var font=Minecraft.getInstance().font;return font.width(text)<=width?text:font.plainSubstrByWidth(text,Math.max(0,width-font.width("…")))+"…";}
 private GeographyHud(){}
}
