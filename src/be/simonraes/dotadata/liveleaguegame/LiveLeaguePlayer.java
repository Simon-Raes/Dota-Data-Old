package be.simonraes.dotadata.liveleaguegame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeaguePlayer implements Serializable{

    public String account_id;
    public String name;
    public String hero_id;
    public String team; //radiant (0), dire (1) or spectator (2)

    public LiveLeaguePlayer(){

    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
