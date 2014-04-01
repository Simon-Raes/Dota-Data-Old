package be.simonraes.dotadata.delegates;

/**
 * Created by Simon on 14/02/14.
 * User by the history loader, class that calls the parsers
 */
public interface ASyncResponseHistoryLoader {
    public void processFinish(boolean foundGames);
}
