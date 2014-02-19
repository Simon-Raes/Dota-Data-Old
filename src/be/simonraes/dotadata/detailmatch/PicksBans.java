package be.simonraes.dotadata.detailmatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 3/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PicksBans implements Serializable {

    private String match_id; //extra field for database

    private boolean is_pick;
    private String hero_id;
    private String team;
    private String order;

    public PicksBans() {

    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public boolean isIs_pick() {
        return is_pick;
    }

    public void setIs_pick(boolean is_pick) {
        this.is_pick = is_pick;
    }

    public String getHero_id() {
        return hero_id;
    }

    public void setHero_id(String hero_id) {
        this.hero_id = hero_id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
