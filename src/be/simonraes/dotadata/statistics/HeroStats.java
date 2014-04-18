package be.simonraes.dotadata.statistics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simon on 22/03/14.
 */
public class HeroStats implements Parcelable {
    private String hero_id;
    private int numberOfGames, wins, losses, longestMatch;
    private double winrate;

    public HeroStats() {
        hero_id = "0";
        numberOfGames = 0;
        wins = 0;
        losses = 0;
        longestMatch = 0; //eeh, pointless
        winrate = 0;

        //can contain more stats! highest gpm, xpm, ...
    }

    public String getHero_id() {
        return hero_id;
    }

    public void setHero_id(String hero_id) {
        this.hero_id = hero_id;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getLongestMatch() {
        return longestMatch;
    }

    public void setLongestMatch(int longestMatch) {
        this.longestMatch = longestMatch;
    }

    public double getWinrate() {
        if (numberOfGames == 0) {
            return 0;
        } else {
            return ((double) wins / (double) numberOfGames) * 100;
        }
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }


    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hero_id);
        dest.writeInt(numberOfGames);
        dest.writeInt(wins);
        dest.writeInt(losses);
        dest.writeInt(longestMatch);
        dest.writeDouble(winrate);
    }

    public HeroStats(Parcel pc) {
        hero_id = pc.readString();
        numberOfGames = pc.readInt();
        wins = pc.readInt();
        losses = pc.readInt();
        longestMatch = pc.readInt();
        winrate = pc.readDouble();
    }

    public static final Creator<HeroStats> CREATOR = new
            Creator<HeroStats>() {
                public HeroStats createFromParcel(Parcel pc) {
                    return new HeroStats(pc);
                }

                public HeroStats[] newArray(int size) {
                    return new HeroStats[size];
                }
            };
}
