package be.simonraes.dotadata.parser;

import android.content.Context;
import android.os.AsyncTask;
import be.simonraes.dotadata.database.MatchesDataSource;
import be.simonraes.dotadata.database.MatchesService;
import be.simonraes.dotadata.delegates.ASyncResponseDetailList;
import be.simonraes.dotadata.detailmatch.DetailContainer;
import be.simonraes.dotadata.detailmatch.DetailMatch;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Simon on 14/02/14.
 * Same as DetailMatchParser, but accepts an array of matchIDs to parse and sends back an arraylist of DetailMatch objects
 */
public class DetailMatchesParser extends AsyncTask<String, Integer, ArrayList<DetailMatch>> {

    private ASyncResponseDetailList delegate;
    private MatchesService matchesService;
    private MatchesDataSource matchesDataSource;

    public DetailMatchesParser(ASyncResponseDetailList delegate, Context context, String userAccountId) {
        this.delegate = delegate;
        matchesService = new MatchesService(context, userAccountId);
        matchesDataSource = new MatchesDataSource(context, userAccountId);
    }

    @Override
    protected ArrayList<DetailMatch> doInBackground(String... params) {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<DetailMatch> detailMatches = new ArrayList<DetailMatch>();
        DetailContainer container;

        for (int i = 0; i < params.length; i++) {

            try {
                //only download match if it's not in the database
                if (!matchesDataSource.matchExists(params[i])) {
                    container = mapper.readValue(new URL("https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=EB5773FAAF039592D9383FA104EEA55D&match_id=" + params[i]), DetailContainer.class);

                    //save match here
                    matchesService.saveSingleMatch(container.detailMatch);
                }

                //detailMatches.add(container.getDetailMatch());
                publishProgress(i + 1);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return detailMatches;
    }

    @Override
    protected void onPostExecute(ArrayList<DetailMatch> container) {
        super.onPostExecute(container);
        delegate.processFinish(container);
    }

    protected void onProgressUpdate(Integer... progress) {
        delegate.processUpdate(progress);
    }

}