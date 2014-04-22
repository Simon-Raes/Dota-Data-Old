package be.simonraes.dotadata.statistics;

/**
 * Created by Simon Raes on 22/04/2014.
 */
public class GraphStats {
    private int numberOfGames, wins, losses;
    private double winrateCalc;
    private int numberOfGamesCalc;
    private String dateString;

    public GraphStats() {
        numberOfGames = wins = losses = 0;
        winrateCalc = 0.0;
        numberOfGamesCalc = 0;
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

    public double getWinrateCalc() {
        return winrateCalc;
    }

    public void setWinrateCalc(double winrateCalc) {
        this.winrateCalc = winrateCalc;
    }

    public int getNumberOfGamesCalc() {
        return numberOfGamesCalc;
    }

    public void setNumberOfGamesCalc(int numberOfGamesCalc) {
        this.numberOfGamesCalc = numberOfGamesCalc;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}
