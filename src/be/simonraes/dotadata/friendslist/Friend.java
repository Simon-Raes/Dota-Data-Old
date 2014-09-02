package be.simonraes.dotadata.friendslist;

import android.os.Parcel;
import android.os.Parcelable;
import be.simonraes.dotadata.historymatch.HistoryPlayer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon Raes on 2/09/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Friend implements Parcelable {
    public String steamid;
    public String relationship;
    public String friend_since;

    public Friend() {
    }

    public String getSteamid() {
        return steamid;
    }

    public void setSteamid(String steamid) {
        this.steamid = steamid;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getFriend_since() {
        return friend_since;
    }

    public void setFriend_since(String friend_since) {
        this.friend_since = friend_since;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(steamid);
        dest.writeString(relationship);
        dest.writeString(friend_since);
    }

    public Friend(Parcel pc) {
        steamid = pc.readString();
        relationship = pc.readString();
        friend_since = pc.readString();
    }

    public static final Parcelable.Creator<Friend> CREATOR = new
            Parcelable.Creator<Friend>() {
                public Friend createFromParcel(Parcel pc) {
                    return new Friend(pc);
                }

                public Friend[] newArray(int size) {
                    return new Friend[size];
                }
            };
}
