package be.simonraes.dotadata.historymatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMatch implements Parcelable {

    private String match_id;
    private String match_seq_num;
    private String start_time;
    private String lobby_type;

    @JsonProperty("players")
    private List<HistoryPlayer> players = new ArrayList<HistoryPlayer>();

    public HistoryMatch(){

    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getMatch_seq_num() {
        return match_seq_num;
    }

    public void setMatch_seq_num(String match_seq_num) {
        this.match_seq_num = match_seq_num;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getLobby_type() {
        return lobby_type;
    }

    public void setLobby_type(String lobby_type) {
        this.lobby_type = lobby_type;
    }

    public List<HistoryPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<HistoryPlayer> players) {
        this.players = players;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(match_id);
        dest.writeString(match_seq_num);
        dest.writeString(start_time);
        dest.writeString(lobby_type);
        dest.writeTypedList(players);
    }

    public HistoryMatch(Parcel pc) {

        match_id = pc.readString();
        match_seq_num = pc.readString();
        start_time = pc.readString();
        lobby_type = pc.readString();
        pc.readTypedList(players, HistoryPlayer.CREATOR);
    }

    public static final Parcelable.Creator<HistoryMatch> CREATOR = new
            Parcelable.Creator<HistoryMatch>() {
                public HistoryMatch createFromParcel(Parcel pc) {
                    return new HistoryMatch(pc);
                }

                public HistoryMatch[] newArray(int size) {
                    return new HistoryMatch[size];
                }
            };
}
