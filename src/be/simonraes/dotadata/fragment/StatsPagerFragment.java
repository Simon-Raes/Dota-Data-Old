package be.simonraes.dotadata.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Spinner;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.GameModeSpinnerAdapter;
import be.simonraes.dotadata.adapter.HeroSpinnerAdapter;
import be.simonraes.dotadata.adapter.ScreenSlidePagerAdapter;
import be.simonraes.dotadata.async.StatsMatchesLoader;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Simon Raes on 16/04/2014.
 * Disastrous code ahead.
 */
public class StatsPagerFragment extends Fragment implements AdapterView.OnItemSelectedListener, StatsMatchesLoader.ASyncResponseStatsLoader {

    private Spinner spinnerHeroes;
    private Spinner spinnerGameModes;
    //selected index in the spinner
    private int heroSelection, gameModeSelection;
    //ID of the selected item in the spinner
    private String gameModeID, heroID;
    //will be filled with Hero Index if this page got opened by clicking on a hero in the heroes list
    private int pos;

    private ArrayList<DetailMatchLite> matches;
    private HashMap<String, Integer> gameModesMap; //contains played gamemodes, count
    private HashMap<String, String> mapGameModeIDName; //contains played gamemodesIDs, gamemodenames
    private HashMap<String, Integer> heroesMap; //contains played heroes, count
    private HashMap<String, String> mapHeroIDName; //contains played heroesID, heronames

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private StatsMatchesLoader sml;

    long lastUpdate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            gameModeSelection = savedInstanceState.getInt("gameModeSelection");
            heroSelection = savedInstanceState.getInt("heroSelection");
            matches = savedInstanceState.getParcelableArrayList("matches");
            mapHeroIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapHeroes");
            mapGameModeIDName = (HashMap<String, String>) savedInstanceState.getSerializable("mapGameModes");
            gameModeID = savedInstanceState.getString("gameModeID");
            heroID = savedInstanceState.getString("heroID");
        } else {
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
        setHasOptionsMenu(true);

        //update active drawer item
        if (getActivity() instanceof DrawerController) {
            ((DrawerController) getActivity()).setActiveDrawerItem(3);

            //make actionbar show drawer icon
            ((DrawerController) getActivity()).getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            //update the actionbar to show the up carat
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getActivity().setTitle("Statistics");

        Bundle extras = getArguments();

        if (extras != null) {
            if (extras.containsKey("hero_id")) {
                //set active hero
                heroID = extras.getString("hero_id");
                //only do this once so the screen doesn't reset back to the passed in value when rotating
                extras.remove("hero_id");
            }
        }

        updateMatches();

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
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
//                spinnerGameModes.setSelection(0, false); //itemselected won't fire on first load with this
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
                HeroSpinnerAdapter adapter = new HeroSpinnerAdapter(getActivity(), (HashMap<String, String>) mapHeroIDName.clone());
                spinnerHeroes.setAdapter(adapter);
//                spinnerHeroes.setSelection(0, false); //itemselected won't fire on first load with this
                spinnerHeroes.setOnItemSelectedListener(this);

                //if there was a savedState, set spinner selection
                if (heroSelection >= 0) {
                    spinnerHeroes.setSelection(heroSelection);
                    System.out.println("set selection herSelection: " + heroSelection);
                }
                if (Integer.parseInt(heroID) > 0) {
                    pos = adapter.getPositionForId(heroID);
                    System.out.println("hero " + heroID + " is at position " + pos);
                    spinnerHeroes.setSelection(pos, false);
                    System.out.println("set selection pos: " + pos);
                }


            }
        }

        lastUpdate = System.currentTimeMillis();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

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
                                    ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                                }
                            } else {
                                ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
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
                                    ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
                                }
                            } else {
                                ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
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
        //lock orientation during loading
        OrientationHelper.lockOrientation(getActivity());

        sml = new StatsMatchesLoader(this, getActivity());
        sml.execute(gameModeID, heroID);
    }

    private void getPlayedHeroesAndGameModes() {

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
                ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).setMatches(result, gameModeID, heroID);
                ((StatsNumbersFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(0)).updateVisuals();
            }
            if (((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(1) != null) {
                ((StatsMatchesFragment) ((ScreenSlidePagerAdapter) mPagerAdapter).getRegisteredFragment(1)).setMatches(result);
            }
        }
    }
}
