package be.simonraes.dotadata.detailmatch;

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
public class DetailMatch implements Parcelable {

    //all games
    @JsonProperty("players")
    private ArrayList<DetailPlayer> players = new ArrayList<DetailPlayer>();
    private boolean radiant_win;
    private String duration;
    private String start_time;
    private String match_id;
    private String match_seq_num;
    private String tower_status_radiant;
    private String tower_status_dire;
    private String barracks_status_radiant;
    private String barracks_status_dire;
    private String cluster;
    private String first_blood_time;
    private String lobby_type;
    private String human_players;
    private String leagueid;
    private String positive_votes;
    private String negative_votes;
    private String game_mode;

    private boolean user_win; //extra database field

    //ranked games
    private String radiant_guild_id;
    private String radiant_guild_name;
    private String radiant_guild_logo;
    private String dire_guild_id;
    private String dire_guild_name;
    private String dire_guild_logo;
    @JsonProperty("picks_bans")
    private ArrayList<PicksBans> picks_bans = new ArrayList<PicksBans>();

    //league games
    private String radiant_name; //team name
    private String radiant_logo; //team logo
    private String radiant_team_complete; //true if all players in the radiant belong to the team
    private String dire_name;
    private String dire_logo;
    private String dire_team_complete;

    //features
    private boolean favourite;
    private String note;

    public DetailMatch() {

    }

    public boolean getRadiant_win() {
        return radiant_win;
    }

    public void setRadiant_win(boolean radiant_win) {
        this.radiant_win = radiant_win;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getMatch_seq_num() {
        return match_seq_num;
    }

    public void setMatch_seq_num(String match_seq_num) {
        this.match_seq_num = match_seq_num;
    }

    public String getTower_status_radiant() {
        return tower_status_radiant;
    }

    public void setTower_status_radiant(String tower_status_radiant) {
        this.tower_status_radiant = tower_status_radiant;
    }

    public String getTower_status_dire() {
        return tower_status_dire;
    }

    public void setTower_status_dire(String tower_status_dire) {
        this.tower_status_dire = tower_status_dire;
    }

    public String getBarracks_status_radiant() {
        return barracks_status_radiant;
    }

    public void setBarracks_status_radiant(String barracks_status_radiant) {
        this.barracks_status_radiant = barracks_status_radiant;
    }

    public String getBarracks_status_dire() {
        return barracks_status_dire;
    }

    public void setBarracks_status_dire(String barracks_status_dire) {
        this.barracks_status_dire = barracks_status_dire;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getFirst_blood_time() {
        return first_blood_time;
    }

    public void setFirst_blood_time(String first_blood_time) {
        this.first_blood_time = first_blood_time;
    }

    public String getLobby_type() {
        return lobby_type;
    }

    public void setLobby_type(String lobby_type) {
        this.lobby_type = lobby_type;
    }

    public String getHuman_players() {
        return human_players;
    }

    public void setHuman_players(String human_players) {
        this.human_players = human_players;
    }

    public String getLeagueid() {
        return leagueid;
    }

    public void setLeagueid(String leagueid) {
        this.leagueid = leagueid;
    }

    public String getPositive_votes() {
        return positive_votes;
    }

    public void setPositive_votes(String positive_votes) {
        this.positive_votes = positive_votes;
    }

    public String getNegative_votes() {
        return negative_votes;
    }

    public void setNegative_votes(String negative_votes) {
        this.negative_votes = negative_votes;
    }

    public String getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(String game_mode) {
        this.game_mode = game_mode;
    }

    public ArrayList<DetailPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<DetailPlayer> players) {
        this.players = players;
    }

    public boolean isRadiant_win() {
        return radiant_win;
    }

    public String getRadiant_guild_id() {
        return radiant_guild_id;
    }

    public void setRadiant_guild_id(String radiant_guild_id) {
        this.radiant_guild_id = radiant_guild_id;
    }

    public String getRadiant_guild_name() {
        return radiant_guild_name;
    }

    public void setRadiant_guild_name(String radiant_guild_name) {
        this.radiant_guild_name = radiant_guild_name;
    }

    public String getRadiant_guild_logo() {
        return radiant_guild_logo;
    }

    public void setRadiant_guild_logo(String radiant_guild_logo) {
        this.radiant_guild_logo = radiant_guild_logo;
    }

    public ArrayList<PicksBans> getPicks_bans() {
        return picks_bans;
    }

    public void setPicks_bans(ArrayList<PicksBans> picks_bans) {
        this.picks_bans = picks_bans;
    }

    public String getDire_guild_id() {
        return dire_guild_id;
    }

    public void setDire_guild_id(String dire_guild_id) {
        this.dire_guild_id = dire_guild_id;
    }

    public String getDire_guild_name() {
        return dire_guild_name;
    }

    public void setDire_guild_name(String dire_guild_name) {
        this.dire_guild_name = dire_guild_name;
    }

    public String getDire_guild_logo() {
        return dire_guild_logo;
    }

    public void setDire_guild_logo(String dire_guild_logo) {
        this.dire_guild_logo = dire_guild_logo;
    }

    public boolean isUser_win() {
        return user_win;
    }

    public void setUser_win(boolean user_win) {
        this.user_win = user_win;
    }

    public String getRadiant_name() {
        return radiant_name;
    }

    public void setRadiant_name(String radiant_name) {
        this.radiant_name = radiant_name;
    }

    public String getRadiant_logo() {
        return radiant_logo;
    }

    public void setRadiant_logo(String radiant_logo) {
        this.radiant_logo = radiant_logo;
    }

    public String getRadiant_team_complete() {
        return radiant_team_complete;
    }

    public void setRadiant_team_complete(String radiant_team_complete) {
        this.radiant_team_complete = radiant_team_complete;
    }

    public String getDire_name() {
        return dire_name;
    }

    public void setDire_name(String dire_name) {
        this.dire_name = dire_name;
    }

    public String getDire_logo() {
        return dire_logo;
    }

    public void setDire_logo(String dire_logo) {
        this.dire_logo = dire_logo;
    }

    public String getDire_team_complete() {
        return dire_team_complete;
    }

    public void setDire_team_complete(String dire_team_complete) {
        this.dire_team_complete = dire_team_complete;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(players);
        dest.writeInt(radiant_win ? 1 : 0);
        dest.writeString(duration);
        dest.writeString(start_time);
        dest.writeString(match_id);
        dest.writeString(match_seq_num);
        dest.writeString(tower_status_radiant);
        dest.writeString(tower_status_dire);
        dest.writeString(barracks_status_radiant);
        dest.writeString(barracks_status_dire);
        dest.writeString(cluster);
        dest.writeString(first_blood_time);
        dest.writeString(lobby_type);
        dest.writeString(human_players);
        dest.writeString(leagueid);
        dest.writeString(positive_votes);
        dest.writeString(negative_votes);
        dest.writeString(game_mode);

        dest.writeInt(user_win ? 1 : 0);

        dest.writeString(radiant_guild_id);
        dest.writeString(radiant_guild_name);
        dest.writeString(radiant_guild_logo);
        dest.writeString(dire_guild_id);
        dest.writeString(dire_guild_name);
        dest.writeString(dire_guild_logo);

        dest.writeTypedList(picks_bans);

        dest.writeString(radiant_name);
        dest.writeString(radiant_logo);
        dest.writeString(radiant_team_complete);
        dest.writeString(dire_name);
        dest.writeString(dire_logo);
        dest.writeString(dire_team_complete);

        dest.writeInt(favourite ? 1 : 0);
        dest.writeString(note);

    }

    public DetailMatch(Parcel pc) {
        pc.readTypedList(players, DetailPlayer.CREATOR);
        radiant_win = (pc.readInt() == 1);
        duration = pc.readString();
        start_time = pc.readString();
        match_id = pc.readString();
        match_seq_num = pc.readString();
        tower_status_radiant = pc.readString();
        tower_status_dire = pc.readString();
        barracks_status_radiant = pc.readString();
        barracks_status_dire = pc.readString();
        cluster = pc.readString();
        first_blood_time = pc.readString();
        lobby_type = pc.readString();
        human_players = pc.readString();
        leagueid = pc.readString();
        positive_votes = pc.readString();
        negative_votes = pc.readString();
        game_mode = pc.readString();

        user_win = (pc.readInt() == 1);

        radiant_guild_id = pc.readString();
        radiant_guild_name = pc.readString();
        radiant_guild_logo = pc.readString();
        dire_guild_id = pc.readString();
        dire_guild_name = pc.readString();
        dire_guild_logo = pc.readString();

        pc.readTypedList(picks_bans, PicksBans.CREATOR);

        radiant_name = pc.readString();
        radiant_logo = pc.readString();
        radiant_team_complete = pc.readString();
        dire_name = pc.readString();
        dire_logo = pc.readString();
        dire_team_complete = pc.readString();

        favourite = (pc.readInt() == 1);
        note = pc.readString();
    }

    public static final Parcelable.Creator<DetailMatch> CREATOR = new
            Parcelable.Creator<DetailMatch>() {
                public DetailMatch createFromParcel(Parcel pc) {
                    return new DetailMatch(pc);
                }

                public DetailMatch[] newArray(int size) {
                    return new DetailMatch[size];
                }
            };

}
