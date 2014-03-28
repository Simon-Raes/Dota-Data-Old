package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.detailmatch.DetailMatchLite;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 27/03/2014.
 */
public interface ASyncResponseStatsLoader {
    public void processFinish(ArrayList<DetailMatchLite> result);
}
