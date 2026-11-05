package org.humanjourney.core.npc.sereneseasons;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.SeasonHelper;
import java.util.Locale;
/** Exact installed 21.8.0.0 public API; no season-driven balance decisions. */
public final class SereneSeasons218Adapter {
    public static String season(Level level){if(level==null||SeasonHelper.dataProvider==null)return null;var state=SeasonHelper.getSeasonState(level);return state==null||state.getSeason()==null?null:state.getSeason().name().toLowerCase(Locale.ROOT);}
    private SereneSeasons218Adapter(){}
}
