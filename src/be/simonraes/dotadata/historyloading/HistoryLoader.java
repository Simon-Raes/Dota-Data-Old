package be.simonraes.dotadata.historyloading;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import be.simonraes.dotadata.database.*;
import be.simonraes.dotadata.detailmatch.*;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.historymatch.HistoryMatch;
import be.simonraes.dotadata.parser.DetailMatchesParser;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.InternetChecker;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by Simon on 13/02/14.
 * Controller for the download of the complete Dota 2 matchhistory.
 */
public class HistoryLoader implements HistoryMatchParser.ASyncResponseHistory, DetailMatchesParser.ASyncResponseDetailList, InternetChecker.ASyncResponseInternet {

    private String accountID;
    private ArrayList<HistoryMatch> matches;
    private HistoryMatchParser parser;

    private ASyncResponseHistoryLoader delegate;
    public interface ASyncResponseHistoryLoader {
        public void processFinish(boolean foundGames);
    }

    private Context context;

    private String latestSavedMatchID;

    private boolean goToDetailParser;
    private boolean firstTimeSetup;

    private InternetChecker checker;

    public static ProgressDialog introDialog;
    private ProgressDialog progressDialog;

    private User user;


    public HistoryLoader(Context context, ASyncResponseHistoryLoader delegate, User user) { //
        this.user = user;
        this.accountID = user.getAccount_id();

        this.delegate = delegate;
        this.context = context;

        goToDetailParser = false;
        firstTimeSetup = false;

        matches = new ArrayList<HistoryMatch>();
    }

    //overloaded constructor for use with non-active user accountID
    public HistoryLoader(Context context, ASyncResponseHistoryLoader delegate, String accountID) {
        this.accountID = accountID;

        this.delegate = delegate;
        this.context = context;

        goToDetailParser = false;
        firstTimeSetup = false;

        matches = new ArrayList<HistoryMatch>();
    }

    public void firstDownload() {
        firstTimeSetup = true;
        updateHistory();
    }

    public void updateHistory() {
        //get the most recent match from the database
        UsersDataSource uds = new UsersDataSource(context);
        if (user == null) {
            user = uds.getUserByID(accountID);
        }

        latestSavedMatchID = user.getLast_saved_match();
        if (latestSavedMatchID == null) {
            latestSavedMatchID = "0";
        }

        introDialog = ProgressDialog.show(context, "", "Searching for games.", true);

        //check if web service is available
        checker = new InternetChecker(this);
        checker.execute();
    }


    //finished checking status of webservice
    @Override
    public void processFinish(Boolean apiAvailable) {

        if (apiAvailable) {
            //start parser
            parser = new HistoryMatchParser(this);
            parser.execute(accountID);
        } else {

            Toast.makeText(context, "Could not connect to the Dota 2 API.", Toast.LENGTH_SHORT).show();
            if (introDialog.isShowing()) {
                introDialog.dismiss();
            }
            if(checker!=null && checker.getStatus()== AsyncTask.Status.RUNNING){
                checker.cancel(true);
                System.out.println(checker.getStatus());
            }
        }
    }

    //received next set of 100 history matches
    @Override
    public void processFinish(HistoryContainer result) {

        //todo: possible workaround for getting more than 500 games: hero_id parameter
        //can return 500 games per account per hero

        if (result.getRecentGames() != null) {

            //check if user is sharing his history
            if (result.getRecentGames().getStatusDetail() != null) {
                if (introDialog.isShowing()) {
                    introDialog.dismiss();
                }
                new AlertDialog.Builder(context)
                        .setTitle("No games found")
                        .setMessage("This user is not sharing his match history or has not yet played any games.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            } else if (result.getRecentGames().getMatches().size() > 0) {

                AppPreferences.setActiveAccountId(context, accountID);

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
                        Toast.makeText(context, "No new games found.", Toast.LENGTH_SHORT).show();
                        if (introDialog != null) {
                            if (introDialog.isShowing()) {
                                introDialog.dismiss();
                            }
                        }
                        goToDetailParser = false;
                        delegate.processFinish(false);
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

                if (introDialog != null) {
                    if (introDialog.isShowing()) {
                        introDialog.dismiss();
                    }
                }

                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Downloading match history");
                if (firstTimeSetup) {
                    //only show this message for the initial download
                    progressDialog.setMessage("Your Dota 2 match history is now downloading. Your games and statistics will be available once the download completes.");
                }
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
                DetailMatchesParser parser = new DetailMatchesParser(this, context, accountID);
                parser.execute(matchIDs);
            }
        } else {
            //API must be offline, alert user
            new AlertDialog.Builder(context)
                    .setTitle("WebAPI offline")
                    .setMessage("The Dota 2 API is currently unavailable. Please try again later.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            if (introDialog.isShowing()) {
                introDialog.dismiss();
            }
        }
    }


    /*Update progress indicator*/
    @Override
    public void processUpdate(Integer[] progress) {
        progressDialog.setProgress(progress[0]);

    }

    /*Finished parsing detailmatches, Save detailmatches to database*/
    @Override
    public void processFinish(ArrayList<DetailMatch> result) {

        UsersDataSource uds = new UsersDataSource(context);
        // Save the user if new / update the user if he is already in the database.
        User updatedUser = new User(user.getAccount_id(), user.getSteam_id(), user.getName(), user.getAvatar());
        updatedUser.setLast_saved_match(matches.get(0).getMatch_id());
        uds.saveUser(updatedUser);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (firstTimeSetup) {
            new AlertDialog.Builder(context)
                    .setTitle("Download finished")
                    .setMessage("Your matches and statistics are now available.")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        //alert delegate that all matches have been downloaded
        delegate.processFinish(true);
    }
}
