package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;

/**
 * Created by Simon on 19/02/14.
 */
public interface ASyncResponsePlayerSummary {
    public void processFinish(PlayerSummaryContainer result);
}
