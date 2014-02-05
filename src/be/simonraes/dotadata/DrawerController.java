package be.simonraes.dotadata;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import be.simonraes.dotadata.fragment.LiveLeagueGamesFragment;
import be.simonraes.dotadata.fragment.LoadingFragment;
import be.simonraes.dotadata.fragment.OtherFragment;
import be.simonraes.dotadata.fragment.RecentGamesFragment;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.interfaces.ASyncResponseHistory;
import be.simonraes.dotadata.interfaces.ASyncResponseLiveLeague;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueContainer;
import be.simonraes.dotadata.parser.HistoryMatchParser;
import be.simonraes.dotadata.parser.LiveLeagueMatchParser;

public class DrawerController extends Activity implements ListView.OnItemClickListener, ASyncResponseHistory, ASyncResponseLiveLeague {

    private String listContent[];
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        listContent = new String[]{"Recent Games", "Hero stats", "divider here","Live league games", "Upcoming league games","divider", "Fantasy League"};

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.drawable.ic_drawer,R.string.drawer_open,R.string.drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle("Appname");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle("check it out");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listContent));
        drawerList.setOnItemClickListener(this);
        drawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //new be.simonraes.dotadata.fragment settings
        Fragment fragment;
        FragmentManager fm = getFragmentManager();

        //recent games
        if(position==0){
            //loadingfragment doesn't set title
            setActionBarTitle("Recent games");

            FragmentTransaction transaction = fm.beginTransaction();
            Fragment loadingFragment = new LoadingFragment();
            transaction.replace(R.id.content_frame, loadingFragment);
            transaction.commit();

            drawerLayout.closeDrawer(drawerList);
            HistoryMatchParser parser = new HistoryMatchParser(this);
            parser.execute();

        //live league games
        } else if(position==3){

            drawerLayout.closeDrawer(drawerList);
            LiveLeagueMatchParser parser = new LiveLeagueMatchParser(this);
            parser.execute();
        } else {
            fragment = new OtherFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            //change drawerLayout and actionbar
            setActionBarTitle(listContent[position]);
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);

            //change be.simonraes.dotadata.fragment
            transaction.addToBackStack(null).commit();
        }

    }

    //history be.simonraes.dotadata.parser finished
    @Override
    public void processFinish(HistoryContainer result) {
        Fragment fragment = new RecentGamesFragment();
        FragmentManager fm = getFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        //send object to be.simonraes.dotadata.fragment
        Bundle bundle=new Bundle();
        bundle.putSerializable("container", result);
        fragment.setArguments(bundle);

        transaction.addToBackStack(null).commit();
    }

    //live league games be.simonraes.dotadata.parser finished
    @Override
    public void processFinish(LiveLeagueContainer result) {
        FragmentManager fm = getFragmentManager();
        Fragment leagueFragment = new LiveLeagueGamesFragment();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, leagueFragment);

        //send object to be.simonraes.dotadata.fragment
        Bundle bundle=new Bundle();
        bundle.putSerializable("container", result);
        leagueFragment.setArguments(bundle);

        transaction.commit();
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



    private void setActionBarTitle(String title){
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

    //nodig om drawer icon (3 verticale strepen) in te stellen
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
