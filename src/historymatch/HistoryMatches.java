package historymatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMatches implements Serializable{

    private String status;
    private String num_results;
    private String total_results;
    private String results_remaining;

    @JsonProperty("matches")
    private ArrayList<HistoryMatch> matches = new ArrayList<HistoryMatch>();

    public HistoryMatches(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNum_results() {
        return num_results;
    }

    public void setNum_results(String num_results) {
        this.num_results = num_results;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

    public String getResults_remaining() {
        return results_remaining;
    }

    public void setResults_remaining(String results_remaining) {
        this.results_remaining = results_remaining;
    }

    public ArrayList<HistoryMatch> getMatches() {
        return matches;
    }

    public void setMatches(ArrayList<HistoryMatch> games) {
        this.matches = games;
    }



}
