package liveleaguegame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueMatches implements Serializable{

    @JsonProperty("games")
    public ArrayList<LiveLeagueMatch> liveLeagueMatches = new ArrayList<LiveLeagueMatch>();

    public LiveLeagueMatches(){

    }

    public ArrayList<LiveLeagueMatch> getLiveLeagueMatches() {
        return liveLeagueMatches;
    }

    public void setLiveLeagueMatches(ArrayList<LiveLeagueMatch> liveLeagueMatches) {
        this.liveLeagueMatches = liveLeagueMatches;
    }
}
