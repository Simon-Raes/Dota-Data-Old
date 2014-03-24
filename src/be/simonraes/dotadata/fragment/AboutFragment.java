package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.simonraes.dotadata.R;

/**
 * Created by Simon on 28/02/14.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().getActionBar().setTitle("About");

        return inflater.inflate(R.layout.about_layout, container, false);
    }
}