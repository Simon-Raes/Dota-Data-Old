package be.simonraes.dotadata.playersummary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 19/02/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSummary {

    private String steamid;
    private String communityvisibilitystate;
    private String profilestate;
    private String personaname;
    private String lastlogoff;
    private String commentpermission;
    private String profileurl;
    private String avatar;
    private String avatarmedium;
    private String avatarfull;
    private String personastate;
    private String primaryclanid;
    private String timecreated;
    private String personastateflags;
    private String loccountrycode;

    public PlayerSummary() {

    }

    public String getSteamid() {
        return steamid;
    }

    public void setSteamid(String steamid) {
        this.steamid = steamid;
    }

    public String getCommunityvisibilitystate() {
        return communityvisibilitystate;
    }

    public void setCommunityvisibilitystate(String communityvisibilitystate) {
        this.communityvisibilitystate = communityvisibilitystate;
    }

    public String getProfilestate() {
        return profilestate;
    }

    public void setProfilestate(String profilestate) {
        this.profilestate = profilestate;
    }

    public String getPersonaname() {
        return personaname;
    }

    public void setPersonaname(String personaname) {
        this.personaname = personaname;
    }

    public String getLastlogoff() {
        return lastlogoff;
    }

    public void setLastlogoff(String lastlogoff) {
        this.lastlogoff = lastlogoff;
    }

    public String getCommentpermission() {
        return commentpermission;
    }

    public void setCommentpermission(String commentpermission) {
        this.commentpermission = commentpermission;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarmedium() {
        return avatarmedium;
    }

    public void setAvatarmedium(String avatarmedium) {
        this.avatarmedium = avatarmedium;
    }

    public String getAvatarfull() {
        return avatarfull;
    }

    public void setAvatarfull(String avatarfull) {
        this.avatarfull = avatarfull;
    }

    public String getPersonastate() {
        return personastate;
    }

    public void setPersonastate(String personastate) {
        this.personastate = personastate;
    }

    public String getPrimaryclanid() {
        return primaryclanid;
    }

    public void setPrimaryclanid(String primaryclanid) {
        this.primaryclanid = primaryclanid;
    }

    public String getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(String timecreated) {
        this.timecreated = timecreated;
    }

    public String getPersonastateflags() {
        return personastateflags;
    }

    public void setPersonastateflags(String personastateflags) {
        this.personastateflags = personastateflags;
    }

    public String getLoccountrycode() {
        return loccountrycode;
    }

    public void setLoccountrycode(String loccountrycode) {
        this.loccountrycode = loccountrycode;
    }
}
