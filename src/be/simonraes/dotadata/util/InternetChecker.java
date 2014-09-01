package be.simonraes.dotadata.util;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 20/02/14.
 */
public class InternetChecker extends AsyncTask<String, Void, Boolean> {

    private ASyncResponseInternet delegate;
    public interface ASyncResponseInternet {
        public void processFinish(Boolean result);
    }

    public InternetChecker(ASyncResponseInternet delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean webStatus = false;

        HttpURLConnection connection = null;
        try {
            URL u = new URL("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547");
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Connection", "Close");
            int code = connection.getResponseCode();

            if (code == 200 || code == 404 || code == 405) {
                webStatus = true;
            } else {
                webStatus = false;
            }

            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }


        return webStatus;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}
