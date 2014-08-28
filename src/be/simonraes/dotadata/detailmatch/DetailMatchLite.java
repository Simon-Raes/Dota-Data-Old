package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Combination of history matchdetails and playerinfo (of the active user), used for getting stats and creating the listviews.
 * Created by Simon on 23/03/14.
 */
public class DetailMatchLite implements Parcelable {

    //detailmatch values
    private boolean radiant_win;
    private String duration;
    private String start_time;
    private String match_id;
    private String lobby_type;
    private String game_mode;
    private boolean favourite; //option to add a match to your favourites
    private String note; //custom note you can attach to a match

    //player_in_match values
    private String account_id;
    private String player_slot;
    private String hero_id;
    private String item_0;
    private String item_1;
    private String item_2;
    private String item_3;
    private String item_4;
    private String item_5;
    private String kills;
    private String deaths;
    private String assists;
    private String leaver_status;
    private String gold;
    private String last_hits;
    private String denies;
    private String gold_per_min;
    private String xp_per_min;
    private String gold_spent;
    private String hero_damage;
    private String tower_damage;
    private String hero_healing;
    private String level;

    public DetailMatchLite() {

    }


    public boolean isRadiant_win() {
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

    public String getLobby_type() {
        return lobby_type;
    }

    public void setLobby_type(String lobby_type) {
        this.lobby_type = lobby_type;
    }

    public String getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(String game_mode) {
        this.game_mode = game_mode;
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

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getPlayer_slot() {
        return player_slot;
    }

    public void setPlayer_slot(String player_slot) {
        this.player_slot = player_slot;
    }

    public String getHero_id() {
        return hero_id;
    }

    public void setHero_id(String hero_id) {
        this.hero_id = hero_id;
    }

    public String getItem_0() {
        return item_0;
    }

    public void setItem_0(String item_0) {
        this.item_0 = item_0;
    }

    public String getItem_1() {
        return item_1;
    }

    public void setItem_1(String item_1) {
        this.item_1 = item_1;
    }

    public String getItem_2() {
        return item_2;
    }

    public void setItem_2(String item_2) {
        this.item_2 = item_2;
    }

    public String getItem_3() {
        return item_3;
    }

    public void setItem_3(String item_3) {
        this.item_3 = item_3;
    }

    public String getItem_4() {
        return item_4;
    }

    public void setItem_4(String item_4) {
        this.item_4 = item_4;
    }

    public String getItem_5() {
        return item_5;
    }

    public void setItem_5(String item_5) {
        this.item_5 = item_5;
    }

    public String getKills() {
        return kills;
    }

    public void setKills(String kills) {
        this.kills = kills;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public String getLeaver_status() {
        return leaver_status;
    }

    public void setLeaver_status(String leaver_status) {
        this.leaver_status = leaver_status;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getLast_hits() {
        return last_hits;
    }

    public void setLast_hits(String last_hits) {
        this.last_hits = last_hits;
    }

    public String getDenies() {
        return denies;
    }

    public void setDenies(String denies) {
        this.denies = denies;
    }

    public String getGold_per_min() {
        return gold_per_min;
    }

    public void setGold_per_min(String gold_per_min) {
        this.gold_per_min = gold_per_min;
    }

    public String getXp_per_min() {
        return xp_per_min;
    }

    public void setXp_per_min(String xp_per_min) {
        this.xp_per_min = xp_per_min;
    }

    public String getGold_spent() {
        return gold_spent;
    }

    public void setGold_spent(String gold_spent) {
        this.gold_spent = gold_spent;
    }

    public String getHero_damage() {
        return hero_damage;
    }

    public void setHero_damage(String hero_damage) {
        this.hero_damage = hero_damage;
    }

    public String getTower_damage() {
        return tower_damage;
    }

    public void setTower_damage(String tower_damage) {
        this.tower_damage = tower_damage;
    }

    public String getHero_healing() {
        return hero_healing;
    }

    public void setHero_healing(String hero_healing) {
        this.hero_healing = hero_healing;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(radiant_win ? 1 : 0);
        dest.writeString(duration);
        dest.writeString(start_time);
        dest.writeString(match_id);
        dest.writeString(lobby_type);
        dest.writeString(game_mode);
        dest.writeInt(favourite ? 1 : 0);
        dest.writeString(note);

        dest.writeString(account_id);
        dest.writeString(player_slot);
        dest.writeString(hero_id);
        dest.writeString(item_0);
        dest.writeString(item_1);
        dest.writeString(item_2);
        dest.writeString(item_3);
        dest.writeString(item_4);
        dest.writeString(item_5);
        dest.writeString(kills);
        dest.writeString(deaths);
        dest.writeString(assists);
        dest.writeString(leaver_status);
        dest.writeString(gold);
        dest.writeString(last_hits);
        dest.writeString(denies);
        dest.writeString(gold_per_min);
        dest.writeString(xp_per_min);
        dest.writeString(gold_spent);
        dest.writeString(hero_damage);
        dest.writeString(tower_damage);
        dest.writeString(hero_healing);
        dest.writeString(level);
    }

    public DetailMatchLite(Parcel pc) {
        radiant_win = (pc.readInt() == 1);
        duration = pc.readString();
        start_time = pc.readString();
        match_id = pc.readString();
        lobby_type = pc.readString();
        game_mode = pc.readString();
        favourite = (pc.readInt() == 1);
        note = pc.readString();

        account_id = pc.readString();
        player_slot = pc.readString();
        hero_id = pc.readString();
        item_0 = pc.readString();
        item_1 = pc.readString();
        item_2 = pc.readString();
        item_3 = pc.readString();
        item_4 = pc.readString();
        item_5 = pc.readString();
        kills = pc.readString();
        deaths = pc.readString();
        assists = pc.readString();
        leaver_status = pc.readString();
        gold = pc.readString();
        last_hits = pc.readString();
        denies = pc.readString();
        gold_per_min = pc.readString();
        xp_per_min = pc.readString();
        gold_spent = pc.readString();
        hero_damage = pc.readString();
        tower_damage = pc.readString();
        hero_healing = pc.readString();
        level = pc.readString();

    }

    public static final Parcelable.Creator<DetailMatchLite> CREATOR = new
            Parcelable.Creator<DetailMatchLite>() {
                public DetailMatchLite createFromParcel(Parcel pc) {
                    return new DetailMatchLite(pc);
                }

                public DetailMatchLite[] newArray(int size) {
                    return new DetailMatchLite[size];
                }
            };

}
