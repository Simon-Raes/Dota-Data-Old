package be.simonraes.dotadata.leaguelisting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 5/02/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class League {

    public String name;
    public String leagueid;
    public String description;
    public String tournament_url;
    public String itemdef;

    public League() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeagueid() {
        return leagueid;
    }

    public void setLeagueid(String leagueid) {
        this.leagueid = leagueid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTournament_url() {
        return tournament_url;
    }

    public void setTournament_url(String tournament_url) {
        this.tournament_url = tournament_url;
    }

    public String getItemdef() {
        return itemdef;
    }

    public void setItemdef(String itemdef) {
        this.itemdef = itemdef;
    }
}
