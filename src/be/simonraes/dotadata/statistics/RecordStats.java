package be.simonraes.dotadata.statistics;

/**
 * Created by Simon Raes on 30/08/2014.
 */
public class RecordStats {

    private String matchId;
    private String record;
    private String recordValue;
    private String heroId;
    private String matchStart;
    private String gameMode;

    public RecordStats(String record){
        matchId = "-1";
        this.record = record;
        recordValue = "-1";
        heroId = "-1";
        matchStart = "-1";
        gameMode = "-1";
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getRecordValue() {
        return recordValue;
    }

    public void setRecordValue(String recordValue) {
        this.recordValue = recordValue;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getMatchStart() {
        return matchStart;
    }

    public void setMatchStart(String matchStart) {
        this.matchStart = matchStart;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
}
