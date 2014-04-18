package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon Raes on 18/04/2014.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalUnits implements Parcelable {
    private String unitname;
    private String item_0;
    private String item_1;
    private String item_2;
    private String item_3;
    private String item_4;
    private String item_5;

    private String match_id; //part of db key
    private String player_slot;   //part of db key

    public AdditionalUnits() {

    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getItem_0() {
        return item_0;
    }

    public void setItem_0(String item_0) {
        this.item_0 = item_0;
    }

    public String getItem_1() {
        return item_1;
    }

    public void setItem_1(String item_1) {
        this.item_1 = item_1;
    }

    public String getItem_2() {
        return item_2;
    }

    public void setItem_2(String item_2) {
        this.item_2 = item_2;
    }

    public String getItem_3() {
        return item_3;
    }

    public void setItem_3(String item_3) {
        this.item_3 = item_3;
    }

    public String getItem_4() {
        return item_4;
    }

    public void setItem_4(String item_4) {
        this.item_4 = item_4;
    }

    public String getItem_5() {
        return item_5;
    }

    public void setItem_5(String item_5) {
        this.item_5 = item_5;
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

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unitname);
        dest.writeString(item_0);
        dest.writeString(item_1);
        dest.writeString(item_2);
        dest.writeString(item_3);
        dest.writeString(item_4);
        dest.writeString(item_5);

        dest.writeString(match_id);
        dest.writeString(player_slot);
    }

    public AdditionalUnits(Parcel pc) {
        unitname = pc.readString();
        item_0 = pc.readString();
        item_1 = pc.readString();
        item_2 = pc.readString();
        item_3 = pc.readString();
        item_4 = pc.readString();
        item_5 = pc.readString();

        match_id = pc.readString();
        player_slot = pc.readString();
    }

    public static final Parcelable.Creator<AdditionalUnits> CREATOR = new
            Parcelable.Creator<AdditionalUnits>() {
                public AdditionalUnits createFromParcel(Parcel pc) {
                    return new AdditionalUnits(pc);
                }

                public AdditionalUnits[] newArray(int size) {
                    return new AdditionalUnits[size];
                }
            };
}
