package be.simonraes.dotadata.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import be.simonraes.dotadata.fragment.StatsMatchesFragment;
import be.simonraes.dotadata.fragment.StatsNumbersFragment;

/**
 * Created by Simon Raes on 19/04/2014.
 */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    private static final int NUM_PAGES = 2;


    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StatsNumbersFragment();
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
