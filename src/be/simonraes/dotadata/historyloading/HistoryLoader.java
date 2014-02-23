package be.simonraes.dotadata.historyloading;

import android.app.Notification;
//import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
//import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.database.PicksBansDataSource;
import be.simonraes.dotadata.database.PlayersInMatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseDetailList;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.delegates.ASyncResponseInternet;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.detailmatch.PicksBans;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.parser.DetailMatchesParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.util.InternetChecker;

import java.util.ArrayList;

/**
 * Created by Simon on 13/02/14.
 * Controller for the download of the complete Dota 2 matchhistory
 */
public class HistoryLoader implements ASyncResponseHistory, ASyncResponseDetailList, ASyncResponseInternet {

    private String accountID;
    private ArrayList<HistoryMatch> matches;
    private HistoryMatchParser parser;
    private ASyncResponseHistoryLoader delegate;
    private Context context;

//    private NotificationManager mNotifyManager;
//    private NotificationCompat.Builder mBuilder;

    private String latestSavedMatchID;

    private boolean goToDetailParser;

    private ProgressDialog introDialog;
    private ProgressDialog progressDialog;

    public HistoryLoader(Context context, ASyncResponseHistoryLoader delegate) { //
        this.accountID = PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", "");


        this.delegate = delegate;
        this.context = context;

        goToDetailParser = false;

        matches = new ArrayList<HistoryMatch>();
    }

    public void firstDownload() {
        //clear previous database
        //context.deleteDatabase("be.simonraes.dotadata.db");
        //start download as usual
        updateHistory();
    }

    public void updateHistory() {

        //get the most recent match from the database
        MatchesDataSource mds = new MatchesDataSource(context, accountID);
        latestSavedMatchID = mds.getLatestMatch().getMatch_id();
        if (latestSavedMatchID == null) {
            latestSavedMatchID = "0";
        }

//        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(context);

        introDialog = ProgressDialog.show(context, "", "Checking for new games.", true);

        //check if web service is available
        InternetChecker checker = new InternetChecker(this);
        checker.execute();
    }

    //finished checking status of webservice
    @Override
    public void processFinish(Boolean result) {


        if (result) {
            //start notification
//            mBuilder.setContentTitle("Downloading Dota 2 history")
//                    .setContentText("Starting download...")
//                    .setTicker("Starting download...")
//                    .setSmallIcon(R.drawable.dotadata_xsm);

            //start parser
            parser = new HistoryMatchParser(this);
//            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("be.simonraes.dotadata.downloadinprogress", true).commit();
            parser.execute(accountID);
        } else {
//            mBuilder.setContentTitle("Dota 2 webservice unavailable")
//                    .setContentText("Please try again later.")
//                    .setTicker("Dota 2 webservice unavailable")
//                    .setSmallIcon(R.drawable.dotadata_xsm);

            introDialog.dismiss();
        }
//        mNotifyManager.notify(1010, mBuilder.build());
    }

    //received next set of 100 history matches
    @Override
    public void processFinish(HistoryContainer result) {
        //todo: needs to use date_max instead of start_at_matchid (which is capped at latest 500 matches),
        // date_max is currently broken (http://dev.dota2.com/showthread.php?t=125875&highlight=date_max)


        if (result.getRecentGames().getMatches().size() > 0) {
            if (Integer.parseInt(result.getRecentGames().getMatches().get(result.getRecentGames().getMatches().size() - 1).getMatch_id()) < Integer.parseInt(latestSavedMatchID)) {
                //last match id of received results is older than latest saved match, saved matchID is in this set of results, this is the last needed set

                goToDetailParser = true;

                ArrayList<HistoryMatch> recentMatches = new ArrayList<HistoryMatch>();
                //only keep the newer matches, discard the rest
                for (HistoryMatch match : result.getRecentGames().getMatches()) {
                    if (Integer.parseInt(match.getMatch_id()) > Integer.parseInt(latestSavedMatchID)) {
                        recentMatches.add(match);
                    }
                }

                if (recentMatches.size() == 0) {
                    //latest downloaded match was the same as the latest saved match, no games were played since last download
//                    updateNotification("No new games found.", 0, 0, false, false);
                    Toast.makeText(context, "No new games found.", Toast.LENGTH_SHORT).show();
//                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("be.simonraes.dotadata.downloadinprogress", false).commit();
                    introDialog.dismiss();

                    goToDetailParser = false;
                }

                matches.addAll(recentMatches);

            } else {
                //stored matchID is not in this set, download the next one
                goToDetailParser = false;

                matches.addAll(result.getRecentGames().getMatches());
                //start parser for next page of results
                parser = new HistoryMatchParser(this);

                int iNextMatchID = Integer.parseInt(result.getRecentGames().getMatches().get(result.getRecentGames().getMatches().size() - 1).getMatch_id()) - 1;
                String nextMatchID = Integer.toString(iNextMatchID);

                parser.execute(accountID, nextMatchID);
            }

        } else {
            //latest set contained no matches, end of history reached, send matches to detailparser
            goToDetailParser = true;
        }


        if (goToDetailParser) {
            //got all historymatches

//            System.out.println("got all historymatches, sending them to detailparser");

            introDialog.dismiss();

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Downloading match history");
            progressDialog.setProgressPercentFormat(null);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(matches.size());
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            String[] matchIDs = new String[matches.size()];

            for (int i = 0; i < matches.size(); i++) {
                matchIDs[i] = matches.get(i).getMatch_id();
            }
            DetailMatchesParser parser = new DetailMatchesParser(this);
            parser.execute(matchIDs);
        }
    }


    /*Update progress indicator*/
    @Override
    public void processUpdate(Integer[] progress) {
//        mBuilder.setContentText(progress[0] + " of " + matches.size() + " matches downloaded.");
//        mBuilder.setProgress(matches.size(), progress[0], false);
//        Notification progressNotification = mBuilder.build();
//        //set notification as ongoing event (can't be removed from notifications)
//        progressNotification.flags |= Notification.FLAG_ONGOING_EVENT;
//        mNotifyManager.notify(1010, progressNotification);


        //introDialog.setProgressPercentFormat(null);
        progressDialog.setProgress(progress[0]);
    }

    /*Finished parsing detailmatches, Save detailmatches to database*/
    @Override
    public void processFinish(ArrayList<DetailMatch> result) {
//        updateNotification("Saving...", 0, 0, false, false);


        //save players and (if needed) picks/bans to database
        ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
        ArrayList<PicksBans> picksBansList = new ArrayList<PicksBans>();

        for (DetailMatch match : result) {
            //get victory or loss
            for (DetailPlayer player : match.getPlayers()) {
                if (player.getAccount_id() != null) {
                    if (player.getAccount_id().equals(accountID)) {
                        //set victory/loss here while we have the user's player info
                        if ((Integer.parseInt(player.getPlayer_slot()) < 100 && match.getRadiant_win()) || (Integer.parseInt(player.getPlayer_slot()) > 100 && !match.getRadiant_win())) {
                            //player won
                            match.setUser_win(true);
                        } else {
                            match.setUser_win(false);
                        }
                    }
                }
                //get players
                player.setMatchID(match.getMatch_id());
                players.add(player);
            }
            //get picksbans
            if (match.getPicks_bans().size() > 0) {
                for (PicksBans picksBans : match.getPicks_bans()) {
                    picksBans.setMatch_id(match.getMatch_id());
                    picksBansList.add(picksBans);
                }
            }
        }
        //save matches to database
        MatchesDataSource mds = new MatchesDataSource(context, accountID);
        mds.saveDetailMatches(result);
        //save players
        PlayersInMatchesDataSource pimds = new PlayersInMatchesDataSource(context);
        pimds.savePlayers(players);
        //save picksbans
        PicksBansDataSource pbds = new PicksBansDataSource(context);
        pbds.savePicksBansList(picksBansList);

//        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("be.simonraes.dotadata.downloadinprogress", false).commit();
//        updateNotification("Download complete.", 0, 0, false, true);

        introDialog.dismiss();

        //alert delegate that all matches have been downloaded
        delegate.processFinish();

    }

//    private void updateNotification(String title, int progress, int maxProgress, boolean isFixed, boolean clickable) {
//        if (clickable) {
//            Intent resultIntent = new Intent(context, DrawerController.class);
//            PendingIntent resultPendingIntent =
//                    PendingIntent.getActivity(
//                            context,
//                            0,
//                            resultIntent,
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                    );
//
//            mBuilder.setContentText(title)
//                    .setTicker(title)
//                    .setSmallIcon(R.drawable.dotadata_xsm)
//                    .setAutoCancel(true)
//                    .setContentIntent(resultPendingIntent)
//                    .setProgress(progress, maxProgress, false);
//            mNotifyManager.notify(1010, mBuilder.build());
//
//        } else {
//            mBuilder.setContentText(title)
//                    .setTicker(title)
//                    .setAutoCancel(true)
//                    .setSmallIcon(R.drawable.dotadata_xsm)
//                    .setProgress(progress, maxProgress, false);
//            mNotifyManager.notify(1010, mBuilder.build());
//        }
//
//
//    }


}
