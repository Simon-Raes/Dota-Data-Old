package be.simonraes.dotadata.historymatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryContainer implements Parcelable {

    @JsonProperty("result")
    public HistoryMatches recentgames;

    public HistoryContainer(){

    }

    public HistoryMatches getRecentGames() {
        return recentgames;
    }

    public void setRecentGames(HistoryMatches recentgames) {
        this.recentgames = recentgames;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(recentgames, 0);
    }

    public HistoryContainer(Parcel pc) {
        recentgames = pc.readParcelable(HistoryMatches.class.getClassLoader());
    }

    public static final Parcelable.Creator<HistoryContainer> CREATOR = new
            Parcelable.Creator<HistoryContainer>() {
                public HistoryContainer createFromParcel(Parcel pc) {
                    return new HistoryContainer(pc);
                }

                public HistoryContainer[] newArray(int size) {
                    return new HistoryContainer[size];
                }
            };
}
