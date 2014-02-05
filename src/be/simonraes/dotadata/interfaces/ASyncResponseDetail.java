package be.simonraes.dotadata.interfaces;

import be.simonraes.dotadata.detailmatch.DetailContainer;

/**
 * Created by Simon on 30/01/14.
 */
public interface ASyncResponseDetail {
    void processFinish(DetailContainer result);
}