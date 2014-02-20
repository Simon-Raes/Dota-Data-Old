package be.simonraes.dotadata;

import android.app.Activity;
import android.os.Bundle;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.fragment.AccountIDHelpFragment;

/**
 * Created by Simon on 20/02/14.
 */
public class AccountIDHelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AccountIDHelpFragment()).commit();
    }
}
