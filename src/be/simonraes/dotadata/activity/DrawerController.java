package be.simonraes.dotadata.activity;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.*;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.DrawerAdapter;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.fragment.*;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.ImageLoadListener;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.OrientationHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DrawerController extends FragmentActivity implements ListView.OnItemClickListener {

    private String listContent[];
    public DrawerLayout drawerLayout;
    private LinearLayout drawerSlider;
    private ImageView imgAvatar;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.drawer_layout);

        mTitle = mDrawerTitle = getTitle();

        listContent = new String[]{"divider GAMES", "Recent Games", "divider STATISTICS", "Statistics", "Heroes", "Graphs", "divider SEARCH", "Find match"}; //"graphs"

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        drawerSlider = (LinearLayout) findViewById(R.id.left_drawer);

        if (!AppPreferences.getActiveAccountId(this).equals("")) {
            setActiveUserInDrawer(AppPreferences.getActiveAccountId(this));
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getActionBar() != null) getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getActionBar() != null) getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        drawerList = (ListView) findViewById(R.id.left_drawer_list);
        drawerList.setAdapter(new DrawerAdapter(this, listContent));
        drawerList.setOnItemClickListener(this);
        drawerList.setBackgroundColor(getResources().getColor(android.R.color.background_light));

        //start loading the content of the statistics spinners here so it's already ready when the user opens the stats screen
        PlayedHeroesMapper phm = PlayedHeroesMapper.getInstance(this);
        if (!AppPreferences.getActiveAccountId(this).equals("0") && !AppPreferences.getActiveAccountId(this).equals("") && AppPreferences.getActiveAccountId(this) != null) {
            if (PlayedHeroesMapper.getMaps().getPlayedHeroes().size() < 1) {
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

        drawerLayout.closeDrawer(drawerSlider);
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


    public void newUserAddedOrSelected(String userAccountID){

        // Set app-wide account ID to this user
        AppPreferences.setActiveAccountId(this, userAccountID);

        //update the playedheroes/gamemodes maps for this new user
        PlayedHeroesMapper.clearInstance();
        PlayedHeroesMapper phm = PlayedHeroesMapper.getInstance(this);
        if (PlayedHeroesMapper.getMaps().getPlayedHeroes().size() < 1) {
            phm.execute();
        }

        //set user as active in the app drawer
        setActiveUserInDrawer(userAccountID);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment(), "RecentGamesFragment").addToBackStack(null).commitAllowingStateLoss();
        OrientationHelper.unlockOrientation(this);
    }

    public void setActiveUserInDrawer(String accountId) {

        UsersDataSource uds = new UsersDataSource(this);
        User user = uds.getUserByID(accountId);
        imgAvatar = (ImageView) findViewById(R.id.imgDrawerSliderAvatar);
        //user avatar
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoadListener animateFirstListener = new ImageLoadListener();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader.displayImage(user.getAvatar(), imgAvatar, options, animateFirstListener);

        TextView txtName = (TextView) findViewById(R.id.txtDrawerSliderName);
        txtName.setText(user.getName());
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
        if (drawerLayout.isDrawerOpen(drawerSlider)) {


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
        if (getActionBar() != null) getActionBar().setTitle(mTitle);
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

            // let aboutFragment handle this button if it is visible
            case R.id.btnAbout:
                AboutFragment aboutFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag("AboutFragment");
                if (aboutFragment != null) {
                    if (aboutFragment.isVisible()) {
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
