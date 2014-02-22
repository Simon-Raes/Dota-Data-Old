package be.simonraes.dotadata.liveleaguegame;

import android.os.Parcel;
import android.os.Parcelable;
import be.simonraes.dotadata.historymatch.HistoryMatches;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueContainer implements Parcelable {

    @JsonProperty("result")
    public LiveLeagueMatches liveLeagueMatches;

    public LiveLeagueContainer() {

    }

    public LiveLeagueMatches getLiveLeagueMatches() {
        return liveLeagueMatches;
    }

    public void setLiveLeagueMatches(LiveLeagueMatches liveLeagueMatches) {
        this.liveLeagueMatches = liveLeagueMatches;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(liveLeagueMatches, 0);
    }

    public LiveLeagueContainer(Parcel pc) {
        liveLeagueMatches = pc.readParcelable(HistoryMatches.class.getClassLoader());
    }

    public static final Parcelable.Creator<LiveLeagueContainer> CREATOR = new
            Parcelable.Creator<LiveLeagueContainer>() {
                public LiveLeagueContainer createFromParcel(Parcel pc) {
                    return new LiveLeagueContainer(pc);
                }

                public LiveLeagueContainer[] newArray(int size) {
                    return new LiveLeagueContainer[size];
                }
            };
}
