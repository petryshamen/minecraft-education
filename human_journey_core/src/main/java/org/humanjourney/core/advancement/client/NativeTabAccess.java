package org.humanjourney.core.advancement.client;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
public interface NativeTabAccess {Map<AdvancementHolder,AdvancementWidget> hjWidgets();double hjScrollX();double hjScrollY();void hjScroll(double x,double y);void hjBounds(int minX,int minY,int maxX,int maxY);}
