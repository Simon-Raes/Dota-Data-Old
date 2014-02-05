package be.simonraes.dotadata.liveleaguegame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 4/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveLeagueMatch implements Serializable{

    @JsonProperty("players")
    public ArrayList<LiveLeaguePlayer> liveLeaguePlayers = new ArrayList<LiveLeaguePlayer>();
    @JsonProperty("radiant_team")
    public LiveLeagueTeam radiantTeam;
    @JsonProperty("dire_team")
    public LiveLeagueTeam direTeam;
    public String lobby_id;
    public String spectators;
    public String tower_state;
    public String league_id;

    public LiveLeagueMatch(){

    }

    public ArrayList<LiveLeaguePlayer> getLiveLeaguePlayers() {
        return liveLeaguePlayers;
    }

    public void setLiveLeaguePlayers(ArrayList<LiveLeaguePlayer> liveLeaguePlayers) {
        this.liveLeaguePlayers = liveLeaguePlayers;
    }

    public LiveLeagueTeam getRadiantTeam() {
        return radiantTeam;
    }

    public void setRadiantTeam(LiveLeagueTeam radiantTeam) {
        this.radiantTeam = radiantTeam;
    }

    public LiveLeagueTeam getDireTeam() {
        return direTeam;
    }

    public void setDireTeam(LiveLeagueTeam direTeam) {
        this.direTeam = direTeam;
    }

    public String getLobby_id() {
        return lobby_id;
    }

    public void setLobby_id(String lobby_id) {
        this.lobby_id = lobby_id;
    }

    public String getSpectators() {
        return spectators;
    }

    public void setSpectators(String spectators) {
        this.spectators = spectators;
    }

    public String getTower_state() {
        return tower_state;
    }

    public void setTower_state(String tower_state) {
        this.tower_state = tower_state;
    }

    public String getLeague_id() {
        return league_id;
    }

    public void setLeague_id(String league_id) {
        this.league_id = league_id;
    }
}
