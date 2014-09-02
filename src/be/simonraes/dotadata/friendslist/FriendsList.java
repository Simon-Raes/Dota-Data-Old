package be.simonraes.dotadata.friendslist;

import android.os.Parcel;
import android.os.Parcelable;
import be.simonraes.dotadata.historymatch.HistoryPlayer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 2/09/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendsList implements Parcelable {

    private ArrayList<Friend> friends = new ArrayList<Friend>();

    public FriendsList() {
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }


    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(friends);
    }

    public FriendsList(Parcel pc) {
        pc.readTypedList(friends, Friend.CREATOR);

    }

    public static final Parcelable.Creator<FriendsList> CREATOR = new
            Parcelable.Creator<FriendsList>() {
                public FriendsList createFromParcel(Parcel pc) {
                    return new FriendsList(pc);
                }

                public FriendsList[] newArray(int size) {
                    return new FriendsList[size];
                }
            };
}
