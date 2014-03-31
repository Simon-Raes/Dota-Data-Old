package be.simonraes.dotadata.util;

import be.simonraes.dotadata.detailmatch.DetailMatch;
import be.simonraes.dotadata.detailmatch.DetailMatchLite;
import be.simonraes.dotadata.detailmatch.DetailPlayer;

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
}
