package be.simonraes.dotadata.util;

import android.support.v4.widget.DrawerLayout;

/**
 * Created by Simon on 2/02/14.
 */
public class Conversions {

    private final static long ONE_SECOND = 1000;
    private final static long SECONDS = 60;

    private final static long ONE_MINUTE = ONE_SECOND * 60;
    private final static long MINUTES = 60;

    private final static long ONE_HOUR = ONE_MINUTE * 60;
    private final static long HOURS = 24;

    private final static long ONE_DAY = ONE_HOUR * 24;

    /**
     * Returns match starting time in format "x days/hours/minutes ago"
     */
    public static String millisToDate(String start) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(Long.parseLong(start) * 1000, System.currentTimeMillis(), 0, 0).toString();
    }

    public static String secondsToTime(String matchDuration) {
        int iSeconds = Integer.parseInt(matchDuration) % 60;
        String seconds;
        if (iSeconds < 10) {
            seconds = "0" + Integer.toString(iSeconds);
        } else {
            seconds = Integer.toString(iSeconds);
        }
        String minutes = Integer.toString(Integer.parseInt(matchDuration) / 60);
        return minutes + ":" + seconds;
    }

    public static String leagueTitleToString(String rawLeague) {
        if (rawLeague.startsWith("#DOTA_")) {
            rawLeague = rawLeague.replace("#DOTA_", "");
        }
        if (rawLeague.startsWith("Item_")) {
            rawLeague = rawLeague.replace("Item_", "");
        }
        if (rawLeague.startsWith("League_")) {
            rawLeague = rawLeague.replace("League_", "");
        }
        rawLeague = rawLeague.replace("_", " ");
        return rawLeague;
    }
}
