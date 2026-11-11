package org.humanjourney.core.geography;
import java.util.Set;
public record GeographyRoles(boolean participant,boolean student,boolean teacher){
 public static GeographyRoles from(Set<String> tags){return new GeographyRoles(tags.contains("hj_participant"),tags.contains("hj_student"),tags.contains("hj_teacher"));}
 public boolean canDiscover(){return participant&&student&&!teacher;}
}
