package be.simonraes.dotadata.vanity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 20/02/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class VanityResponse {

    public String steamid;
    public String success;

    public VanityResponse() {

    }

    public String getSteamid() {
        return steamid;
    }

    public void setSteamid(String steamid) {
        this.steamid = steamid;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}
