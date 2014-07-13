package be.simonraes.dotadata.util;

import java.util.ArrayList;

/**
 * Created by Simon Raes on 21/06/2014.
 */
public class ExperienceList {

    private static ArrayList<Integer> experienceList;
    private static ArrayList<Integer> experienceListLevelUp;

    public static ArrayList<Integer> getExperienceList() {
        if (experienceList == null) {
            initExperienceList();
        }
        return experienceList;
    }

    public static int getTotalExperienceRequiredForLevel(int level) {
        if (experienceList == null) {
            initExperienceList();
        }
        return experienceList.get(level);
    }

    public static int getExperienceRequiredForLevelUp(int level) {
        if (experienceListLevelUp == null) {
            initExperienceListLevelUp();
        }
        return experienceListLevelUp.get(level);
    }

    private static void initExperienceList() {
        experienceList = new ArrayList<Integer>();
        experienceList.add(0, 0);
        experienceList.add(1, 0);
        experienceList.add(2, 200);
        experienceList.add(3, 500);
        experienceList.add(4, 900);
        experienceList.add(5, 1400);
        experienceList.add(6, 2000);
        experienceList.add(7, 2600);
        experienceList.add(8, 3200);
        experienceList.add(9, 4400);
        experienceList.add(10, 5400);
        experienceList.add(11, 6000);
        experienceList.add(12, 8200);
        experienceList.add(13, 9000);
        experienceList.add(14, 10400);
        experienceList.add(15, 11900);
        experienceList.add(16, 13500);
        experienceList.add(17, 15200);
        experienceList.add(18, 17000);
        experienceList.add(19, 18900);
        experienceList.add(20, 20900);
        experienceList.add(21, 23000);
        experienceList.add(22, 25200);
        experienceList.add(23, 27500);
        experienceList.add(24, 29900);
        experienceList.add(25, 32400);
    }

    private static void initExperienceListLevelUp() {
        experienceListLevelUp = new ArrayList<Integer>();
        experienceListLevelUp.add(0, 0);
        experienceListLevelUp.add(1, 0);
        experienceListLevelUp.add(2, 200);
        experienceListLevelUp.add(3, 300);
        experienceListLevelUp.add(4, 400);
        experienceListLevelUp.add(5, 500);
        experienceListLevelUp.add(6, 600);
        experienceListLevelUp.add(7, 600);
        experienceListLevelUp.add(8, 600);
        experienceListLevelUp.add(9, 1200);
        experienceListLevelUp.add(10, 1000);
        experienceListLevelUp.add(11, 600);
        experienceListLevelUp.add(12, 2200);
        experienceListLevelUp.add(13, 800);
        experienceListLevelUp.add(14, 1400);
        experienceListLevelUp.add(15, 1500);
        experienceListLevelUp.add(16, 1500);
        experienceListLevelUp.add(17, 1700);
        experienceListLevelUp.add(18, 1800);
        experienceListLevelUp.add(19, 1900);
        experienceListLevelUp.add(20, 2000);
        experienceListLevelUp.add(21, 2100);
        experienceListLevelUp.add(22, 2200);
        experienceListLevelUp.add(23, 2300);
        experienceListLevelUp.add(24, 2400);
        experienceListLevelUp.add(25, 2500);
    }
}
