package detailmatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailContainer implements Serializable{

    @JsonProperty("result")
    public DetailMatch detailMatch;

    public DetailContainer(){

    }

    public DetailMatch getDetailMatch() {
        return detailMatch;
    }

    public void setDetailMatch(DetailMatch detailMatch) {
        this.detailMatch = detailMatch;
    }
}
