package be.simonraes.dotadata.interfaces;

import be.simonraes.dotadata.leaguelisting.LeagueListingContainer;

/**
 * Created by Simon on 5/02/14.
 */
public interface ASyncResponseLeagueListing {
    void processFinish(LeagueListingContainer result);
}
