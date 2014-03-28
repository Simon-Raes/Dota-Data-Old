package be.simonraes.dotadata.detailmatch;

/**
 * Extra info added by the user (note, favourite,...)
 * Created by Simon Raes on 28/03/2014.
 */
public class DetailMatchExtras {

    private String match_id;
    private String note;
    private boolean favourite;

    public DetailMatchExtras() {
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
