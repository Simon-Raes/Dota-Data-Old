package be.simonraes.dotadata.playersummary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Simon on 19/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSummaryPlayers {

    @JsonProperty("players")
    private ArrayList<PlayerSummary> players = new ArrayList<PlayerSummary>();

    public PlayerSummaryPlayers() {

    }

    public ArrayList<PlayerSummary> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<PlayerSummary> players) {
        this.players = players;
    }
}
