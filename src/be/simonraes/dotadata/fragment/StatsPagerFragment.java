package be.simonraes.dotadata.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Spinner;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.GameModeSpinnerAdapter;
import be.simonraes.dotadata.adapter.HeroSpinnerAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseStatsLoader;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.util.AppPreferences;
import be.simonraes.dotadata.util.GameModes;
import be.simonraes.dotadata.util.HeroList;
import be.simonraes.dotadata.util.OrientationHelper;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon Raes on 16/04/2014.
 */
public class StatsPagerFragment extends Fragment implements AdapterView.OnItemSelectedListener, ASyncResponseStatsLoader {

    private Spinner spinnerHeroes;
    private Spinner spinnerGameModes;
    //selected index in the spinner
    private int heroSelection, gameModeSelection;
    //ID of the selected item in the spinner
    private String gameModeID, heroID;

    private ArrayList<DetailMatchLite> matches;
    private HashMap<String, Integer> gameModesMap; //contains played gamemodes, count
    private HashMap<String, String> mapGameModeIDName; //contains played gamemodesIDs, gamemodenames
    private HashMap<String, Integer> heroesMap; //contains played heroes, count
    private HashMap<String, String> mapHeroIDName; //contains played heroesID, heronames

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private StatsMatchesLoader sml;

    long lastUpdate = 0;

    StatsNumbersForPagerFragment statsFrag;
    StatsMatchesFragment matchFrag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            System.out.println("come from state");
            gameModeSelection = savedInstanceState.getInt("gameModeSelection");
            heroSelection = savedInstanceState.getInt("heroSelection");
            matches = savedInstanceState.getParcelableArrayList("matches");
            mapHeroIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapHeroes");
            mapGameModeIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapGameModes");
            gameModeID = savedInstanceState.getString("gameModeID");
            heroID = savedInstanceState.getString("heroID");
        } else {
            System.out.println("come not from state");

            gameModeSelection = -1;
            heroSelection = -1;
            //ID of the active item in the spinners
            gameModeID = "-1";
            heroID = "-1";
        }

        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, null);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(1);

        getActivity().setTitle("Statistics");

//        if (matches == null) {
        updateMatches();
//        }


        //update here on first load of the page
//        if (gameModeID.equals("-1") && heroID.equals("-1")) {
//            updateMatches();
//        }

        setHasOptionsMenu(true);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt("gameModeSelection", gameModeSelection);
        outState.putInt("heroSelection", heroSelection);
        outState.putParcelableArrayList("matches", matches);
        outState.putSerializable("mapHeroes", mapHeroIDName);
        outState.putSerializable("mapGameModes", mapGameModeIDName);
        outState.putString("gameModeID", gameModeID);
        outState.putString("heroID", heroID);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_menu, menu);

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
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(true);
            spinnerGameModes = (Spinner) spinGameModes.getActionView();
            if (spinnerGameModes != null) {

                //only show the played heroes in the dropdownlist
                if (mapHeroIDName == null) {
                    getPlayedHeroesAndGameModes();
                } else if (mapHeroIDName.size() < 1) {
                    getPlayedHeroesAndGameModes();
                }

                spinnerGameModes.setAdapter(new GameModeSpinnerAdapter(getActivity(), (HashMap<String, String>) mapGameModeIDName.clone()));
                spinnerGameModes.setSelection(0, false); //itemselected won't fire on first load with this
                spinnerGameModes.setOnItemSelectedListener(this);

                //if there was a savedState, set spinner selection
                if (gameModeSelection >= 0) {
                    spinnerGameModes.setSelection(gameModeSelection);
                }
            }
        }
        MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
        if (spinHeroes != null) {
            spinHeroes.setVisible(true);
            spinnerHeroes = (Spinner) spinHeroes.getActionView();
            if (spinnerHeroes != null) {

                //only show the played heroes in the dropdownlist
                if (mapHeroIDName == null) {
                    getPlayedHeroesAndGameModes();
                } else if (mapHeroIDName.size() < 1) {
                    getPlayedHeroesAndGameModes();
                }

                spinnerHeroes.setAdapter(new HeroSpinnerAdapter(getActivity(), (HashMap<String, String>) mapHeroIDName.clone()));
                spinnerHeroes.setSelection(0, false); //itemselected won't fire on first load with this
                spinnerHeroes.setOnItemSelectedListener(this);

                //if there was a savedState, set spinner selection
                if (heroSelection >= 0) {
                    spinnerHeroes.setSelection(heroSelection);
                }

            }
        }

        lastUpdate = System.currentTimeMillis();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemSelected");

        switch (parent.getId()) {
            case R.id.spinGameModes:
                System.out.println("gamemode selected");

                GameModeSpinnerAdapter gAdapter = (GameModeSpinnerAdapter) parent.getAdapter();
                gameModeID = gAdapter.getIDForPosition(position);
                gameModeSelection = spinnerGameModes.getSelectedItemPosition();

                if (System.currentTimeMillis() - lastUpdate > 100) {

                    if (sml != null) {
                        if (sml.getStatus() != AsyncTask.Status.RUNNING) {
                            updateMatches();
                        }
                    } else {
                        updateMatches();
                    }

                    lastUpdate = System.currentTimeMillis();

                    if (matches != null) {
                        if (matches.size() > 0) {
                            if (sml != null) {
                                if (sml.getStatus() != AsyncTask.Status.RUNNING) {
                                    ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                                }
                            } else {
                                ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                            }


                        }
                    }
                }
                //lastUpdate = System.currentTimeMillis();

                break;
            case R.id.spinHeroes:
                System.out.println("hero selected");

                HeroSpinnerAdapter adapter = (HeroSpinnerAdapter) parent.getAdapter();
                heroID = adapter.getIDForPosition(position);
                heroSelection = spinnerHeroes.getSelectedItemPosition();

                //only update is there was at least .1 second between the 2 spinners (prevents both updating automatically)
                if (System.currentTimeMillis() - lastUpdate > 100) {

                    System.out.println("didn't go to details, reeeload");
                    if (sml != null) {
                        if (sml.getStatus() != AsyncTask.Status.RUNNING) {
                            updateMatches();
                        }
                    } else {
                        updateMatches();
                    }

                    if (matches != null) {
                        if (matches.size() > 0) {
                            if (sml != null) {
                                if (sml.getStatus() != AsyncTask.Status.RUNNING) {
                                    ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                                }
                            } else {
                                ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                            }
                        }
                    }
                } else {
                    System.out.println("not time yet heroes");
                }


                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*Gets new data for the selected filters*/
    private void updateMatches() {
        System.out.println("updating matches");
        //lock orientation during loading
        OrientationHelper.lockOrientation(getActivity());

        sml = new StatsMatchesLoader(this, getActivity());
        sml.execute(gameModeID, heroID);
    }

    private void getPlayedHeroesAndGameModes() {
        System.out.println("getPlayedHeroesAndGameModes");

        mapHeroIDName = PlayedHeroesMapper.getMaps().getPlayedHeroes();
        mapGameModeIDName = PlayedHeroesMapper.getMaps().getPlayedGameModes();

        if (mapHeroIDName == null || mapGameModeIDName == null) {
            System.out.println("map empty for some reason");
            MatchesDataSource mds = new MatchesDataSource(getActivity(), AppPreferences.getAccountID(getActivity()));
            mapHeroIDName = new HashMap<String, String>();
            mapGameModeIDName = new HashMap<String, String>();
            for (DetailMatchLite rec : mds.getAllRealDetailMatchesLite()) {
                mapHeroIDName.put(rec.getHero_id(), HeroList.getHeroName(rec.getHero_id()));
                mapGameModeIDName.put(rec.getGame_mode(), GameModes.getGameMode(rec.getGame_mode()));
            }
        }
    }

    @Override
    public void processFinish(ArrayList<DetailMatchLite> result) {
        matches = result;

        if (mPagerAdapter != null) {
            if (((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0) != null) {
                ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).setMatches(result, gameModeID, heroID);
                ((StatsNumbersForPagerFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
            }
            if (((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(1) != null) {
                ((StatsMatchesFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(1)).setMatches(result);
            }
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StatsNumbersForPagerFragment();
                case 1:
                    return new StatsMatchesFragment();
                default:
                    return new StatsMatchesFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Stats";
                case 1:
                    return "Matches";
                default:
                    return "error";
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
