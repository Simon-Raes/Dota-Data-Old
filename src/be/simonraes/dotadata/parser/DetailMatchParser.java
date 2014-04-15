package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.delegates.ASyncResponseDetail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 30/01/14.
 */
public class DetailMatchParser extends AsyncTask<String, Void, DetailContainer> {

    private ASyncResponseDetail delegate;

    public DetailMatchParser(ASyncResponseDetail delegate) {
        this.delegate = delegate;
    }

    @Override
    protected DetailContainer doInBackground(String... params) {


        ObjectMapper mapper = new ObjectMapper();
        String matchid = params[0];

        DetailContainer container = new DetailContainer();

        try {
            container = mapper.readValue(new URL("https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=EB5773FAAF039592D9383FA104EEA55D&match_id=" + matchid), DetailContainer.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return container;
    }

    @Override
    protected void onPostExecute(DetailContainer container) {
        super.onPostExecute(container);
        delegate.processFinish(container);
    }
}
