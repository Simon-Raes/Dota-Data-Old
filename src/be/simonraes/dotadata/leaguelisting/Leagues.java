package be.simonraes.dotadata.leaguelisting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Simon on 5/02/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Leagues {

    @JsonProperty("leagues")
    public ArrayList<League> leagues = new ArrayList<League>();

    public Leagues() {

    }

    public ArrayList<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(ArrayList<League> leagues) {
        this.leagues = leagues;
    }
}
