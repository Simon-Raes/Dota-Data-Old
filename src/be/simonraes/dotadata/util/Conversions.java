package be.simonraes.dotadata.util;

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

    /**
     * Convert tower_status_radiant/dire to a TowerStatus object that contains the status of all towers for that team.
     */
    public static TowerStatus towerStatusFromString(String status) {
        String bin = binaryTo16String(status);
        return generateTowerStatus(bin);
    }

    //todo: remove duplicate stringbuilder code
    public static TowerStatus radiantTowerStatusFromTeamString(String status) {

        String radiantCode = binaryTo32String(status).substring(21, 32);

        if (radiantCode.length() < 16) {
            StringBuilder builder;
            while (radiantCode.length() < 16) {
                builder = new StringBuilder(radiantCode);
                radiantCode = builder.insert(0, "0").toString();
            }
        }


        System.out.println("status from json: " + status);
        System.out.println("binary 32: " + binaryTo32String(status));
        System.out.println("radiant code: " + radiantCode);


        return generateTowerStatus(radiantCode);
    }

    public static TowerStatus direTowerStatusFromTeamString(String status) {
        String direCode = binaryTo32String(status).substring(10, 21);

        if (direCode.length() < 16) {
            StringBuilder builder;
            while (direCode.length() < 16) {
                builder = new StringBuilder(direCode);
                direCode = builder.insert(0, "0").toString();
            }
        }


        System.out.println("status from json: " + status);
        System.out.println("binary 32: " + binaryTo32String(status));
        System.out.println("direCode code: " + direCode);

        return generateTowerStatus(direCode);
    }


    private static String binaryTo32String(String status) {
        int iStatus = Integer.parseInt(status);

        String bin = Integer.toBinaryString(iStatus);

        if (bin.length() < 32) {
            StringBuilder builder;
            while (bin.length() < 32) {
                builder = new StringBuilder(bin);
                bin = builder.insert(0, "0").toString();
            }
        }
        return bin;
    }


    private static String binaryTo16String(String status) {

        int iStatus = Integer.parseInt(status);

        String bin = Integer.toBinaryString(iStatus);

        if (bin.length() < 16) {
            StringBuilder builder;
            while (bin.length() < 16) {
                builder = new StringBuilder(bin);
                bin = builder.insert(0, "0").toString();
            }
        }

        return bin;
    }

    private static TowerStatus generateTowerStatus(String bin) {
        TowerStatus ts = new TowerStatus();

        if (Character.toString(bin.charAt(5)).equals("1")) {
            ts.setTopT4(true);
        }
        if (Character.toString(bin.charAt(6)).equals("1")) {
            ts.setBotT4(true);
        }

        if (Character.toString(bin.charAt(7)).equals("1")) {
            ts.setBotT3(true);
        }
        if (Character.toString(bin.charAt(8)).equals("1")) {
            ts.setBotT2(true);
        }
        if (Character.toString(bin.charAt(9)).equals("1")) {
            ts.setBotT1(true);
        }

        if (Character.toString(bin.charAt(10)).equals("1")) {
            ts.setMidT3(true);
        }
        if (Character.toString(bin.charAt(11)).equals("1")) {
            ts.setMidT2(true);
        }
        if (Character.toString(bin.charAt(12)).equals("1")) {
            ts.setMidT1(true);
        }

        if (Character.toString(bin.charAt(13)).equals("1")) {
            ts.setTopT3(true);
        }
        if (Character.toString(bin.charAt(14)).equals("1")) {
            ts.setTopT2(true);
        }
        if (Character.toString(bin.charAt(15)).equals("1")) {
            ts.setTopT1(true);
        }

        return ts;
    }
}
