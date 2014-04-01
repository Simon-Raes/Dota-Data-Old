package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Extra info added by the user (note, favourite,...)
 * Created by Simon Raes on 28/03/2014.
 */
public class DetailMatchExtras implements Parcelable {

    private String match_id;
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
        dest.writeString(note);
        dest.writeInt(favourite ? 1 : 0);

    }

    public DetailMatchExtras(Parcel pc) {

        match_id = pc.readString();
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
