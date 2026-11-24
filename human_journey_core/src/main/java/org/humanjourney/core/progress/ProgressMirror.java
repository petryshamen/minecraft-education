package org.humanjourney.core.progress;
import java.util.*;
import java.nio.charset.StandardCharsets;
/** Single bounded transaction and immutable publication; client cannot create progress. */
public final class ProgressMirror {
 private ProgressView view=ProgressView.empty(false);private UUID session;private final Set<UUID> retired=new LinkedHashSet<>();private long published,pending;private byte[][] chunks;private int length,received;private String problem="";
 public ProgressView view(){return view;}public String problem(){return problem;}
 public void clear(){view=ProgressView.empty(false);session=null;retired.clear();published=0;drop();problem="";}
 private void drop(){chunks=null;pending=0;length=0;received=0;}
 public boolean accept(ProgressPackets.Frame p){if(p.invalid()||retired.contains(p.session()))return false;if(!p.session().equals(session)){if(session!=null){retired.add(session);if(retired.size()>8)retired.remove(retired.iterator().next());}session=p.session();published=0;drop();}if(p.sequence()<=published||p.sequence()<pending)return false;if(chunks==null||p.sequence()>pending){drop();pending=p.sequence();length=p.length();chunks=new byte[p.parts()][];}if(p.length()!=length||p.parts()!=chunks.length){drop();problem="Conflicting civilization snapshot";return false;}var data=p.data();if(chunks[p.part()]!=null){if(Arrays.equals(chunks[p.part()],data))return true;drop();problem="Conflicting civilization fragment";return false;}chunks[p.part()]=data;received++;if(received<chunks.length)return true;try{byte[] all=new byte[length];for(int i=0;i<chunks.length;i++)System.arraycopy(chunks[i],0,all,i*ProgressPackets.CHUNK,chunks[i].length);var next=ProgressView.parse(new String(all,StandardCharsets.UTF_8));view=next;published=pending;drop();problem="";return true;}catch(RuntimeException error){drop();problem="Invalid civilization snapshot; retained previous complete view";return false;}}
}
