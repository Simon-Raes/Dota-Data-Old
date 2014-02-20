package be.simonraes.dotadata.vanity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 20/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VanityContainer {

    public VanityResponse response;


    public VanityContainer() {

    }

    public VanityResponse getResponse() {
        return response;
    }

    public void setResponse(VanityResponse response) {
        this.response = response;
    }
}
