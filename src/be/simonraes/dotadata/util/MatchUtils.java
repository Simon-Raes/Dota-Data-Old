package be.simonraes.dotadata.util;

import be.simonraes.dotadata.detailmatch.AbilityUpgrades;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.statistics.TeamExperienceStats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Simon Raes on 31/03/2014.
 */
public class MatchUtils {

    // Determines how many points will be created in the team experience graph
    private static final int TEAM_EXPERIENCE_GRAPH_INTERVAL = 60; // Create a point every 60 seconds

    public static boolean isUser_win(DetailMatch match, String accountID) {
        boolean userwin = false;
        for (DetailPlayer player : match.getPlayers()) {
            if (player.getAccount_id() != null) {
                if (player.getAccount_id().equals(accountID)) {
                    if ((Integer.parseInt(player.getPlayer_slot()) < 100 && match.getRadiant_win()) || (Integer.parseInt(player.getPlayer_slot()) > 100 && !match.getRadiant_win())) {
                        //player won
                        userwin = true;
                    } else {
                        userwin = false;
                    }

                }
            }
        }
        return userwin;
    }

    public static boolean isUser_win(DetailMatchLite matchLite) {
        if ((Integer.parseInt(matchLite.getPlayer_slot()) < 100 && matchLite.isRadiant_win()) || (Integer.parseInt(matchLite.getPlayer_slot()) > 100 && !matchLite.isRadiant_win())) {
            //player won
            return true;
        } else {
            return false;
        }
    }

    public static boolean isRadiant(DetailPlayer player) {
        if (Integer.parseInt(player.getPlayer_slot()) < 5) {
            return true;
        } else {
            return false;
        }
    }

    public static TeamExperienceStats getExperienceTeamGraphData(DetailMatch match) {
        TreeMap<Integer, Integer> expRadiant = new TreeMap<Integer, Integer>();
        TreeMap<Integer, Integer> expDire = new TreeMap<Integer, Integer>();

        ArrayList<DetailPlayer> players = match.getPlayers();

        // Check if the object contains any experience information
        boolean radiantHasExperience = false;
        boolean direHasExperience = false;

        for (DetailPlayer player : match.getPlayers()) {
            if (player.getAbilityupgrades().size() > 0) {
                if(isRadiant(player)){
                    radiantHasExperience = true;
                } else {
                    direHasExperience = true;
                }
            }
        }

        if (radiantHasExperience && direHasExperience) {

            for (DetailPlayer player : players) {
                for (AbilityUpgrades upgrade : player.getAbilityupgrades()) {
                    if (isRadiant(player)) {
                        addToHashMap(upgrade, expRadiant);
                    } else {
                        addToHashMap(upgrade, expDire);
                    }
                }
            }

            TeamExperienceStats teamStats = new TeamExperienceStats();

            //calculate total xp for both teams at all known points (timestamps) of the game
            int previoustotal = 0;
            for (Map.Entry<Integer, Integer> entry : expRadiant.entrySet()) {
                teamStats.getExpRadiant().put(entry.getKey(), previoustotal + entry.getValue());
                previoustotal = previoustotal + entry.getValue();
            }
            previoustotal = 0;
            for (Map.Entry<Integer, Integer> entry : expDire.entrySet()) {
                teamStats.getExpDire().put(entry.getKey(), previoustotal + entry.getValue());
                previoustotal = previoustotal + entry.getValue();
            }

            //calculate difference
            TreeMap<Double, Integer> joinedMap = new TreeMap<Double, Integer>();

            //find the latest entry to make both experience graphs end at the same timestamp
            int latestEntry;

            if (teamStats.getExpRadiant().lastEntry().getKey() > teamStats.getExpDire().lastEntry().getKey()) {
                latestEntry = teamStats.getExpRadiant().lastEntry().getKey();
            } else {
                latestEntry = teamStats.getExpDire().lastEntry().getKey();
            }


            //draw a point ever 60 seconds
            for (int i = 0; i < latestEntry; i += TEAM_EXPERIENCE_GRAPH_INTERVAL) {
                int radiantExperience = 0;
                for (Map.Entry<Integer, Integer> entry : teamStats.getExpRadiant().entrySet()) {
                    if (entry.getKey() <= i) {
                        radiantExperience = entry.getValue();
                    } else {
                        break;
                    }
                }
                int direExperience = 0;
                for (Map.Entry<Integer, Integer> entry : teamStats.getExpDire().entrySet()) {
                    if (entry.getKey() <= i) {
                        direExperience = entry.getValue();
                    } else {
                        break;
                    }
                }

                // Save the difference to the difference-map
                joinedMap.put((double)i, radiantExperience - direExperience);
            }

            // Find all points where the graph goes from negative to positive or positive to negative.
            // Add a new point on that zero-point so the graph has a place to switch color.
            TreeMap<Double, Integer> newValues = new TreeMap<Double, Integer>();

            for (Map.Entry<Double, Integer> entry : joinedMap.entrySet()) {

                // to find next entry
                if (joinedMap.containsKey(entry.getKey() + TEAM_EXPERIENCE_GRAPH_INTERVAL)) {
                    int expDifferenceAtCurrentPoint = entry.getValue();
                    int expDifferenceAtNextPoint = joinedMap.get(entry.getKey() + TEAM_EXPERIENCE_GRAPH_INTERVAL);

                    int currentFromZeroY, nextFromZeroY;

                    // Only check if the graph goes from negative to positive or positive to negative
                    if ((expDifferenceAtCurrentPoint < 0 && expDifferenceAtNextPoint > 0) ||
                            (expDifferenceAtCurrentPoint > 0 && expDifferenceAtNextPoint < 0)) {
                        if (expDifferenceAtCurrentPoint < 0) {
                            currentFromZeroY = 0 - expDifferenceAtCurrentPoint;
                            nextFromZeroY = expDifferenceAtNextPoint;
                        } else {
                            currentFromZeroY = expDifferenceAtCurrentPoint;
                            nextFromZeroY = 0 - expDifferenceAtNextPoint;
                        }

                        int totalYDifference = currentFromZeroY + nextFromZeroY;
                        double pointZeroIsXDistanceFromCurrentPoint = (double)TEAM_EXPERIENCE_GRAPH_INTERVAL * ((double)currentFromZeroY/ (double)totalYDifference);
                        newValues.put(entry.getKey() + pointZeroIsXDistanceFromCurrentPoint, 0);
                    }
                }
            }

            joinedMap.putAll(newValues);
            teamStats.setExpJoined(joinedMap);

            return teamStats;
        } else {
            return null;
        }
    }

    private static void addToHashMap(AbilityUpgrades upgrade, TreeMap<Integer, Integer> map) {
        int expAtTime = 0;
        if (map.containsKey(Integer.parseInt(upgrade.getTime()))) {
            expAtTime = map.get(Integer.parseInt(upgrade.getTime())) + ExperienceList.getExperienceRequiredForLevelUp(Integer.parseInt(upgrade.getLevel()));
        } else {
            expAtTime = ExperienceList.getExperienceRequiredForLevelUp(Integer.parseInt(upgrade.getLevel()));
        }
        map.put(Integer.parseInt(upgrade.getTime()), expAtTime);
    }
}
