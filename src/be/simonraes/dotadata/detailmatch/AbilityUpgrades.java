package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbilityUpgrades implements Parcelable {

    private String key;
    private String match_id; //part of db key
    private String player_slot;   //part of db key
    private String ability;
    private String time;
    private String level; //part of db key

    public AbilityUpgrades(){

    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getPlayer_slot() {
        return player_slot;
    }

    public void setPlayer_slot(String player_slot) {
        this.player_slot = player_slot;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(match_id);
        dest.writeString(player_slot);
        dest.writeString(ability);
        dest.writeString(time);
        dest.writeString(level);
    }

    public AbilityUpgrades(Parcel pc) {
        match_id = pc.readString();
        player_slot = pc.readString();
        ability = pc.readString();
        time = pc.readString();
        level = pc.readString();
    }

    public static final Parcelable.Creator<AbilityUpgrades> CREATOR = new
            Parcelable.Creator<AbilityUpgrades>() {
                public AbilityUpgrades createFromParcel(Parcel pc) {
                    return new AbilityUpgrades(pc);
                }

                public AbilityUpgrades[] newArray(int size) {
                    return new AbilityUpgrades[size];
                }
            };

}
