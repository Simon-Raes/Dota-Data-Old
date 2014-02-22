package be.simonraes.dotadata.liveleaguegame;

import android.os.Parcel;
import android.os.Parcelable;
import be.simonraes.dotadata.historymatch.HistoryMatches;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueTeam implements Parcelable {

    public String team_name;
    public String team_id;
    public String team_logo;
    public String complete;

    public LiveLeagueTeam() {

    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTeam_logo() {
        return team_logo;
    }

    public void setTeam_logo(String team_logo) {
        this.team_logo = team_logo;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(team_name);
        dest.writeString(team_id);
        dest.writeString(team_logo);
        dest.writeString(complete);

    }

    public LiveLeagueTeam(Parcel pc) {
        team_name = pc.readString();
        team_id = pc.readString();
        team_logo = pc.readString();
        complete = pc.readString();

    }

    public static final Creator<LiveLeagueTeam> CREATOR = new
            Creator<LiveLeagueTeam>() {
                public LiveLeagueTeam createFromParcel(Parcel pc) {
                    return new LiveLeagueTeam(pc);
                }

                public LiveLeagueTeam[] newArray(int size) {
                    return new LiveLeagueTeam[size];
                }
            };
}
