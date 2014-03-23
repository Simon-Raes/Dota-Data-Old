package be.simonraes.dotadata.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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


    public static String dotaIDToCommunityID(String accountID) {
        long steamID;
        String accID = accountID;

        try {
            Long.parseLong(accID);
        } catch (Exception ex) {
            accID = "0";
        }

        if (accID.equals("")) {
            steamID = 0;
        } else {
            steamID = 76561197960265728L + Long.parseLong(accID);
        }

        return Long.toString(steamID);
    }


    public static String community64IDToDota64ID(String communityID) {
        long accountID = 0L;
        try {
            accountID = Long.parseLong(communityID) - 76561197960265728L;
        } catch (NumberFormatException ex) {
            accountID = 0L;
        } finally {
            //todo: needs a better check to see if number matches an account

            if (accountID < 0) {
                return "0";
            } else {
                return Long.toString(accountID);

            }
        }
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

    public static LinkedHashMap sortHashMapByValues(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (String) val);
                    break;
                }

            }

        }
        return sortedMap;
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
