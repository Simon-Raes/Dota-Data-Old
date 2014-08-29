package be.simonraes.dotadata.statistics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Values used in the graphs.
 * Created by Simon Raes on 22/04/2014.
 */
public class GraphStats implements Parcelable {

    private int numberOfGamesPeriod; // Number of games played in this period (week)
    private int numberOfGamesCumulative; // Number of games played up to this period (this week + all previous weeks)

    private int gpmTotalPeriod;
    private int gpmTotalCumulative;
    private double gpmAveragedCumulative;

    private int winsPeriod, lossesPeriod;
    private double winrateCumulative;

    private int year, week;
    private String dateString;

    private int lastHitsTotalPeriod;
    private int lastHitsTotalCumulative;
    private double lastHitsAveragedCumulative;

    private int killsTotalPeriod;
    private int killsTotalCumulative;
    private double killsAveragedCumulative;

    private int deathsTotalPeriod;
    private int deathsTotalCumulative;
    private double deathsAveragedCumulative;

    private int assistsTotalPeriod;
    private int assistsTotalCumulative;
    private double assistsAveragedCumulative;

    public GraphStats() {
        numberOfGamesPeriod = winsPeriod = lossesPeriod = 0;
        winrateCumulative = 0.0;
        numberOfGamesCumulative = 0;
    }

    public int getNumberOfGamesPeriod() {
        return numberOfGamesPeriod;
    }

    public void setNumberOfGamesPeriod(int numberOfGamesPeriod) {
        this.numberOfGamesPeriod = numberOfGamesPeriod;
    }

    public int getWinsPeriod() {
        return winsPeriod;
    }

    public void setWinsPeriod(int winsPeriod) {
        this.winsPeriod = winsPeriod;
    }

    public int getLossesPeriod() {
        return lossesPeriod;
    }

    public void setLossesPeriod(int lossesPeriod) {
        this.lossesPeriod = lossesPeriod;
    }

    public double getWinrateCumulative() {
        return winrateCumulative;
    }

    public void setWinrateCumulative(double winrateCumulative) {
        this.winrateCumulative = winrateCumulative;
    }

    public int getNumberOfGamesCumulative() {
        return numberOfGamesCumulative;
    }

    public void setNumberOfGamesCumulative(int numberOfGamesCumulative) {
        this.numberOfGamesCumulative = numberOfGamesCumulative;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getGpmTotalPeriod() {
        return gpmTotalPeriod;
    }

    public void setGpmTotalPeriod(int gpmTotalPeriod) {
        this.gpmTotalPeriod = gpmTotalPeriod;
    }

    public int getGpmTotalCumulative() {
        return gpmTotalCumulative;
    }

    public void setGpmTotalCumulative(int gpmTotalCumulative) {
        this.gpmTotalCumulative = gpmTotalCumulative;
    }

    public double getGpmAveragedCumulative() {
        return gpmAveragedCumulative;
    }

    public void setGpmAveragedCumulative(double gpmAveragedCumulative) {
        this.gpmAveragedCumulative = gpmAveragedCumulative;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getLastHitsTotalPeriod() {
        return lastHitsTotalPeriod;
    }

    public void setLastHitsTotalPeriod(int lastHitsTotalPeriod) {
        this.lastHitsTotalPeriod = lastHitsTotalPeriod;
    }

    public int getLastHitsTotalCumulative() {
        return lastHitsTotalCumulative;
    }

    public void setLastHitsTotalCumulative(int lastHitsTotalCumulative) {
        this.lastHitsTotalCumulative = lastHitsTotalCumulative;
    }

    public double getLastHitsAveragedCumulative() {
        return lastHitsAveragedCumulative;
    }

    public void setLastHitsAveragedCumulative(double lastHitsAveragedCumulative) {
        this.lastHitsAveragedCumulative = lastHitsAveragedCumulative;
    }

    public int getKillsTotalPeriod() {
        return killsTotalPeriod;
    }

    public void setKillsTotalPeriod(int killsTotalPeriod) {
        this.killsTotalPeriod = killsTotalPeriod;
    }

    public int getKillsTotalCumulative() {
        return killsTotalCumulative;
    }

    public void setKillsTotalCumulative(int killsTotalCumulative) {
        this.killsTotalCumulative = killsTotalCumulative;
    }

    public double getKillsAveragedCumulative() {
        return killsAveragedCumulative;
    }

    public void setKillsAveragedCumulative(double killsAveragedCumulative) {
        this.killsAveragedCumulative = killsAveragedCumulative;
    }

    public int getDeathsTotalPeriod() {
        return deathsTotalPeriod;
    }

    public void setDeathsTotalPeriod(int deathsTotalPeriod) {
        this.deathsTotalPeriod = deathsTotalPeriod;
    }

    public int getDeathsTotalCumulative() {
        return deathsTotalCumulative;
    }

    public void setDeathsTotalCumulative(int deathsTotalCumulative) {
        this.deathsTotalCumulative = deathsTotalCumulative;
    }

    public double getDeathsAveragedCumulative() {
        return deathsAveragedCumulative;
    }

    public void setDeathsAveragedCumulative(double deathsAveragedCumulative) {
        this.deathsAveragedCumulative = deathsAveragedCumulative;
    }

    public int getAssistsTotalPeriod() {
        return assistsTotalPeriod;
    }

    public void setAssistsTotalPeriod(int assistsTotalPeriod) {
        this.assistsTotalPeriod = assistsTotalPeriod;
    }

    public int getAssistsTotalCumulative() {
        return assistsTotalCumulative;
    }

    public void setAssistsTotalCumulative(int assistsTotalCumulative) {
        this.assistsTotalCumulative = assistsTotalCumulative;
    }

    public double getAssistsAveragedCumulative() {
        return assistsAveragedCumulative;
    }

    public void setAssistsAveragedCumulative(double assistsAveragedCumulative) {
        this.assistsAveragedCumulative = assistsAveragedCumulative;
    }

    // parcelable
    @Override
    public int describeContents() {
        return 0;
    }


    /*private int numberOfGamesPeriod; // Number of games played in this period (week)
    private int numberOfGamesCumulative; // Number of games played up to this period (this week + all previous weeks)
    private int gpmTotalPeriod;
    private int gpmTotalCumulative;
    private double gpmAveragedCumulative;
    private int winsPeriod, lossesPeriod;
    private double winrateCumulative;
    private int year, week;
    private String dateString;*/


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(numberOfGamesPeriod);
        dest.writeInt(numberOfGamesCumulative);

        dest.writeInt(gpmTotalPeriod);
        dest.writeInt(gpmTotalCumulative);
        dest.writeDouble(gpmAveragedCumulative);

        dest.writeInt(winsPeriod);
        dest.writeInt(lossesPeriod);
        dest.writeDouble(winrateCumulative);

        dest.writeInt(year);
        dest.writeInt(week);
        dest.writeString(dateString);

        dest.writeInt(lastHitsTotalPeriod);
        dest.writeInt(lastHitsTotalCumulative);
        dest.writeDouble(lastHitsAveragedCumulative);

        dest.writeInt(killsTotalPeriod);
        dest.writeInt(killsTotalCumulative);
        dest.writeDouble(killsAveragedCumulative);

        dest.writeInt(deathsTotalPeriod);
        dest.writeInt(deathsTotalCumulative);
        dest.writeDouble(deathsAveragedCumulative);

        dest.writeInt(assistsTotalPeriod);
        dest.writeInt(assistsTotalCumulative);
        dest.writeDouble(assistsAveragedCumulative);
    }

    public GraphStats(Parcel pc) {
        numberOfGamesPeriod = pc.readInt();
        numberOfGamesCumulative = pc.readInt();

        gpmTotalPeriod = pc.readInt();
        gpmTotalCumulative = pc.readInt();
        gpmAveragedCumulative = pc.readDouble();

        winsPeriod = pc.readInt();
        lossesPeriod = pc.readInt();
        winrateCumulative = pc.readDouble();

        year = pc.readInt();
        week = pc.readInt();
        dateString = pc.readString();

        lastHitsTotalPeriod = pc.readInt();
        lastHitsTotalCumulative = pc.readInt();
        lastHitsAveragedCumulative = pc.readDouble();

        killsTotalPeriod = pc.readInt();
        killsTotalCumulative = pc.readInt();
        killsAveragedCumulative = pc.readDouble();

        deathsTotalPeriod = pc.readInt();
        deathsTotalCumulative = pc.readInt();
        deathsAveragedCumulative = pc.readDouble();

        assistsTotalPeriod = pc.readInt();
        assistsTotalCumulative = pc.readInt();
        assistsAveragedCumulative = pc.readDouble();
    }

    public static final Parcelable.Creator<GraphStats> CREATOR = new
            Parcelable.Creator<GraphStats>() {
                public GraphStats createFromParcel(Parcel pc) {
                    return new GraphStats(pc);
                }

                public GraphStats[] newArray(int size) {
                    return new GraphStats[size];
                }
            };
}
