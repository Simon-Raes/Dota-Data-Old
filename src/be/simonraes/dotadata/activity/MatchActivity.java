package be.simonraes.dotadata.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.fragment.MatchDetailFragment;

/**
 * Created by Simon Raes on 16/04/2014.
 * Contains the matchDetails fragment.
 */
public class MatchActivity extends DrawerController {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View contentView = inflater.inflate(R.layout.drawer_layout, null, false);

        drawerLayout.addView(contentView, 0);

        Intent intent = getIntent();
        DetailMatch match = intent.getParcelableExtra("match");

        Fragment fragment = new MatchDetailFragment();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);

        //send object to fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("be.simonraes.dotadata.detailmatch", match);
        fragment.setArguments(bundle);

        if (savedInstanceState == null || savedInstanceState.getBoolean("pageLaunch", true)) {
            transaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pageLaunch", false);
    }

    /**disable activity switch animation*/
    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            // There are more fragments in the backstack, go back one fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // User pressed back in the last fragment in the stack, finish this activity
            finish();
        }
    }
}
