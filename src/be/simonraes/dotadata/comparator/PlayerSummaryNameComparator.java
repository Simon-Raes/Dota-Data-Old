package be.simonraes.dotadata.comparator;

import be.simonraes.dotadata.friendslist.Friend;
import be.simonraes.dotadata.playersummary.PlayerSummary;
import be.simonraes.dotadata.statistics.HeroStats;

import java.util.Comparator;

/**
 * Created by Simon Raes on 2/09/2014.
 */
public class PlayerSummaryNameComparator implements Comparator<PlayerSummary> {

    @Override
    public int compare(PlayerSummary friendOne, PlayerSummary friendTwo) {
        return friendOne.getPersonaname().compareTo(friendTwo.getPersonaname());
    }
}
