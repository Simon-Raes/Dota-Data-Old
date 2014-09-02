package be.simonraes.dotadata.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Converts values from the API data to objects.
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

    /**
     * Converts a number of seconds to a formatted duration string.
     */
    public static String secondsToTime(String matchDuration) {
        long iSeconds = Long.parseLong(matchDuration);
        long millis = iSeconds * 1000;

        long lHours = TimeUnit.MILLISECONDS.toHours(millis);
        long lMinutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(lHours);
        long lSeconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(lHours) - TimeUnit.MINUTES.toSeconds(lMinutes);

        String hours = Long.toString(lHours);
        String minutes = "", seconds = "";

        if (lMinutes < 10) {
            minutes = "0" + lMinutes;
        } else if (lMinutes < 1) {
            minutes = "00";
        } else {
            minutes = Long.toString(lMinutes);
        }

        if (lSeconds < 10) {
            seconds = "0" + lSeconds;
        } else if (lSeconds < 1) {
            seconds = "00";
        } else {
            seconds = Long.toString(lSeconds);
        }

        return hours + ":" + minutes + ":" + seconds;
    }


    public static String steam32IdToSteam64Id(String accountID) {
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

    public static String steam64IdToSteam32Id(String communityID) {
        long accountID = 0L;
        try {
            accountID = Long.parseLong(communityID) - 76561197960265728L;
        } catch (NumberFormatException ex) {
            accountID = 0L;
        } finally {
            if (accountID < 0) {
                return "0";
            } else {
                return Long.toString(accountID);
            }
        }
    }

    /**
     * Converts tower/barracks_status_radiant/dire to a TowerStatusGroup object that contains the status of all towers and barracks.
     */
    public static ArrayList<BuildingStatus> towerStatusFromString(String radiantTowers, String direTowers, String radiantBarracks, String direBarracks) {
        String radiantTowerBits = binaryToString(radiantTowers, 16);
        String direTowerBits = binaryToString(direTowers, 16);
        String radiantBarracksBits = binaryToString(radiantBarracks, 8);
        String direBarracksBits = binaryToString(direBarracks, 8);

        ArrayList<BuildingStatus> buildings = new ArrayList<BuildingStatus>();

        // Add barracks first so the towers will be rendered over them.
        generateRadiantBarracksStatus(buildings, radiantBarracksBits);
        generateDireBarracksStatus(buildings, direBarracksBits);
        generateRadiantTowerStatus(buildings, radiantTowerBits);
        generateDireTowerStatus(buildings, direTowerBits);

        return buildings;
    }

    private static String binaryToString(String status, int numberOfBits) {

        String binaryBits = Integer.toBinaryString(Integer.parseInt(status));

        if (binaryBits.length() < numberOfBits) {
            StringBuilder builder;
            while (binaryBits.length() < numberOfBits) {
                builder = new StringBuilder(binaryBits);
                binaryBits = builder.insert(0, "0").toString();
            }
        }
        return binaryBits;
    }

    private static void generateRadiantTowerStatus(ArrayList<BuildingStatus> towerStatusGroup, String bits) {

        if (Character.toString(bits.charAt(5)).equals("1")) {
            // Top T4
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .145, .825));
        }
        if (Character.toString(bits.charAt(6)).equals("1")) {
            // Bot t4
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .165, .845));
        }

        if (Character.toString(bits.charAt(7)).equals("1")) {
            // Bot t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .26, .90));
        }
        if (Character.toString(bits.charAt(8)).equals("1")) {
            // Bot t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .47, .90));
        }
        if (Character.toString(bits.charAt(9)).equals("1")) {
            // Bot t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .8, .89));
        }

        if (Character.toString(bits.charAt(10)).equals("1")) {
            // Mid t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .22, .77));
        }
        if (Character.toString(bits.charAt(11)).equals("1")) {
            // Mid t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .29, .68));
        }
        if (Character.toString(bits.charAt(12)).equals("1")) {
            // Mid t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .41, .59));
        }

        if (Character.toString(bits.charAt(13)).equals("1")) {
            // Top t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .1, .72));
        }
        if (Character.toString(bits.charAt(14)).equals("1")) {
            // Top t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .12, .56));
        }
        if (Character.toString(bits.charAt(15)).equals("1")) {
            // Top t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.TOWER, .12, .39));
        }
    }

    private static void generateRadiantBarracksStatus(ArrayList<BuildingStatus> towerStatusGroup, String bits) {
        if (Character.toString(bits.charAt(2)).equals("1")) {
            // Bottom ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .24, .88));
        }
        if (Character.toString(bits.charAt(3)).equals("1")) {
            // Bottom melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .24, .92));
        }

        if (Character.toString(bits.charAt(4)).equals("1")) {
            // Mid ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .19, .77));
        }
        if (Character.toString(bits.charAt(5)).equals("1")) {
            // Mid melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .22, .80));
        }

        if (Character.toString(bits.charAt(6)).equals("1")) {
            // Top ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .08, .74));
        }
        if (Character.toString(bits.charAt(7)).equals("1")) {
            // Top melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.RADIANT, BuildingStatus.BuildingType.BARRACKS, .12, .74));
        }
    }

    private static void generateDireTowerStatus(ArrayList<BuildingStatus> towerStatusGroup, String bits) {

        if (Character.toString(bits.charAt(5)).equals("1")) {
            // Top t4
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .82, .19));
        }
        if (Character.toString(bits.charAt(6)).equals("1")) {
            // Bot t4
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .84, .21));
        }

        if (Character.toString(bits.charAt(7)).equals("1")) {
            // Bot t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .89, .33));
        }
        if (Character.toString(bits.charAt(8)).equals("1")) {
            // Bot t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .88, .50));
        }
        if (Character.toString(bits.charAt(9)).equals("1")) {
            // Bot t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .87, .62));
        }

        if (Character.toString(bits.charAt(10)).equals("1")) {
            // Mid t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .76, .28));
        }
        if (Character.toString(bits.charAt(11)).equals("1")) {
            // Mid t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .65, .37));
        }
        if (Character.toString(bits.charAt(12)).equals("1")) {
            // Mid t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .57, .49));
        }

        if (Character.toString(bits.charAt(13)).equals("1")) {
            // Top t3
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .72, .15));
        }
        if (Character.toString(bits.charAt(14)).equals("1")) {
            // Top t2
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .49, .13));
        }
        if (Character.toString(bits.charAt(15)).equals("1")) {
            // Top t1
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.TOWER, .2, .12));
        }

    }



    private static void generateDireBarracksStatus(ArrayList<BuildingStatus> towerStatusGroup, String bits) {
        if (Character.toString(bits.charAt(2)).equals("1")) {
            // Bottom ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .87, .31));
        }
        if (Character.toString(bits.charAt(3)).equals("1")) {
            // Bottom melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .91, .31));
        }

        if (Character.toString(bits.charAt(4)).equals("1")) {
            // Mid ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .755, .26));
        }
        if (Character.toString(bits.charAt(5)).equals("1")) {
            // Top melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .785, .28));
        }

        if (Character.toString(bits.charAt(6)).equals("1")) {
            // Top ranged
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .74, .135));
        }
        if (Character.toString(bits.charAt(7)).equals("1")) {
            // Top melee
            towerStatusGroup.add(new BuildingStatus(BuildingStatus.Side.DIRE, BuildingStatus.BuildingType.BARRACKS, .74, .17));
        }
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
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static double roundDouble(double value, int places) {
        if (value > 0) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } else {
            return 0;
        }

    }
}
