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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.delegates.ASyncResponseVanity;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.parser.VanityResolverParser;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.vanity.VanityContainer;

/**
 * Created by Simon on 13/02/14.
 */
public class AccountIDHelpFragment extends Fragment implements View.OnClickListener, ASyncResponseVanity, ASyncResponseHistoryLoader {

    private EditText etxtDotabuff, etxtProfileNumber, etxtIDName;
    private Button btnHelpDotabuff, btnHelpProfileNumber, btnHelpIDName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accountid_help_layout, container, false);

        getActivity().getActionBar().setTitle("Dota 2 Account ID");
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
        switch (v.getId()) {

            case R.id.btnHelpDotabuff:
                saveDotaID(etxtDotabuff.getText().toString());

                //hide keyboard
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnHelpDotabuff.getWindowToken(), 0);

                break;

            case R.id.btnHelpProfileNumber:

                //hide keyboard
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnHelpProfileNumber.getWindowToken(), 0);
                if (!etxtProfileNumber.getText().toString().equals("") && etxtProfileNumber.getText().toString() != null) {
                    saveDotaID(Conversions.community64IDToDota64ID(etxtProfileNumber.getText().toString()));
                } else {
                    saveDotaID("0");
                }
                break;
            case R.id.btnHelpIDName:

                //hide keyboard
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(btnHelpIDName.getWindowToken(), 0);

                if (!etxtIDName.getText().toString().equals("") && etxtIDName.getText().toString() != null) {
                    VanityResolverParser parser = new VanityResolverParser(this);
                    parser.execute(etxtIDName.getText().toString());
                } else {
                    saveDotaID("0");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void processFinish(VanityContainer result) {
        //got player long ID
        saveDotaID(Conversions.community64IDToDota64ID(result.getResponse().getSteamid()));
    }

    private void saveDotaID(String accountID) {

        if (!accountID.equals("0")) {

            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("be.simonraes.dotadata.accountid", accountID).commit();

            HistoryLoader loader = new HistoryLoader(getActivity(), this);
            loader.updateHistory();

            new AlertDialog.Builder(getActivity())
                    .setTitle("Success!")
                    .setMessage("Your Dota 2 account ID has been saved and your match history is now downloading in the background. You can use other apps while you wait. Use the notification to keep track of progress.")
                    .setCancelable(false)
                    .setIcon(R.drawable.dotadata_sm)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing, just dismiss
                        }
                    }
                    ).show();


        } else

        {
            Toast.makeText(getActivity(), "Could not find a Dota 2 account ID for that user. Please try a different username or number.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void processFinish() {

    }
}
