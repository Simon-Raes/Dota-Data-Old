package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import be.simonraes.dotadata.R;

/**
 * Created by Simon on 13/02/14.
 */
public class AccountIDHelpFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accountid_help_layout, container, false);

        getActivity().getActionBar().setTitle("Dota 2 Account ID");

        Button btnHelpProfileNumber = (Button) view.findViewById(R.id.btnHelpProfileNumber);
        btnHelpProfileNumber.setOnClickListener(this);

        Button btnHelpIDName = (Button) view.findViewById(R.id.btnHelpIDName);
        btnHelpIDName.setOnClickListener(this);

        TextView txtDotabuffLink = (TextView) view.findViewById(R.id.txtAccountIDDBURL);
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
            case R.id.btnHelpProfileNumber:
                Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnHelpIDName:
                Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
