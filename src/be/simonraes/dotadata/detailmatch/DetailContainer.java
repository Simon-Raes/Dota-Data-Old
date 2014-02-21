package be.simonraes.dotadata.detailmatch;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Simon on 30/01/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailContainer implements Parcelable {

    @JsonProperty("result")
    public DetailMatch detailMatch;

    public DetailContainer(){

    }

    public DetailMatch getDetailMatch() {
        return detailMatch;
    }

    public void setDetailMatch(DetailMatch detailMatch) {
        this.detailMatch = detailMatch;
    }

    //parcelable code

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(detailMatch, 0);
    }

    public DetailContainer(Parcel pc) {
        detailMatch = pc.readParcelable(DetailMatch.class.getClassLoader());
    }

    public static final Parcelable.Creator<DetailContainer> CREATOR = new
            Parcelable.Creator<DetailContainer>() {
                public DetailContainer createFromParcel(Parcel pc) {
                    return new DetailContainer(pc);
                }

                public DetailContainer[] newArray(int size) {
                    return new DetailContainer[size];
                }
            };
}
