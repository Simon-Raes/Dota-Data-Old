package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.statistics.DetailMatchLite;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 * Used to alert listener that the matches have been loaded from the database.
 */
public interface ASyncResponseDatabase {
    public void processFinish(ArrayList<DetailMatchLite> detailMatches);
}
