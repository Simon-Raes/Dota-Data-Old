package be.simonraes.dotadata.delegates;

import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.detailmatch.DetailMatch;

import java.util.ArrayList;

/**
 * Created by Simon on 30/01/14.
 */
public interface ASyncResponseDetailList {
    void processFinish(ArrayList<DetailMatch> result);

    void processUpdate(Integer[] progress);
}