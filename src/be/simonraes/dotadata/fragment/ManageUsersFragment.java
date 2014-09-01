package be.simonraes.dotadata.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.adapter.UsersAdapter;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.statistics.PlayedHeroesMapper;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.AppPreferences;

import java.util.ArrayList;

/**
 * List with all users that can be used to switch to a different active user. Has button to add new user.
 * Created by Simon on 23/02/14.
 */
public class ManageUsersFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ArrayList<User> users;
    private ListView lvUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_users_layout, null);
        lvUsers = (ListView) view.findViewById(R.id.lvSelectUser);

        getActivity().setTitle("Manage users");
        setHasOptionsMenu(true);

        // Update active drawer item
        ((DrawerController) getActivity()).setActiveDrawerItem(0);

        Button btnNewUser = (Button) view.findViewById(R.id.btnSelectUserNew);
        btnNewUser.setOnClickListener(this);

        UsersDataSource uds = new UsersDataSource(getActivity());
        users = uds.getAllUsers();
        lvUsers.setAdapter(new UsersAdapter(getActivity(), users, getActivity()));
        lvUsers.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Set app-wide account ID to this user
        AppPreferences.putAccountID(getActivity(), users.get(position).getAccount_id());

        //todo: this shouldn't be here
        // Uupdate the playedheroes/gamemodes maps for this user
        PlayedHeroesMapper.clearInstance();
        PlayedHeroesMapper phm = PlayedHeroesMapper.getInstance(getActivity());
        if (PlayedHeroesMapper.getMaps().getPlayedHeroes().size() < 1) {
            phm.execute();
        }

        lvUsers.setAdapter(new UsersAdapter(getActivity(), users, getActivity()));
        //update active drawer item
        ((DrawerController) getActivity()).setActiveDrawerItem(1);
        //update active user in drawer slider
        ((DrawerController) getActivity()).setActiveUser(users.get(position).getAccount_id());

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new RecentGamesFragment()).addToBackStack(null).commit();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectUserNew:
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddUserFragment()).addToBackStack(null).commit();
                break;
            default:
                break;
        }
    }
}
