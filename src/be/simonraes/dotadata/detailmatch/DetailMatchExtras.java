package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Extra info added by the user (note, favourite,...)
 * Created by Simon Raes on 28/03/2014.
 */
public class DetailMatchExtras implements Parcelable {

    private String match_id;
    private String account_id;
    private boolean user_win;
    private String note;
    private boolean favourite;

    public DetailMatchExtras() {
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public boolean isUser_win() {
        return user_win;
    }

    public void setUser_win(boolean user_win) {
        this.user_win = user_win;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(match_id);
        dest.writeString(account_id);
        dest.writeInt(user_win ? 1 : 0);
        dest.writeString(note);
        dest.writeInt(favourite ? 1 : 0);

    }

    public DetailMatchExtras(Parcel pc) {

        match_id = pc.readString();
        account_id = pc.readString();
        user_win = (pc.readInt() == 1);
        note = pc.readString();
        favourite = (pc.readInt() == 1);
    }

    public static final Parcelable.Creator<DetailMatchExtras> CREATOR = new
            Parcelable.Creator<DetailMatchExtras>() {
                public DetailMatchExtras createFromParcel(Parcel pc) {
                    return new DetailMatchExtras(pc);
                }

                public DetailMatchExtras[] newArray(int size) {
                    return new DetailMatchExtras[size];
                }
            };
}
