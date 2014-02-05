package be.simonraes.dotadata.liveleaguegame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueContainer implements Serializable{

    @JsonProperty("result")
    public LiveLeagueMatches liveLeagueMatches;

    public LiveLeagueContainer(){

    }

    public LiveLeagueMatches getLiveLeagueMatches() {
        return liveLeagueMatches;
    }

    public void setLiveLeagueMatches(LiveLeagueMatches liveLeagueMatches) {
        this.liveLeagueMatches = liveLeagueMatches;
    }
}
