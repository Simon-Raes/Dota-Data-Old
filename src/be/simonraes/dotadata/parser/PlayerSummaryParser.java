package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import be.simonraes.dotadata.playersummary.PlayerSummaryContainer;
import be.simonraes.dotadata.util.Conversions;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 19/02/14.
 */
public class PlayerSummaryParser extends AsyncTask<String, Void, PlayerSummaryContainer> {

    private ASyncResponsePlayerSummary delegate;
    public interface ASyncResponsePlayerSummary {
        public void processFinish(PlayerSummaryContainer result);
    }

    public PlayerSummaryParser(ASyncResponsePlayerSummary delegate) {
        this.delegate = delegate;
    }

    @Override
    protected PlayerSummaryContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();

        String steamID = "";
        if (params.length > 0) {
            steamID = Conversions.dotaIDToCommunityID(params[0]);
        }

        PlayerSummaryContainer container = new PlayerSummaryContainer();

        try {
            container = mapper.readValue(new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=EB5773FAAF039592D9383FA104EEA55D&steamids=" + steamID), PlayerSummaryContainer.class);
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
    protected void onPostExecute(PlayerSummaryContainer playerSummaryContainer) {
        super.onPostExecute(playerSummaryContainer);
        delegate.processFinish(playerSummaryContainer);
    }
}
