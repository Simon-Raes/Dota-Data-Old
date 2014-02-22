package be.simonraes.dotadata.historymatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryMatches implements Parcelable {

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

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(num_results);
        dest.writeString(total_results);
        dest.writeString(results_remaining);
        dest.writeTypedList(matches);
    }

    public HistoryMatches(Parcel pc) {
        status = pc.readString();
        num_results = pc.readString();
        total_results = pc.readString();
        results_remaining = pc.readString();
        pc.readTypedList(matches, HistoryMatch.CREATOR);
    }

    public static final Creator<HistoryMatches> CREATOR = new
            Creator<HistoryMatches>() {
                public HistoryMatches createFromParcel(Parcel pc) {
                    return new HistoryMatches(pc);
                }

                public HistoryMatches[] newArray(int size) {
                    return new HistoryMatches[size];
                }
            };


}
