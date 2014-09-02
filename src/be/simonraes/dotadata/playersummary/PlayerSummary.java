package be.simonraes.dotadata.playersummary;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Simon on 19/02/14.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSummary implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(steamid);
        dest.writeString(communityvisibilitystate);
        dest.writeString(profilestate);
        dest.writeString(personaname);
        dest.writeString(lastlogoff);
        dest.writeString(commentpermission);
        dest.writeString(profileurl);
        dest.writeString(avatar);
        dest.writeString(avatarmedium);
        dest.writeString(avatarfull);
        dest.writeString(personastate);
        dest.writeString(primaryclanid);
        dest.writeString(timecreated);
        dest.writeString(personastateflags);
        dest.writeString(loccountrycode);
    }

    public PlayerSummary(Parcel pc) {

        steamid = pc.readString();
        communityvisibilitystate = pc.readString();
        profilestate = pc.readString();
        personaname = pc.readString();
        lastlogoff = pc.readString();
        commentpermission = pc.readString();
        profileurl = pc.readString();
        avatar = pc.readString();
        avatarmedium = pc.readString();
        avatarfull = pc.readString();
        personastate = pc.readString();
        primaryclanid = pc.readString();
        timecreated = pc.readString();
        personastateflags = pc.readString();
        loccountrycode = pc.readString();
    }

    public static final Creator<PlayerSummary> CREATOR = new
            Creator<PlayerSummary>() {
                public PlayerSummary createFromParcel(Parcel pc) {
                    return new PlayerSummary(pc);
                }

                public PlayerSummary[] newArray(int size) {
                    return new PlayerSummary[size];
                }
            };
}
