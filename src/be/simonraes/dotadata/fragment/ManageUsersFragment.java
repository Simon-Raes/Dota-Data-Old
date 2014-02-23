package be.simonraes.dotadata.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.adapter.UsersAdapter;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.user.User;

import java.util.ArrayList;

/**
 * Created by Simon on 23/02/14.
 */
public class ManageUsersFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<User> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_user_layout, null);
        ListView lvUsers = (ListView) view.findViewById(R.id.lvSelectUser);

        getActivity().getActionBar().setTitle("Manage users");

        Button btnNewUser = (Button) view.findViewById(R.id.btnSelectUserNew);
        btnNewUser.setOnClickListener(this);

        UsersDataSource uds = new UsersDataSource(getActivity());
        users = uds.getAllUsers();
        System.out.println("got " + users.size() + "users");
        lvUsers.setAdapter(new UsersAdapter(getActivity(), users));
        lvUsers.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //set app-wide account ID to this user
        if (!users.get(position).getAccount_id().equals(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("be.simonraes.dotadata.accountid", ""))) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("be.simonraes.dotadata.accountid", users.get(position).getAccount_id()).commit();
            Toast.makeText(getActivity(), "Switched user.", Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment()).addToBackStack(null).commit();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectUserNew:

                getFragmentManager().beginTransaction().replace(R.id.content_frame, new AccountIDHelpFragment()).addToBackStack(null).commit();

                break;
            default:
                break;
        }
    }
}
