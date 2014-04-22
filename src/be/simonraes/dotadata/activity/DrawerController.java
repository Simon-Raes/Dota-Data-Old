package be.simonraes.dotadata.activity;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
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
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.OrientationHelper;

public class DrawerController extends FragmentActivity implements ListView.OnItemClickListener {

    private String listContent[];
    public DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        mTitle = mDrawerTitle = getTitle();

        listContent = new String[]{"divider GAMES", "Recent Games", "divider STATISTICS", "Statistics", "Heroes", "Graphs", "divider SEARCH", "Find match"}; //"graphs"

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

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(this, listContent));
        drawerList.setOnItemClickListener(this);
        drawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        //start loading the content of the statistics spinners here so it's already ready when the user opens the stats screen
        PlayedHeroesMapper phm = PlayedHeroesMapper.getInstance(this);
        if (!AppPreferences.getAccountID(this).equals("0") && !AppPreferences.getAccountID(this).equals("") && AppPreferences.getAccountID(this) != null) {
            if (phm.getMaps().getPlayedHeroes().size() < 1) {
                if (phm.getStatus() != AsyncTask.Status.RUNNING) {
                    phm.execute();
                }
            }
        }


        if (savedInstanceState == null || savedInstanceState.getBoolean("appLaunch", true)) {

            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment(), "RecentGamesFragment").addToBackStack(null).commit();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("appLaunch", false);
    }

    //disable activity switch animation
    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        drawerLayout.closeDrawer(drawerList);
        drawerList.setItemChecked(position, true);

        switch (position) {
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment(), "RecentGamesFragment").addToBackStack(null).commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new StatsPagerFragment(), "StatsPagerFragment").addToBackStack(null).commit();
                break;
            case 4:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HeroesFragment(), "HeroesFragment").addToBackStack(null).commit();
                break;
            case 5:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new GraphFragment(), "GraphFragment").addToBackStack(null).commit();
                break;
            case 7:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SearchFragment(), "SearchFragment").addToBackStack(null).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment(), "RecentGamesFragment").addToBackStack(null).commit();
                break;
        }
    }

    /**
     * Sets the selected list item in the drawer (visual indicator only)
     */
    public void setActiveDrawerItem(int position) {
        drawerList.setItemChecked(position, true);
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
            MenuItem spinRanking = menu.findItem(R.id.spinRanking);
            if (spinRanking != null) {
                spinRanking.setVisible(false);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ManageUsersFragment(), "ManageUsersFragment").addToBackStack(null).commit();
                return true;

            //let aboutFragment handle this button if it is visible
            case R.id.btnAbout:
                AboutFragment myFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag("AboutFragment");
                if (myFragment != null) {
                    if (myFragment.isVisible()) {
                        return false;
                    } else {
                        drawerToggle.setDrawerIndicatorEnabled(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment(), "AboutFragment").addToBackStack(null).commit();
                        return true;
                    }
                } else {
                    drawerToggle.setDrawerIndicatorEnabled(true);
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new AboutFragment(), "AboutFragment").addToBackStack(null).commit();
                    return true;
                }


            default:
                return super.onOptionsItemSelected(item);
        }
        //return true;

    }

    //Sets the drawer icon in the actionbar title
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
        //remove the dialog on rotation, prevents 'not attached to window manager' error
        if (HistoryLoader.introDialog != null) {
            if (HistoryLoader.introDialog.isShowing()) {
                HistoryLoader.introDialog.dismiss();
            }
        }
    }
}
