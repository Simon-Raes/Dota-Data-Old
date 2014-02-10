package be.simonraes.dotadata;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.adapter.DrawerAdapter;
import be.simonraes.dotadata.fragment.*;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.interfaces.ASyncResponseHistory;
import be.simonraes.dotadata.interfaces.ASyncResponseLiveLeague;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueContainer;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.parser.LiveLeagueMatchParser;

public class DrawerController extends Activity implements ListView.OnItemClickListener, ASyncResponseLiveLeague {

    private String listContent[];
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.drawer_layout);

        listContent = new String[]{"divider MY GAMES", "Recent Games", "Hero stats", "divider LEAGUE GAMES", "Live league games",
                "Upcoming league games", "divider FANTASY LEAGUE", "Fantasy League", "divider APPLICATION", "Settings", "About"};

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(this, listContent));
        drawerList.setOnItemClickListener(this);
        drawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Fragment fragment;
        FragmentManager fm = getFragmentManager();

        if (position == 1) {
            drawerLayout.closeDrawer(drawerList);

            Fragment recentGamesFragment = new RecentGamesFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, recentGamesFragment);

            transaction.commit();

        } else if (position == 4) {
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);

            LiveLeagueMatchParser parser = new LiveLeagueMatchParser(this);
            parser.execute();

        } else if (position == 9) {
            fragment = new SettingsFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);

            transaction.commit();

        } else {
            fragment = new OtherFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            //change drawerLayout and actionbar
            setActionBarTitle(listContent[position]);
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);

            transaction.addToBackStack(null).commit();
        }
    }


    //live league games parser finished
    @Override
    public void processFinish(LiveLeagueContainer result) {
        FragmentManager fm = getFragmentManager();
        Fragment leagueGamesFragment = new LiveLeagueGamesFragment();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, leagueGamesFragment);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("container", result);
        leagueGamesFragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    private void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //used for setting drawer icon in actionbar
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


}
