package be.simonraes.dotadata;

import android.app.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.adapter.DrawerAdapter;
import be.simonraes.dotadata.delegates.ASyncResponseLiveLeague;
import be.simonraes.dotadata.fragment.*;
import be.simonraes.dotadata.liveleaguegame.LiveLeagueContainer;

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
                "Upcoming league games", "divider FANTASY LEAGUE", "Fantasy League"};

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
        drawerLayout.closeDrawer(drawerList);
        drawerList.setItemChecked(position, true);

        if (position == 1) {
            fragment = new RecentGamesFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            transaction.addToBackStack(null).commit();

        } else if (position == 2) {
            fragment = new StatsFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            transaction.addToBackStack(null).commit();

        } else if (position == 4) {
            fragment = new LiveLeagueGamesFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            transaction.addToBackStack(null).commit();

        } else {
            fragment = new NYIFragment();

            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);

            transaction.addToBackStack(null).commit();
        }
    }


    //live league games parser finished
    @Override
    public void processFinish(LiveLeagueContainer result) {

    }


    private void setActionBarTitle(String title) {
        getActionBar().setTitle(title);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
        if (btnRefresh != null) {
            btnRefresh.setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (item.getItemId()) {
            case R.id.btnSettings:
                Fragment settingsFragment = new SettingsFragment();

                transaction.replace(R.id.content_frame, settingsFragment);

                transaction.addToBackStack(null).commit();
                break;

            case R.id.btnAbout:
                Fragment aboutFragment = new NYIFragment();

                transaction.replace(R.id.content_frame, aboutFragment);

                transaction.addToBackStack(null).commit();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //remove history notification (in case app was terminated during downloading)
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1010);
    }
}
