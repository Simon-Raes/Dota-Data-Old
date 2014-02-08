package be.simonraes.dotadata.parser;

import android.os.AsyncTask;
import android.widget.ImageView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.teamlogo.TeamLogoContainer;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Accepts an imageview and image URL (teamlogo only) and sets image
 */
public class SteamRemoteStorageParser extends AsyncTask<String, Void, TeamLogoContainer> {

    private ImageView imageView;

    public SteamRemoteStorageParser(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected TeamLogoContainer doInBackground(String... params) {
        ObjectMapper mapper = new ObjectMapper();

        String ugcid = params[0];

        TeamLogoContainer container = new TeamLogoContainer();

        try {
            container = mapper.readValue(new URL("http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=EB5773FAAF039592D9383FA104EEA55D&appid=570&ugcid=" + ugcid), TeamLogoContainer.class);
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
    protected void onPostExecute(TeamLogoContainer teamLogoContainer) {
        super.onPostExecute(teamLogoContainer);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.hero_sb_loading)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        AnimateFirstDisplayListenerToo animateFirstListener = new AnimateFirstDisplayListenerToo();

        if (imageView.isShown()) {
            imageLoader.displayImage(teamLogoContainer.getTeamlogo().getUrl(), imageView, options, animateFirstListener);
        }
        //delegate.processFinish(teamLogoContainer);
    }
}
