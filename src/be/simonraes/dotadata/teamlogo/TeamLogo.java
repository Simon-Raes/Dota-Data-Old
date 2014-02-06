package be.simonraes.dotadata.teamlogo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 6/02/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamLogo {

    public String filename;
    public String url;
    public String size;

    public TeamLogo() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
