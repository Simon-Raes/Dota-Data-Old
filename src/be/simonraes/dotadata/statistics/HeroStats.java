package be.simonraes.dotadata.statistics;

/**
 * Created by Simon on 22/03/14.
 */
public class HeroStats {
    private int numberOfGames, wins, losses, longestMatch;
    private double winrate;

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
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }
}
