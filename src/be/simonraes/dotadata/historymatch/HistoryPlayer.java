package be.simonraes.dotadata.historymatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryPlayer implements Parcelable {

    private String account_id;
    private String player_slot;
    private String hero_id;

    public HistoryPlayer() {

    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getPlayer_slot() {
        return player_slot;
    }

    public void setPlayer_slot(String player_slot) {
        this.player_slot = player_slot;
    }

    public String getHero_id() {
        return hero_id;
    }

    public void setHero_id(String hero_id) {
        this.hero_id = hero_id;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(account_id);
        dest.writeString(player_slot);
        dest.writeString(hero_id);
    }

    public HistoryPlayer(Parcel pc) {
        account_id = pc.readString();
        player_slot = pc.readString();
        hero_id = pc.readString();
    }

    public static final Creator<HistoryPlayer> CREATOR = new
            Creator<HistoryPlayer>() {
                public HistoryPlayer createFromParcel(Parcel pc) {
                    return new HistoryPlayer(pc);
                }

                public HistoryPlayer[] newArray(int size) {
                    return new HistoryPlayer[size];
                }
            };
}
