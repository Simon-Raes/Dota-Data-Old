package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.historymatch.HistoryContainer;

/**
 * Created by Simon on 24/01/14.
 */
public interface ASyncResponseHistory {
    void processFinish(HistoryContainer result);
}
