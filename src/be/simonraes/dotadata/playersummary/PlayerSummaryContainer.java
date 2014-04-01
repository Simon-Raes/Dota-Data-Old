package be.simonraes.dotadata.playersummary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by Simon on 19/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSummaryContainer {

    @JsonProperty("response")
    public PlayerSummaryPlayers players;

    public PlayerSummaryContainer() {

    }

    public PlayerSummaryPlayers getPlayers() {
        return players;
    }

    public void setPlayers(PlayerSummaryPlayers players) {
        this.players = players;
    }
}
