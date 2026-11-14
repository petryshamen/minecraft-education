package org.humanjourney.core.geography.map.client;
import java.util.*;
/** Client presentation only: never changes discovery, geometry or network state. */
public enum BoundaryMode {
 HOVER("hover","Hover"),LOCALS("locals","Locals"),ALL("all","All"),OFF("off","Off");
 private final String id,label;
 BoundaryMode(String id,String label){this.id=id;this.label=label;}
 public String id(){return id;}public String label(){return "HJ Borders: "+label;}
 public BoundaryMode next(){return values()[(ordinal()+1)%values().length];}
 public boolean locals(){return this==LOCALS||this==ALL;}public boolean macros(){return this==ALL;}
 public static boolean valid(Object value){return value instanceof String s&&Arrays.stream(values()).anyMatch(m->m.id.equals(s));}
 public static BoundaryMode parse(String value){return Arrays.stream(values()).filter(m->m.id.equals(value)).findFirst().orElse(HOVER);}
}
