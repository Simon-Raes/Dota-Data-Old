package be.simonraes.dotadata.liveleaguegame;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeaguePlayer implements Parcelable {

    public String account_id;
    public String name;
    public String hero_id;
    public String team; //radiant (0), dire (1), spectator (2)? or caster (4)?

    public LiveLeaguePlayer() {

    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHero_id() {
        return hero_id;
    }

    public void setHero_id(String hero_id) {
        this.hero_id = hero_id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account_id);
        dest.writeString(name);
        dest.writeString(hero_id);
        dest.writeString(team);

    }

    public LiveLeaguePlayer(Parcel pc) {
        account_id = pc.readString();
        name = pc.readString();
        hero_id = pc.readString();
        team = pc.readString();

    }

    public static final Creator<LiveLeaguePlayer> CREATOR = new
            Creator<LiveLeaguePlayer>() {
                public LiveLeaguePlayer createFromParcel(Parcel pc) {
                    return new LiveLeaguePlayer(pc);
                }

                public LiveLeaguePlayer[] newArray(int size) {
                    return new LiveLeaguePlayer[size];
                }
            };
}
