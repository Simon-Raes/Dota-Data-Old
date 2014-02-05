package be.simonraes.dotadata.detailmatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 3/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PicksBans {

    private boolean is_pick;
    private String hero_id;
    private String team;
    private String order;

    public PicksBans(){

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
