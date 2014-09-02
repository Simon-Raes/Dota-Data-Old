package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import be.simonraes.dotadata.vanity.VanityContainer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Turns a Steam vanity URL into a Steam 64 ID.
 * Created by Simon on 20/02/14.
 */
public class VanityResolverParser extends AsyncTask<String, Void, VanityContainer> {

    private ASyncResponseVanity delegate;
    public interface ASyncResponseVanity {
        public void processFinish(VanityContainer result);
    }

    public VanityResolverParser(ASyncResponseVanity delegate) {
        this.delegate = delegate;
    }

    @Override
    protected VanityContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();
        String vanity = params[0];

        VanityContainer container = new VanityContainer();

        try {
            container = mapper.readValue(new URL("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=EB5773FAAF039592D9383FA104EEA55D&vanityurl=" + vanity), VanityContainer.class);
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
    protected void onPostExecute(VanityContainer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
