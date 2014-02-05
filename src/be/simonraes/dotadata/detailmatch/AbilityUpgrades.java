package be.simonraes.dotadata.detailmatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbilityUpgrades implements Serializable{

    private String ability;
    private String time;
    private String level;

    public AbilityUpgrades(){

    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
