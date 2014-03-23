package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.simonraes.dotadata.historymatch.HistoryContainer;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 30/01/14.
 */
public class HistoryMatchParser extends AsyncTask<String, Void, HistoryContainer> {

    private ASyncResponseHistory delegate;

    public HistoryMatchParser(ASyncResponseHistory delegate) {
        this.delegate = delegate;
    }

    @Override
    protected HistoryContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();
        HistoryContainer container = new HistoryContainer();

        String accountID = params[0];

        String startAtMatchID = "";
        if (params.length > 1) {
            if (params[1] != null && !params[1].equals("")) {
                startAtMatchID = params[1];
            }
        }


        String parseURL = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=" + accountID;

        if (!startAtMatchID.equals("")) {
            parseURL = parseURL + "&start_at_match_id=" + startAtMatchID;
        }

        try {

            container = mapper.readValue(new URL(parseURL), HistoryContainer.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return container;
    }

    @Override
    protected void onPostExecute(HistoryContainer container) {
        super.onPostExecute(container);
        System.out.println("done history parsing");
        delegate.processFinish(container);
    }
}
