package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.parser.VanityResolverParser;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.*;
import be.simonraes.dotadata.vanity.VanityContainer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Simon on 13/02/14.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener, VanityResolverParser.ASyncResponseVanity, HistoryLoader.ASyncResponseHistoryLoader, PlayerSummaryParser.ASyncResponsePlayerSummary {

    private EditText etxtDotabuff, etxtProfileNumber, etxtIDName;
    private String userAccountID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_layout, container, false);

        if (getActivity() != null) {
            if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle("Add new user");
                getActivity().getActionBar().setSubtitle(null);
            }
            if (getActivity().getWindow() != null) {
                // Make sure keyboard doesn't automatically open on page load
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        }

        Button btnHelpDotabuff = (Button) view.findViewById(R.id.btnHelpDotabuff);
        btnHelpDotabuff.setOnClickListener(this);

        Button btnHelpProfileNumber = (Button) view.findViewById(R.id.btnHelpProfileNumber);
        btnHelpProfileNumber.setOnClickListener(this);

        Button btnHelpIDName = (Button) view.findViewById(R.id.btnHelpIDName);
        btnHelpIDName.setOnClickListener(this);

        etxtDotabuff = (EditText) view.findViewById(R.id.txtHelpDotabuff);
        // Lock orientation while keyboard is visible (crashes when going from portrait to landscape, but not the other way around (?))
        etxtDotabuff.setOnFocusChangeListener(new onFocusListener());
        etxtDotabuff.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etxtDotabuff.getWindowToken(), 0);

                    dotaBuffEntered();
                    return true;
                }
                return false;
            }
        });

        etxtProfileNumber = (EditText) view.findViewById(R.id.txtHelpProfileNumber);
        // Lock orientation while keyboard is visible (crashes when going from portrait to landscape, but not the other way around (?!))
        etxtProfileNumber.setOnFocusChangeListener(new onFocusListener());
        etxtProfileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etxtProfileNumber.getWindowToken(), 0);

                    profileNumberEntered();
                    return true;
                }
                return false;
            }
        });

        etxtIDName = (EditText) view.findViewById(R.id.txtHelpIDName);
        // Lock orientation while keyboard is visible (crashes when going from portrait to landscape, but not the other way around (?!))
        etxtIDName.setOnFocusChangeListener(new onFocusListener());
        etxtIDName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etxtIDName.getWindowToken(), 0);

                    profileVanityEntered();
                    return true;
                }
                return false;
            }
        });


        TextView txtDotabuffLink = (TextView) view.findViewById(R.id.txtAccountIDDBExample);
        txtDotabuffLink.setText(Html.fromHtml("Example: http://dotabuff.com/players/<b>6133547</b>"));

        TextView txtProfileNumber = (TextView) view.findViewById(R.id.txtHelpProfileExample);
        txtProfileNumber.setText(Html.fromHtml("Example: http://steamcommunity.com/profiles/<b>76561197966399275</b>"));

        TextView txtIDName = (TextView) view.findViewById(R.id.txtHelpIDExample);
        txtIDName.setText(Html.fromHtml("Example: http://steamcommunity.com/id/<b>Voshond</b>/"));

        return view;
    }

    @Override
    public void onClick(View v) {
        // Hide keyboard
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etxtDotabuff.getWindowToken(), 0);
        // Lock orientation during loading/parsing
        OrientationHelper.lockOrientation(getActivity());
        switch (v.getId()) {

            case R.id.btnHelpDotabuff:
                dotaBuffEntered();
                break;

            case R.id.btnHelpIDName:
                profileVanityEntered();
                break;

            case R.id.btnHelpProfileNumber:
                profileNumberEntered();
                break;

            default:
                break;
        }
    }

    private void dotaBuffEntered() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        OrientationHelper.lockOrientation(getActivity());

        if (InternetCheck.isOnline(getActivity())) {
            if (!etxtDotabuff.getText().equals("")) {
                saveDotaID(etxtDotabuff.getText().toString());
            } else {
                saveDotaID("0");
            }
        } else {
            showNoInternetToast();
        }
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etxtDotabuff.getWindowToken(), 0);
    }

    private void profileVanityEntered() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        OrientationHelper.lockOrientation(getActivity());

        if (InternetCheck.isOnline(getActivity())) {
            if (!etxtIDName.getText().toString().equals("") && etxtIDName.getText().toString() != null) {
                VanityResolverParser parser = new VanityResolverParser(this);
                parser.execute(etxtIDName.getText().toString());
            } else {
                saveDotaID("0");
            }
        } else {
            showNoInternetToast();
        }
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etxtIDName.getWindowToken(), 0);
    }

    private void profileNumberEntered() {
        getActivity().setProgressBarIndeterminateVisibility(true);
        OrientationHelper.lockOrientation(getActivity());

        if (InternetCheck.isOnline(getActivity())) {
            if (!etxtProfileNumber.getText().toString().equals("") && etxtProfileNumber.getText().toString() != null) {
                saveDotaID(Conversions.steam64IdToSteam32Id(etxtProfileNumber.getText().toString()));
            } else {
                saveDotaID("0");
            }
        } else {
            showNoInternetToast();
        }
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(etxtProfileNumber.getWindowToken(), 0);
    }

    private void showNoInternetToast() {
        Toast.makeText(getActivity(), "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Finished converting Vanity URL to Steam64 Id details.
     */
    @Override
    public void processFinish(VanityContainer result) {
        saveDotaID(Conversions.steam64IdToSteam32Id(result.getResponse().getSteamid()));
    }

    private void saveDotaID(String accountID) {

        if (!accountID.equals("0")) {
            userAccountID = accountID;

            // Get player information
            PlayerSummaryParser parser = new PlayerSummaryParser(this);
            parser.execute(accountID);

        } else {
            Toast.makeText(getActivity(), "Could not find Dota 2 matches for that user. Please try a different username or number.", Toast.LENGTH_LONG).show();
            OrientationHelper.unlockOrientation(getActivity());
        }
    }

    // Got player summary based on accountID
    @Override
    public void processFinish(PlayerSummaryContainer result) {
        getActivity().setProgressBarIndeterminateVisibility(false);

        if (result.getPlayers() != null) {

            if (result.getPlayers().getPlayers().size() > 0) {

                final PlayerSummaryContainer finalResult = result;

                // Check if user is already in the database
                UsersDataSource uds = new UsersDataSource(getActivity());
                User testUser = uds.getUserByID(userAccountID);
                if (testUser.getAccount_id() != null && !testUser.getAccount_id().equals("")) {

                    // User already saved, just switch user instead of downloading
                    AppPreferences.setActiveAccountId(getActivity(), testUser.getAccount_id());

                    getFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment()).addToBackStack(null).commit();

                } else {

                    View layDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_view_found_user, null);
                    ImageView imgDialog = (ImageView) layDialog.findViewById(R.id.imgDialogFoundUser);
                    TextView txtDialog = (TextView) layDialog.findViewById(R.id.txtDialogFoundUser);
                    txtDialog.setText("Found user " + result.getPlayers().getPlayers().get(0).getPersonaname() + ".\nStart download for this account?");

                    new AlertDialog.Builder(getActivity())
                            .setTitle(result.getPlayers().getPlayers().get(0).getPersonaname())
                            .setView(layDialog)
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            //everything is good, save user account id and user
                                            User user = new User(userAccountID, finalResult.getPlayers().getPlayers().get(0).getSteamid(), finalResult.getPlayers().getPlayers().get(0).getPersonaname(), finalResult.getPlayers().getPlayers().get(0).getAvatarmedium());

                                            //start download
                                            startDownload(user);
                                        }
                                    }
                            )
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Not adding this user, dismiss dialog
                                    OrientationHelper.unlockOrientation(getActivity());
                                }
                            })
                            .show();

                    // Set found player avatar in the dialog.
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .resetViewBeforeLoading(true)
                            .cacheInMemory(true)
                            .showImageOnLoading(R.drawable.item_lg_unknown)
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .build();
                    ImageLoadListener animateFirstListener = new ImageLoadListener();

                    imageLoader.displayImage(result.getPlayers().getPlayers().get(0).getAvatarmedium(), imgDialog, options, animateFirstListener);
                }
            } else {
                Toast.makeText(getActivity(), "Could not find a Dota 2 account ID for that user. Please try a different username or number.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "The Dota 2 API is currently unavailable. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    /** Starts historyloader*/
    private void startDownload(User user) {
        this.userAccountID = user.getAccount_id();

        HistoryLoader loader = new HistoryLoader(getActivity(), this, user);
        loader.firstDownload();
    }

    // Result received from historyloader
    @Override
    public void processFinish(boolean foundGames) {
        // Alert the main activity that a new user has been added and set as active
        ((DrawerController) getActivity()).newUserAddedOrSelected(userAccountID);
    }

    private class onFocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                OrientationHelper.lockOrientation(getActivity());
            } else {
                OrientationHelper.unlockOrientation(getActivity());
            }
        }
    }
}
