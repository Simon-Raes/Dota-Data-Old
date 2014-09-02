package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import be.simonraes.dotadata.friendslist.FriendsListContainer;
import be.simonraes.dotadata.vanity.VanityContainer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Takes a Steam64 ID and returns the Steam64 IDs of all friends of that user.
 * Created by Simon Raes on 2/09/2014.
 */
public class FriendsListParser extends AsyncTask<String, Void, FriendsListContainer> {

    private ASyncResponseFriendsList delegate;
    public interface ASyncResponseFriendsList {
        public void processFinish(FriendsListContainer result);
    }

    public FriendsListParser(ASyncResponseFriendsList delegate) {
        this.delegate = delegate;
    }

    @Override
    protected FriendsListContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();
        String vanity = params[0];

        FriendsListContainer container = new FriendsListContainer();

        try {
            container = mapper.readValue(new URL("http://api.steampowered.com/ISteamUser/GetFriendList/v0001/" +
                    "?key=EB5773FAAF039592D9383FA104EEA55D&relationship=friend&steamid=" + vanity), FriendsListContainer.class);
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
    protected void onPostExecute(FriendsListContainer result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
