package org.humanjourney.core.geography;
import java.util.*;
/** One ledger per civilisation/world, not per player or dimension. IDs survive label and catalog changes. */
public final class DiscoveryLedger {
 public enum Kind{MACRO,LOCAL}
 public record Snapshot(Set<String> macros,Set<String> locals,long revision){public Snapshot{macros=Set.copyOf(macros);locals=Set.copyOf(locals);}}
 public record Delta(Set<String> macros,Set<String> locals,long revision){public Delta{macros=Set.copyOf(macros);locals=Set.copyOf(locals);}public boolean changed(){return !macros.isEmpty()||!locals.isEmpty();}}
 private final Set<String> macros=new TreeSet<>(),locals=new TreeSet<>();private long revision;
 public DiscoveryLedger(){}
 public DiscoveryLedger(Collection<String> macros,Collection<String> locals,long revision){macros.stream().filter(DiscoveryLedger::validId).forEach(this.macros::add);locals.stream().filter(DiscoveryLedger::validId).forEach(this.locals::add);this.revision=Math.max(0,revision);}
 public static boolean validId(String id){return id!=null&&id.matches("[a-z0-9_][a-z0-9_.-]{0,63}")&&!Set.of("none","unknown").contains(id);}
 public Snapshot snapshot(){return new Snapshot(macros,locals,revision);}
 public Delta discover(GeographyRoles role,GeographyContext context){if(!role.canDiscover())return new Delta(Set.of(),Set.of(),revision);Set<String> newMacros=new TreeSet<>(),newLocals=new TreeSet<>();if(validId(context.macroId())&&macros.add(context.macroId()))newMacros.add(context.macroId());if(validId(context.localId())&&locals.add(context.localId()))newLocals.add(context.localId());if(!newMacros.isEmpty()||!newLocals.isEmpty())revision++;return new Delta(newMacros,newLocals,revision);}
 public Delta mark(Kind kind,String id){if(!validId(id))throw new IllegalArgumentException("invalid geography ID: "+id);var target=kind==Kind.MACRO?macros:locals;if(!target.add(id))return new Delta(Set.of(),Set.of(),revision);revision++;return new Delta(kind==Kind.MACRO?Set.of(id):Set.of(),kind==Kind.LOCAL?Set.of(id):Set.of(),revision);}
 public void reset(){macros.clear();locals.clear();revision++;}
}
