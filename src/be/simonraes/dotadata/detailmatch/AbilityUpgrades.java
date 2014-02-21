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

    private String ability;
    private String time;
    private String level;

    public AbilityUpgrades(){

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
        dest.writeString(ability);
        dest.writeString(time);
        dest.writeString(level);
    }

    public AbilityUpgrades(Parcel pc) {
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
