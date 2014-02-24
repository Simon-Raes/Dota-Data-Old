package be.simonraes.dotadata.user;

/**
 * Created by Simon on 23/02/14.
 */
public class User {

    private String account_id;
    private String steam_id;
    private String name;
    private String avatar;
    private String last_saved_match;

    public User() {
    }

    public User(String account_id, String steam_id, String name, String avatar) {
        this.account_id = account_id;
        this.steam_id = steam_id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getSteam_id() {
        return steam_id;
    }

    public void setSteam_id(String steam_id) {
        this.steam_id = steam_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLast_saved_match() {
        return last_saved_match;
    }

    public void setLast_saved_match(String last_saved_match) {
        this.last_saved_match = last_saved_match;
    }
}
