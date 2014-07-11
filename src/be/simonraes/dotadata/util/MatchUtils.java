package be.simonraes.dotadata.util;

import be.simonraes.dotadata.detailmatch.AbilityUpgrades;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.detailmatch.DetailPlayer;
import be.simonraes.dotadata.statistics.TeamExperienceStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Simon Raes on 31/03/2014.
 */
public class MatchUtils {

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

    public static TeamExperienceStats getExperienceTeamGraphData(ArrayList<DetailPlayer> players) {
        TreeMap<Integer, Integer> expRadiant = new TreeMap<Integer, Integer>();
        TreeMap<Integer, Integer> expDire = new TreeMap<Integer, Integer>();

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

        TreeMap<Integer, Integer> totalValues = new TreeMap<Integer, Integer>();
        int previousTotal = 0;


        return teamStats;
    }

    private static void addToHashMap(AbilityUpgrades upgrade, TreeMap<Integer, Integer> map) {
        int expAtTime = 0;
        if (map.containsKey(Integer.parseInt(upgrade.getTime()))) {
            expAtTime = map.get(Integer.parseInt(upgrade.getTime())) + ExperienceList.getTotalExperienceRequiredForLevel(Integer.parseInt(upgrade.getLevel()));
        } else {
            expAtTime = ExperienceList.getTotalExperienceRequiredForLevel(Integer.parseInt(upgrade.getLevel()));
        }
        map.put(Integer.parseInt(upgrade.getTime()), expAtTime);
    }
}
