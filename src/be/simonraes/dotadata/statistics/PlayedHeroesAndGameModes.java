package be.simonraes.dotadata.statistics;

import java.util.HashMap;

/**
 * Created by Simon Raes on 28/03/2014.
 */
public class PlayedHeroesAndGameModes {
    private HashMap<String, String> playedHeroes;
    private HashMap<String, String> playedGameModes;

    public PlayedHeroesAndGameModes() {
        playedHeroes = new HashMap<String, String>();
        playedGameModes = new HashMap<String, String>();
    }

    public HashMap<String, String> getPlayedHeroes() {
        return playedHeroes;
    }

    public void setPlayedHeroes(HashMap<String, String> playedHeroes) {
        this.playedHeroes = playedHeroes;
    }

    public HashMap<String, String> getPlayedGameModes() {
        return playedGameModes;
    }

    public void setPlayedGameModes(HashMap<String, String> playedGameModes) {
        this.playedGameModes = playedGameModes;
    }
}
