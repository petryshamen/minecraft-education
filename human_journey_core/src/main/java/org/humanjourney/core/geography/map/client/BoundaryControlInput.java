package org.humanjourney.core.geography.map.client;
import java.util.*;
/** Capture only gestures which begin inside the usable control, never normal map gestures. */
public final class BoundaryControlInput {
 public enum Action{PASS,CYCLE,CONSUME}
 private final Set<Integer> held=new HashSet<>();
 public Action press(BoundaryControlLayout.Rect target,boolean usable,double x,double y,int button){if(!usable||target==null||!target.contains(x,y)||button<0)return Action.PASS;held.add(button);return button==0?Action.CYCLE:Action.CONSUME;}
 public boolean release(int button){return held.remove(button);}public boolean drag(int button){return held.contains(button);}
}
