package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;

/**
 * Created by Simon on 28/02/14.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private Button btnJackson, btnHoloGraph, btnUIL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_layout, container, false);

        getActivity().setTitle("About");
        //update active drawer item
        ((DrawerController) getActivity()).setActiveDrawerItem(0);

        btnJackson = (Button) view.findViewById(R.id.btnAboutLibraryJackson);
        btnJackson.setOnClickListener(this);
        btnHoloGraph = (Button) view.findViewById(R.id.btnAboutLibraryHoloGraph);
        btnHoloGraph.setOnClickListener(this);
        btnUIL = (Button) view.findViewById(R.id.btnAboutLibraryUIL);
        btnUIL.setOnClickListener(this);

        setHasOptionsMenu(true);

        return view;
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
        MenuItem spinHeroes = menu.findItem(R.id.spinHeroes);
        if (spinHeroes != null) {
            spinHeroes.setVisible(false);
        }
        MenuItem spinGameModes = menu.findItem(R.id.spinGameModes);
        if (spinGameModes != null) {
            spinGameModes.setVisible(false);
        }
    }

    @Override
    public void onClick(View view) {
        Intent browserIntent;
        switch (view.getId()) {
            case R.id.btnAboutLibraryJackson:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/FasterXML/jackson"));
                startActivity(browserIntent);
                break;
            case R.id.btnAboutLibraryHoloGraph:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bitbucket.org/danielnadeau/holographlibrary/wiki/Home"));
                startActivity(browserIntent);
                break;
            case R.id.btnAboutLibraryUIL:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nostra13/Android-Universal-Image-Loader"));
                startActivity(browserIntent);
                break;
            default:
                break;
        }
    }
}