package be.simonraes.dotadata.liveleaguegame;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueMatches implements Parcelable {

    @JsonProperty("games")
    public ArrayList<LiveLeagueMatch> liveLeagueMatches = new ArrayList<LiveLeagueMatch>();

    public LiveLeagueMatches() {

    }

    public ArrayList<LiveLeagueMatch> getLiveLeagueMatches() {
        return liveLeagueMatches;
    }

    public void setLiveLeagueMatches(ArrayList<LiveLeagueMatch> liveLeagueMatches) {
        this.liveLeagueMatches = liveLeagueMatches;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(liveLeagueMatches);
    }

    public LiveLeagueMatches(Parcel pc) {
        pc.readTypedList(liveLeagueMatches, LiveLeagueMatch.CREATOR);
    }

    public static final Creator<LiveLeagueMatches> CREATOR = new
            Creator<LiveLeagueMatches>() {
                public LiveLeagueMatches createFromParcel(Parcel pc) {
                    return new LiveLeagueMatches(pc);
                }

                public LiveLeagueMatches[] newArray(int size) {
                    return new LiveLeagueMatches[size];
                }
            };
}
