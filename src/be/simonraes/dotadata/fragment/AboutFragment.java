package be.simonraes.dotadata.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;

/**
 * Created by Simon on 28/02/14.
 * Fragment with info about the app.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {

    private Button btnJackson, btnHoloGraph, btnUIL;
    private View fragView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.about_layout, container, false);

        getActivity().setTitle("About");

        //update active drawer item (0 = this screen has no drawer item)
        ((DrawerController) getActivity()).setActiveDrawerItem(0);

        btnJackson = (Button) fragView.findViewById(R.id.btnAboutLibraryJackson);
        btnJackson.setOnClickListener(this);
        btnHoloGraph = (Button) fragView.findViewById(R.id.btnAboutLibraryHoloGraph);
        btnHoloGraph.setOnClickListener(this);
        btnUIL = (Button) fragView.findViewById(R.id.btnAboutLibraryUIL);
        btnUIL.setOnClickListener(this);

        setHasOptionsMenu(true);

        return fragView;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnAbout:
                //user pressed About button while already on About, SHOW OGRE MAGI!
                crossFadeToOgre();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void crossFadeToOgre() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.

        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);


        final ImageView ogre = (ImageView) fragView.findViewById(R.id.aboutOgre);

        if (ogre != null) {
            if (ogre.getVisibility() == View.GONE) {
                ogre.setAlpha(0f);
                ogre.setVisibility(View.VISIBLE);
                ogre.animate()
                        .alpha(1f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(null);
            } else {
                ogre.animate()
                        .alpha(0f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ogre.setVisibility(View.GONE);
                            }
                        });
            }
        }


    }


}