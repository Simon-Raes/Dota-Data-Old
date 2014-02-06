package be.simonraes.dotadata.teamlogo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Simon on 6/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamLogoContainer {

    @JsonProperty("data")
    public TeamLogo teamlogo;

    public TeamLogo getTeamlogo() {
        return teamlogo;
    }

    public void setTeamlogo(TeamLogo teamlogo) {
        this.teamlogo = teamlogo;
    }
}
