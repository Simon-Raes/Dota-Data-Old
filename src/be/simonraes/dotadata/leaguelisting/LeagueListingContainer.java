package be.simonraes.dotadata.leaguelisting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Simon on 5/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueListingContainer {

    @JsonProperty("result")
    public Leagues leagues;

    public LeagueListingContainer() {
    }

    public Leagues getLeagues() {
        return leagues;
    }

    public void setLeagues(Leagues leagues) {
        this.leagues = leagues;
    }
}
