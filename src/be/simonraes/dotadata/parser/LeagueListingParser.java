package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.interfaces.ASyncResponseLeagueListing;
import be.simonraes.dotadata.leaguelisting.LeagueListingContainer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 5/02/14.
 */
public class LeagueListingParser extends AsyncTask<String, Void, LeagueListingContainer> {

    private ASyncResponseLeagueListing delegate;

    public LeagueListingParser(ASyncResponseLeagueListing delegate) {
        this.delegate = delegate;
    }

    @Override
    protected LeagueListingContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();

        LeagueListingContainer container = new LeagueListingContainer();

        try {
            container = mapper.readValue(new URL("https://api.steampowered.com/IDOTA2Match_570/GetLeagueListing/v0001/?key=EB5773FAAF039592D9383FA104EEA55D"), LeagueListingContainer.class);
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
    protected void onPostExecute(LeagueListingContainer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
