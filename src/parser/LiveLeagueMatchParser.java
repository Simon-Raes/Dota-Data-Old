package parser;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import historymatch.HistoryContainer;
import interfaces.ASyncResponseDetail;
import interfaces.ASyncResponseLiveLeague;
import liveleaguegame.LiveLeagueContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 4/02/14.
 */
public class LiveLeagueMatchParser extends AsyncTask<String, Void, LiveLeagueContainer> {

    private ASyncResponseLiveLeague delegate;

    public LiveLeagueMatchParser(ASyncResponseLiveLeague delegate){
        this.delegate = delegate;
    }

    @Override
    protected LiveLeagueContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();

        LiveLeagueContainer container = new LiveLeagueContainer();

        try {
            container = mapper.readValue(new URL("https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=EB5773FAAF039592D9383FA104EEA55D"), LiveLeagueContainer.class);
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
    protected void onPostExecute(LiveLeagueContainer liveLeagueContainer) {
        super.onPostExecute(liveLeagueContainer);
        delegate.processFinish(liveLeagueContainer);
    }
}
