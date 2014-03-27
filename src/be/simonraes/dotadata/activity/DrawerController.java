package be.simonraes.dotadata.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.DrawerAdapter;
import be.simonraes.dotadata.fragment.*;
import be.simonraes.dotadata.util.OrientationHelper;

public class DrawerController extends Activity implements ListView.OnItemClickListener {

    private String listContent[];
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private boolean appLaunch = true;
    private String previousActionBarTitle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.drawer_layout);

        mTitle = mDrawerTitle = getTitle();

        listContent = new String[]{"divider MY GAMES", "Recent Games", "Statistics"};

//        listContent = new String[]{"divider MY GAMES", "Recent Games", "Statistics", "divider LEAGUE GAMES", "Live league games",
//                "Upcoming league games", "divider FANTASY LEAGUE", "Fantasy League"};

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
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

        if (savedInstanceState == null || savedInstanceState.getBoolean("appLaunch", true)) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new RecentGamesFragment()).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("appLaunch", false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        drawerLayout.closeDrawer(drawerList);
        drawerList.setItemChecked(position, true);

        switch (position) {
            case 1:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment(), "RecentGamesFragment").addToBackStack(null).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new StatsFragment(), "StatsFragment").addToBackStack(null).commit();
                break;
//            case 3:
//                getFragmentManager().beginTransaction().replace(R.id.content_frame, new LiveLeagueGamesFragment(), "LiveLeagueGamesFragment").addToBackStack(null).commit();
//                break;

            default:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new NYIFragment(), "NYIFragment").addToBackStack(null).commit();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //turn on the Navigation Drawer image; this is called in the LowerLevelFragments
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return drawerToggle;
    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        if (drawerLayout.isDrawerOpen(drawerList)) {


            MenuItem btnRefresh = menu.findItem(R.id.btnRefresh);
            if (btnRefresh != null) {
                btnRefresh.setVisible(false);
            }
            MenuItem btnFavourite = menu.findItem(R.id.btnFavourite);
            if (btnFavourite != null) {
                btnFavourite.setVisible(false);
            }
            MenuItem btnNote = menu.findItem(R.id.btnNote);
            if (btnNote != null) {
                btnNote.setVisible(false);
            }
            MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
            if (spinHeroes != null) {
                spinHeroes.setVisible(false);
            }
            MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
            if (spinGameModes != null) {
                spinGameModes.setVisible(false);
            }

        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        switch (item.getItemId()) {
            case R.id.btnManageUsers:
                drawerToggle.setDrawerIndicatorEnabled(true);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new ManageUsersFragment(), "ManageUsersFragment").addToBackStack(null).commit();
                break;


            case R.id.btnAbout:
                drawerToggle.setDrawerIndicatorEnabled(true);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment(), "AboutFragment").addToBackStack(null).commit();
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
        OrientationHelper.unlockOrientation(this);
    }
}
