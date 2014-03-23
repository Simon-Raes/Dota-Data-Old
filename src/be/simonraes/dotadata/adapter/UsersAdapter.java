package be.simonraes.dotadata.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.database.UsersDataSource;
import be.simonraes.dotadata.delegates.ASyncResponseHistoryLoader;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Simon on 23/02/14.
 */
public class UsersAdapter extends ArrayAdapter<User> implements ASyncResponseHistoryLoader {

    private ArrayList<User> users;
    private Context context;
    private Activity activity;

    public UsersAdapter(Context context, ArrayList<User> users, Activity activity) {
        super(context, R.layout.manage_users_row, users);

        this.context = context;
        this.users = users;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = users.get(position);
        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.manage_users_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.imgUser = (ImageView) view.findViewById(R.id.imgSelectUser);
            viewholder.txtUser = (TextView) view.findViewById(R.id.txtSelectUserName);
            viewholder.btnRemove = (ImageButton) view.findViewById(R.id.btnSwitchUserDelete);
            viewholder.btnUpdate = (ImageButton) view.findViewById(R.id.btnSwitchUserUpdate);

            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }


        //background
        if (user != null) {
            if (user.getAccount_id().equals(PreferenceManager.getDefaultSharedPreferences(context).getString("be.simonraes.dotadata.accountid", ""))) {
                view.setBackgroundColor(context.getResources().getColor(R.color.AntiqueWhite));
            }
        }


        //username
        viewholder.txtUser.setText(user.getName());

        //user avatar
        ImageLoader imageLoader = ImageLoader.getInstance();
        AnimateFirstDisplayListenerToo animateFirstListener = new AnimateFirstDisplayListenerToo();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .showImageOnLoading(R.drawable.item_lg_unknown)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader.displayImage(user.getAvatar(), viewholder.imgUser, options, animateFirstListener);

        final User finalUser = users.get(position);
        final UsersAdapter finalAdapter = this;

        //delete button
        viewholder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete " + finalUser.getName() + "?")
                        .setMessage("Really delete this user and all their matches?")
                        .setCancelable(false)
                        .setIcon(R.drawable.dotadata_sm)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                removeUserFromList(finalUser.getAccount_id());

                                MatchesDataSource mds = new MatchesDataSource(context, finalUser.getAccount_id());
                                //only delete matches, other users might have the same matches and want to keep their playersinmatches records
                                mds.deleteUserMatches();

                                UsersDataSource uds = new UsersDataSource(context);
                                uds.deleteUserByID(finalUser.getAccount_id());

                                Toast.makeText(context, "User removed.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        )
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, just dismiss
                            }
                        })
                        .show();
            }

        });

        viewholder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HistoryLoader loader = new HistoryLoader(context, finalAdapter, finalUser.getAccount_id());
                loader.updateHistory();
            }

        });


        return view;
    }

    private void removeUserFromList(String accountID) {
        ArrayList<User> tmpUsers = (ArrayList<User>) users.clone();
        for (User user : tmpUsers) {
            if (user.getAccount_id().equals(accountID)) {
                users.remove(user);
            }
        }
        if (users.size() > 0) {
            //put first user as active user
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("be.simonraes.dotadata.accountid", users.get(0).getAccount_id()).commit();
        } else {
            //no users left, put empty accountid field so user will be forced to enter new account
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("be.simonraes.dotadata.accountid", "").commit();
        }
        notifyDataSetChanged();
    }

    //finished updating user matches
    @Override
    public void processFinish(boolean foundGames) {
        OrientationHelper.unlockOrientation(activity);
    }

    private class ViewHolder {
        public ImageView imgUser;
        public TextView txtUser;
        public ImageButton btnRemove, btnUpdate;
    }
}
