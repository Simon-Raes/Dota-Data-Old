package be.simonraes.dotadata.util;

/**
 * Created by Simon on 8/02/14.
 * Contains the status of the map (towers and barracks) for a team
 */
public class TowerStatus {
    boolean topT1, topT2, topT3, midT1, midT2, midT3, botT1, botT2, botT3, topT4, botT4;

    public TowerStatus() {
        topT1 = false;
        topT2 = false;
        topT3 = false;
        midT1 = false;
        midT2 = false;
        midT3 = false;
        botT1 = false;
        botT2 = false;
        botT3 = false;
        topT4 = false;
        botT4 = false;
    }

    public boolean isTopT1() {
        return topT1;
    }

    public void setTopT1(boolean topT1) {
        this.topT1 = topT1;
    }

    public boolean isTopT2() {
        return topT2;
    }

    public void setTopT2(boolean topT2) {
        this.topT2 = topT2;
    }

    public boolean isTopT3() {
        return topT3;
    }

    public void setTopT3(boolean topT3) {
        this.topT3 = topT3;
    }

    public boolean isMidT1() {
        return midT1;
    }

    public void setMidT1(boolean midT1) {
        this.midT1 = midT1;
    }

    public boolean isMidT2() {
        return midT2;
    }

    public void setMidT2(boolean midT2) {
        this.midT2 = midT2;
    }

    public boolean isMidT3() {
        return midT3;
    }

    public void setMidT3(boolean midT3) {
        this.midT3 = midT3;
    }

    public boolean isBotT1() {
        return botT1;
    }

    public void setBotT1(boolean botT1) {
        this.botT1 = botT1;
    }

    public boolean isBotT2() {
        return botT2;
    }

    public void setBotT2(boolean botT2) {
        this.botT2 = botT2;
    }

    public boolean isBotT3() {
        return botT3;
    }

    public void setBotT3(boolean botT3) {
        this.botT3 = botT3;
    }

    public boolean isTopT4() {
        return topT4;
    }

    public void setTopT4(boolean topT4) {
        this.topT4 = topT4;
    }

    public boolean isBotT4() {
        return botT4;
    }

    public void setBotT4(boolean botT4) {
        this.botT4 = botT4;
    }
}
