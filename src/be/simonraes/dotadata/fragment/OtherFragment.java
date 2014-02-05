package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import be.simonraes.dotadata.R;

/**
 * Created by Simon on 29/01/14.
 */
public class OtherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.otherfragment_layout, container, false);
    }
}