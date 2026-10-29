package org.humanjourney.core.water;
import java.util.*;
import java.util.function.Predicate;
import org.humanjourney.core.water.WaterPolicy.Quality;
/** Pure geometry/classification shared by runtime and unit tests. Ordered first-match regions. */
public final class WaterGeography {
    public record Point(double x,double z){
        public Point { if(!Double.isFinite(x)||!Double.isFinite(z)||Math.abs(x)>30000000||Math.abs(z)>30000000)throw new IllegalArgumentException("region coordinates outside world bounds"); }
    }
    public record Region(String id,String dimension,Quality quality,List<Point> polygon){
        public Region {
            polygon=List.copyOf(polygon);
            if(id.isBlank()||id.length()>128||polygon.size()<3||polygon.size()>256||new HashSet<>(polygon).size()!=polygon.size())throw new IllegalArgumentException("invalid region id/vertices");
            double area=0;for(int i=0;i<polygon.size();i++){Point a=polygon.get(i),b=polygon.get((i+1)%polygon.size());area+=a.x*b.z-b.x*a.z;}
            if(Math.abs(area)<1e-6)throw new IllegalArgumentException("degenerate region: "+id);
            for(int i=0;i<polygon.size();i++)for(int j=i+1;j<polygon.size();j++){
                if(j==i+1||(i==0&&j==polygon.size()-1))continue;
                if(intersects(polygon.get(i),polygon.get((i+1)%polygon.size()),polygon.get(j),polygon.get((j+1)%polygon.size())))throw new IllegalArgumentException("self-intersecting region: "+id);
            }
        }
        public boolean contains(double x,double z){
            boolean inside=false;Point p=new Point(x,z);
            for(int i=0,j=polygon.size()-1;i<polygon.size();j=i++){
                Point a=polygon.get(j),b=polygon.get(i);if(onSegment(a,b,p))return true;
                if((a.z>z)!=(b.z>z)&&x<(b.x-a.x)*(z-a.z)/(b.z-a.z)+a.x)inside=!inside;
            }return inside;
        }
    }
    private static double cross(Point a,Point b,Point p){return (b.x-a.x)*(p.z-a.z)-(b.z-a.z)*(p.x-a.x);}
    private static boolean onSegment(Point a,Point b,Point p){return Math.abs(cross(a,b,p))<1e-7&&p.x>=Math.min(a.x,b.x)&&p.x<=Math.max(a.x,b.x)&&p.z>=Math.min(a.z,b.z)&&p.z<=Math.max(a.z,b.z);}
    private static boolean intersects(Point a,Point b,Point c,Point d){return onSegment(a,b,c)||onSegment(a,b,d)||onSegment(c,d,a)||onSegment(c,d,b)||(cross(a,b,c)*cross(a,b,d)<0&&cross(c,d,a)*cross(c,d,b)<0);}
    public static Quality classify(boolean water,String dimension,double x,double z,boolean purified,boolean dirty,Predicate<String> biomeTag,WaterExtensions.Classification policy){
        return classify(water,dimension,x,z,purified,dirty,biomeTag,policy,()->false);
    }
    public static Quality classify(boolean water,String dimension,double x,double z,boolean purified,boolean dirty,Predicate<String> biomeTag,WaterExtensions.Classification policy,java.util.function.BooleanSupplier connectedOcean){
        if(!water)return null;
        for(Region r:policy.regions())if(r.dimension().equals(dimension)&&r.contains(x,z))return r.quality();
        if(purified)return Quality.PURIFIED;
        if(dirty)return Quality.DIRTY;
        return policy.saltTags().stream().anyMatch(biomeTag)||connectedOcean.getAsBoolean()?Quality.SALT:Quality.NORMAL;
    }
    private WaterGeography(){}
}
