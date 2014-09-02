package be.simonraes.dotadata.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.activity.DrawerController;
import be.simonraes.dotadata.historyloading.HistoryLoader;
import be.simonraes.dotadata.playersummary.PlayerSummary;
import be.simonraes.dotadata.user.User;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.ImageLoadListener;
import be.simonraes.dotadata.util.OrientationHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 2/09/2014.
 */
public class FriendsListAdapter extends ArrayAdapter<PlayerSummary> implements HistoryLoader.ASyncResponseHistoryLoader {
    private Context context;
    private ArrayList<PlayerSummary> friends;
    private PlayerSummary clickedFriend;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private FriendsListAdapter thisAdapater = this;

    public FriendsListAdapter(Context context, ArrayList<PlayerSummary> objects) {
        super(context, R.layout.friends_list_row, objects);
        this.context = context;
        this.friends = objects;

        imageLoader = ImageLoader.getInstance();
        animateFirstListener = new ImageLoadListener();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PlayerSummary friend = friends.get(position);

        View view = convertView;
        final ViewHolder viewholder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.friends_list_row, parent, false);
            viewholder = new ViewHolder();
            viewholder.imgAvatar = (ImageView) view.findViewById(R.id.imgFriendAvatar);
            viewholder.txtName = (TextView) view.findViewById(R.id.txtFriendName);
            viewholder.btnAdd = (ImageButton) view.findViewById(R.id.btnAddFriend);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }


        viewholder.txtName.setText(friend.getPersonaname());
        imageLoader.displayImage(friend.getAvatarmedium(), viewholder.imgAvatar, options, animateFirstListener);
        viewholder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrientationHelper.lockOrientation((DrawerController)context);

                new AlertDialog.Builder(context)
                        .setTitle(friend.getPersonaname())
                        .setMessage("Download this user's match history?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Store the selected friend so the ID can be sent to the main activity
                                        // (main activity then sets that user as active)
                                        clickedFriend = friend;

                                        // Create a new user object and send it to the history loader, loader will
                                        // store the user in the database after downloading all matches.
                                        User user = new User(Conversions.steam64IdToSteam32Id(friend.getSteamid()),
                                                friend.getSteamid(), friend.getPersonaname(), friend.getAvatarmedium());

                                        // Download the user's matches
                                        HistoryLoader loader = new HistoryLoader(context, thisAdapater, user);
                                        loader.firstDownload();
                                    }
                                }
                        )
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, just dismiss
                                OrientationHelper.unlockOrientation((DrawerController)context);
                            }
                        })
                        .show();
            }

        });

        return view;
    }

    @Override
    public void processFinish(boolean foundGames) {
        OrientationHelper.unlockOrientation((DrawerController)context);
        ((DrawerController)context).newUserAddedOrSelected(Conversions.steam64IdToSteam32Id(clickedFriend.getSteamid()));
    }

    private class ViewHolder {
        public ImageView imgAvatar;
        public TextView txtName;
        public ImageButton btnAdd;
    }
}
