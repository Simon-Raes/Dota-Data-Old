package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 14/02/14.
 * User by the history loader, class that calls the parsers
 */
public interface ASyncResponseHistoryLoader {
    public void processFinish(boolean foundGames);
}
