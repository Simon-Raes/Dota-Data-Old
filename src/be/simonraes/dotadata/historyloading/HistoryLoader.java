package be.simonraes.dotadata.historyloading;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDetailList;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.parser.DetailMatchesParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;

import java.util.ArrayList;

/**
 * Created by Simon on 13/02/14.
 * Controller for the download of the complete Dota 2 matchhistory
 */
public class HistoryLoader implements ASyncResponseHistory, ASyncResponseDetailList {

    private String accountID;
    private ArrayList<HistoryMatch> matches;
    private HistoryMatchParser parser;
    private ASyncResponseHistoryLoader delegate;
    private Context context;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private String latestSavedMatchID;
    private boolean stopDownloading;

    public HistoryLoader(ASyncResponseHistoryLoader delegate, Context context, String accountID) {
        this.accountID = accountID;
        this.delegate = delegate;
        this.context = context;

        matches = new ArrayList<HistoryMatch>();
    }

    public void updateHistory() {

        //get the most recent match from the database
        MatchesDataSource mds = new MatchesDataSource(context);
        latestSavedMatchID = mds.getLatestMatch().getMatch_id();
        if (latestSavedMatchID == null) {
            latestSavedMatchID = "0";
        }
        stopDownloading = false;

        //todo: use last match ID to check if a match should be downloaded+saved or not, only download new matches


        //start notification
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Downloading Dota 2 history")
                .setContentText("Starting download...")
                .setSmallIcon(R.drawable.dd_sm);
        mNotifyManager.notify(1010, mBuilder.build());

        //start parser
        parser = new HistoryMatchParser(this);
        parser.execute(accountID);
    }

    @Override
    public void processFinish(HistoryContainer result) {
        //todo: needs to use date_max instead of start_at_matchid (which is capped at latest 500 matches),
        // date_max is currently broken (http://dev.dota2.com/showthread.php?t=125875&highlight=date_max)


        System.out.println("got next set, size is now " + matches.size());

        if (result.getRecentGames().getMatches().size() > 0 && !stopDownloading) {
            if (Integer.parseInt(result.getRecentGames().getMatches().get(result.getRecentGames().getMatches().size() - 1).getMatch_id()) < Integer.parseInt(latestSavedMatchID)) {
                //last match id in received results is older than latest saved match, saved matchID is in this set of results

                System.out.println("last stored match is in this set, stop parsing");

                stopDownloading = true; //this will be the last set of downloaded historymatches

                ArrayList<HistoryMatch> recentMatches = new ArrayList<HistoryMatch>();
                //only keep the newer matches, discard the rest
                for (HistoryMatch match : result.getRecentGames().getMatches()) {
                    if (Integer.parseInt(match.getMatch_id()) > Integer.parseInt(latestSavedMatchID)) {

                        System.out.println("matchID " + match.getMatch_id() + " is more recent than last saved match (" + latestSavedMatchID + ") adding to list");

                        recentMatches.add(match);
                    }
                }
                if (recentMatches.size() == 0) {
                    updateNotification("No new games found.", 0, 0, false);

                }
                matches.addAll(recentMatches);

            } else {
                //stored matchID is not in this set, download the next one

                System.out.println("need next set of history results, starting parser");

                matches.addAll(result.getRecentGames().getMatches());
                //start parser for next page of results
                parser = new HistoryMatchParser(this);

                int iNextMatchID = Integer.parseInt(result.getRecentGames().getMatches().get(result.getRecentGames().getMatches().size() - 1).getMatch_id()) - 1;
                String nextMatchID = Integer.toString(iNextMatchID);

                parser.execute(accountID, nextMatchID);
            }

        } else {
            //got all historymatches
            String[] matchIDs = new String[matches.size()];

            for (int i = 0; i < matches.size(); i++) {
                matchIDs[i] = matches.get(i).getMatch_id();
            }
            DetailMatchesParser parser = new DetailMatchesParser(this);
            parser.execute(matchIDs);
        }


    }


    /*Update notification progress bar*/
    @Override
    public void processUpdate(Integer[] progress) {
        mBuilder.setContentText(progress[0] + " of " + matches.size() + " matches downloaded.");
        mBuilder.setProgress(matches.size(), progress[0], false);
        Notification progressNotification = mBuilder.build();
        //set notification as ongoing event (can't be removed from notifications)
        progressNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotifyManager.notify(1010, progressNotification);

    }

    /*Save detailmatches to database*/
    @Override
    public void processFinish(ArrayList<DetailMatch> result) {
        updateNotification("Saving...", 0, 0, false);

        //save matches to database
        MatchesDataSource mds = new MatchesDataSource(context);
        mds.saveDetailMatches(result);

        updateNotification("Download complete.", 0, 0, false);

    }

    private void updateNotification(String title, int progress, int maxProgress, boolean isFixed) {
        mBuilder.setContentText(title)
                .setProgress(progress, maxProgress, false);
        mNotifyManager.notify(1010, mBuilder.build());
    }
}
