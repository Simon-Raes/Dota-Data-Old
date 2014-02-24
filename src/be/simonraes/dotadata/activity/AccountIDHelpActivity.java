//package be.simonraes.dotadata.activity;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import be.simonraes.dotadata.R;
//import be.simonraes.dotadata.fragment.AddUserFragment;
//
///**
// * Created by Simon on 20/02/14.
// */
//public class AccountIDHelpActivity extends Activity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new AddUserFragment()).commit();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("be.simonraes.dotadata.downloadinprogress", false).commit();
//
//    }
//}
