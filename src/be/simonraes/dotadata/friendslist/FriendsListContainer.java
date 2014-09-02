package be.simonraes.dotadata.friendslist;

import android.os.Parcel;
import android.os.Parcelable;
import be.simonraes.dotadata.historymatch.HistoryMatches;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon Raes on 2/09/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FriendsListContainer implements Parcelable{
    private FriendsList friendslist;

    public FriendsListContainer() {
    }

    public FriendsList getFriendslist() {
        return friendslist;
    }

    public void setFriendslist(FriendsList friendslist) {
        this.friendslist = friendslist;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(friendslist, 0);
    }

    public FriendsListContainer(Parcel pc) {
        friendslist = pc.readParcelable(FriendsList.class.getClassLoader());
    }

    public static final Parcelable.Creator<FriendsListContainer> CREATOR = new
            Parcelable.Creator<FriendsListContainer>() {
                public FriendsListContainer createFromParcel(Parcel pc) {
                    return new FriendsListContainer(pc);
                }

                public FriendsListContainer[] newArray(int size) {
                    return new FriendsListContainer[size];
                }
            };
}
