package org.humanjourney.core.integration.xaero.client;
import java.util.*;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import xaero.map.gui.GuiMap;
import org.humanjourney.core.geography.client.GeographyClientConfig;
import org.humanjourney.core.geography.map.client.*;
import org.humanjourney.core.integration.xaero.XaeroStatus;
/** Optional exact-version client UI; public Forge screen hooks, no additional Xaero Mixin. */
public final class XaeroBoundaryControl {
 private static final class Entry{Button button;boolean failed;String problem="";final BoundaryControlInput input=new BoundaryControlInput();}
 private static final Map<GuiMap,Entry> controls=new WeakHashMap<>();
 public static void register(){
  ScreenEvent.Init.Post.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=new Entry();controls.put(screen,entry);try{entry.button=Button.builder(Component.literal(GeographyClientConfig.mode().label()),b->cycle(entry)).bounds(8,8,BoundaryControlLayout.WIDTH,BoundaryControlLayout.HEIGHT).build();entry.button.visible=false;event.addListener(entry.button);update(screen,entry);}catch(RuntimeException|ReflectiveOperationException|LinkageError error){fail(entry,error);}}});
  ScreenEvent.Render.Pre.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=controls.get(screen);if(entry!=null&&!entry.failed)try{update(screen,entry);}catch(RuntimeException|ReflectiveOperationException|LinkageError error){fail(entry,error);}}});
  ScreenEvent.Render.Post.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=controls.get(screen);if(entry!=null&&!entry.failed&&entry.button!=null&&entry.button.visible)try{entry.button.render(event.getGuiGraphics(),event.getMouseX(),event.getMouseY(),event.getPartialTick());}catch(RuntimeException|LinkageError error){fail(entry,error);}}});
  ScreenEvent.MouseButtonPressed.Pre.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=controls.get(screen);if(entry==null||entry.button==null)return;var b=entry.button;var bounds=new BoundaryControlLayout.Rect(b.getX(),b.getY(),b.getWidth(),b.getHeight());var action=entry.input.press(bounds,!entry.failed&&b.visible&&b.active,event.getMouseX(),event.getMouseY(),event.getButton());if(action!=BoundaryControlInput.Action.PASS){try{if(action==BoundaryControlInput.Action.CYCLE)b.mouseClicked(event.getMouseX(),event.getMouseY(),0);}catch(RuntimeException|LinkageError error){fail(entry,error);}event.setCanceled(true);}}});
  ScreenEvent.MouseButtonReleased.Pre.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=controls.get(screen);if(entry!=null&&entry.input.release(event.getButton()))event.setCanceled(true);}});
  ScreenEvent.MouseDragged.Pre.BUS.addListener(event->{if(event.getScreen() instanceof GuiMap screen){var entry=controls.get(screen);if(entry!=null&&entry.input.drag(event.getMouseButton()))event.setCanceled(true);}});
 }
 private static void cycle(Entry entry){try{GeographyClientConfig.mode(GeographyClientConfig.mode().next());entry.problem="";entry.button.setMessage(Component.literal(GeographyClientConfig.mode().label()));}catch(RuntimeException error){entry.problem="Could not save boundary mode; previous preference retained: "+error.getClass().getSimpleName();XaeroStatus.control(entry.problem);com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] {}",entry.problem,error);}}
 private static void update(GuiMap screen,Entry entry)throws IllegalAccessException{var b=entry.button;b.setMessage(Component.literal(GeographyClientConfig.mode().label()));b.visible=false;b.active=false;if(!Xaero13913Adapter.controlsUsable(screen)||Xaero13913Adapter.controlMenusOpen(screen)){XaeroStatus.control("Hidden while map presentation is unavailable or a native menu is open");return;}var occupied=new ArrayList<BoundaryControlLayout.Rect>();for(var child:screen.children())if(child instanceof AbstractWidget widget&&widget!=b&&widget.visible)occupied.add(new BoundaryControlLayout.Rect(widget.getX(),widget.getY(),widget.getWidth(),widget.getHeight()));var place=BoundaryControlLayout.place(screen.width,screen.height,occupied);if(place.isEmpty()){XaeroStatus.control("No collision-free map-control space; config preference remains usable");return;}var p=place.get();b.setX(p.x());b.setY(p.y());b.visible=true;b.active=true;XaeroStatus.control(entry.problem.isEmpty()?"Ready at "+p.x()+","+p.y():entry.problem);}
 private static void fail(Entry entry,Throwable error){entry.failed=true;if(entry.button!=null){entry.button.visible=false;entry.button.active=false;}entry.problem="Map control unavailable; config-based boundaries remain usable: "+error.getClass().getSimpleName();XaeroStatus.control(entry.problem);com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] {}",entry.problem,error);}
 private XaeroBoundaryControl(){}
}
