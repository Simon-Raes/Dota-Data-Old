package be.simonraes.dotadata.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.delegates.ASyncResponsePlayerSummary;
import be.simonraes.dotadata.delegates.ASyncResponseVanity;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.parser.PlayerSummaryParser;
import be.simonraes.dotadata.parser.VanityResolverParser;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.InternetCheck;
import be.simonraes.dotadata.util.OrientationHelper;
import be.simonraes.dotadata.vanity.VanityContainer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Simon on 13/02/14.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener, ASyncResponseVanity, ASyncResponseHistoryLoader, ASyncResponsePlayerSummary {

    private EditText etxtDotabuff, etxtProfileNumber, etxtIDName;
    private Button btnHelpDotabuff, btnHelpProfileNumber, btnHelpIDName;

    private String userAccountID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accountid_help_layout, container, false);

        getActivity().getActionBar().setTitle("Add new user");

        //make sure keyboard doesn't automatically open on page load
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnHelpDotabuff = (Button) view.findViewById(R.id.btnHelpDotabuff);
        btnHelpDotabuff.setOnClickListener(this);

        btnHelpProfileNumber = (Button) view.findViewById(R.id.btnHelpProfileNumber);
        btnHelpProfileNumber.setOnClickListener(this);

        btnHelpIDName = (Button) view.findViewById(R.id.btnHelpIDName);
        btnHelpIDName.setOnClickListener(this);

        etxtProfileNumber = (EditText) view.findViewById(R.id.txtHelpProfileNumber);
        etxtIDName = (EditText) view.findViewById(R.id.txtHelpIDName);
        etxtDotabuff = (EditText) view.findViewById(R.id.txtHelpDotabuff);

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
        //hide keyboard
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnHelpDotabuff.getWindowToken(), 0);
        //lock orientation during loading/parsing
        OrientationHelper.lockOrientation(getActivity());
        switch (v.getId()) {

            case R.id.btnHelpDotabuff:
                if (InternetCheck.isOnline(getActivity())) {
                    if (!etxtDotabuff.getText().equals("")) {
                        saveDotaID(etxtDotabuff.getText().toString());
                    } else {
                        saveDotaID("0");
                    }
                } else {
                    Toast.makeText(getActivity(), "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnHelpProfileNumber:

                if (InternetCheck.isOnline(getActivity())) {
                    if (!etxtProfileNumber.getText().toString().equals("") && etxtProfileNumber.getText().toString() != null) {
                        saveDotaID(Conversions.community64IDToDota64ID(etxtProfileNumber.getText().toString()));
                    } else {
                        saveDotaID("0");
                    }
                } else {
                    Toast.makeText(getActivity(), "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnHelpIDName:

                if (InternetCheck.isOnline(getActivity())) {
                    if (!etxtIDName.getText().toString().equals("") && etxtIDName.getText().toString() != null) {
                        VanityResolverParser parser = new VanityResolverParser(this);
                        parser.execute(etxtIDName.getText().toString());
                    } else {
                        saveDotaID("0");
                    }
                } else {
                    Toast.makeText(getActivity(), "You are not connected to the internet.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void processFinish(VanityContainer result) {
        //got player steamID ID
        saveDotaID(Conversions.community64IDToDota64ID(result.getResponse().getSteamid()));
    }

    private void saveDotaID(String accountID) {

        if (!accountID.equals("0")) {
            userAccountID = accountID;

            //get player information
            PlayerSummaryParser parser = new PlayerSummaryParser(this);
            parser.execute(accountID);


        } else {
            Toast.makeText(getActivity(), "Could not find a Dota 2 account ID for that user. Please try a different username or number.", Toast.LENGTH_LONG).show();
            OrientationHelper.unlockOrientation(getActivity());
        }
    }


    //got player summary based on accountID
    @Override
    public void processFinish(PlayerSummaryContainer result) {
        if (result.getPlayers().getPlayers().size() > 0) {

            final PlayerSummaryContainer finalResult = result;


            //check if user is already in the database
            UsersDataSource uds = new UsersDataSource(getActivity());
            User testUser = uds.getUserByID(userAccountID);
            if (testUser.getAccount_id() != null && !testUser.getAccount_id().equals("")) {
                System.out.println("user is not null (is in db), switching");
                //just switch user instead of downloading
                Toast.makeText(getActivity(), "Switched user.", Toast.LENGTH_LONG).show();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment()).addToBackStack(null).commit();

            } else {
//                View layDialog = getActivity().getLayoutInflater().inflate(R.layout.dialog_view_found_user, null);
//                ImageView imgDialog = (ImageView) layDialog.findViewById(R.id.imgDialogFoundUser);
//
//                ImageLoader imageLoader = ImageLoader.getInstance();
//                DisplayImageOptions options = new DisplayImageOptions.Builder()
//                        .resetViewBeforeLoading(true)
//                        .cacheInMemory(true)
//                        .showImageOnLoading(R.drawable.item_lg_unknown)
//                        .imageScaleType(ImageScaleType.EXACTLY)
//                        .build();
//                AnimateFirstDisplayListenerToo animateFirstListener = new AnimateFirstDisplayListenerToo();
//
//                imageLoader.displayImage(testUser.getAvatar(), imgDialog, options, animateFirstListener);

//                TextView txtDialog = (TextView) layDialog.findViewById(R.id.txtDialogFoundUser);
//                txtDialog.setText("Start download for this account?");
                new AlertDialog.Builder(getActivity())
                        .setTitle(result.getPlayers().getPlayers().get(0).getPersonaname())
                                //todo: add user image (url is already in result object)
                        .setMessage("Found user " + result.getPlayers().getPlayers().get(0).getPersonaname() + ".\nStart download for this account?")
//                        .setView(layDialog)
                        .setCancelable(false)
                                //.setIcon(R.drawable.dotadata_sm)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                //everything is good, save user account id and user
                                UsersDataSource uds = new UsersDataSource(getActivity());
                                User user = new User(userAccountID, finalResult.getPlayers().getPlayers().get(0).getSteamid(), finalResult.getPlayers().getPlayers().get(0).getPersonaname(), finalResult.getPlayers().getPlayers().get(0).getAvatarmedium());
                                uds.saveUser(user);
                                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("be.simonraes.dotadata.accountid", userAccountID).commit();

                                //start download
                                startDownload();
                            }
                        }

                        )
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, just dismiss
                                OrientationHelper.unlockOrientation(getActivity());
                            }
                        })
                        .show();

            }
        } else {
            Toast.makeText(getActivity(), "Could not find a Dota 2 account ID for that user. Please try a different username or number.", Toast.LENGTH_LONG).show();
        }
    }

    //start historyloader
    private void startDownload() {
        HistoryLoader loader = new HistoryLoader(getActivity(), this);
        loader.firstDownload();
    }

    //result from historyloader
    @Override
    public void processFinish(boolean foundGames) {
        OrientationHelper.unlockOrientation(getActivity());
    }
}
