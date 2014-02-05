package be.simonraes.dotadata.historymatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryContainer implements Serializable{

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
}
