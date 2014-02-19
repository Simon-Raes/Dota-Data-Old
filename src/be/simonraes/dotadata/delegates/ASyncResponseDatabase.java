package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 */
public interface ASyncResponseDatabase {
    public void processFinish(ArrayList<DetailMatch> detailMatches);
}
