package be.simonraes.dotadata.statistics;

import java.util.TreeMap;

/**
 * Created by Simon Raes on 21/06/2014.
 */
public class TeamExperienceStats {
    TreeMap<Integer, Integer> expRadiant;
    TreeMap<Integer, Integer> expDire;
    TreeMap<Integer, Integer> expJoined;

    public TeamExperienceStats() {
        expRadiant = new TreeMap<Integer, Integer>();
        expDire = new TreeMap<Integer, Integer>();
    }

    public TreeMap<Integer, Integer> getExpRadiant() {
        return expRadiant;
    }

    public void setExpRadiant(TreeMap<Integer, Integer> expRadiant) {
        this.expRadiant = expRadiant;
    }

    public TreeMap<Integer, Integer> getExpDire() {
        return expDire;
    }

    public void setExpDire(TreeMap<Integer, Integer> expDire) {
        this.expDire = expDire;
    }

    public TreeMap<Integer, Integer> getExpJoined() {
        return expJoined;
    }

    public void setExpJoined(TreeMap<Integer, Integer> expJoined) {
        this.expJoined = expJoined;
    }
}
