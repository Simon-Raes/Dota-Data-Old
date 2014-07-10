package be.simonraes.dotadata.util;

import android.os.AsyncTask;
import be.simonraes.dotadata.delegates.ASyncResponseInternet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simon on 20/02/14.
 */
public class InternetChecker extends AsyncTask<String, Void, Boolean> {

    private ASyncResponseInternet delegate;

    public InternetChecker(ASyncResponseInternet delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        System.out.println("checking internet");
        boolean webStatus = false;

        HttpURLConnection connection = null;
        try {
            URL u = new URL("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547");
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Connection", "Close");
            int code = connection.getResponseCode();

            //todo: can still return true when service is offline, resulting in an app crash

            if (code == 200 || code == 404 || code == 405) {
                webStatus = true;
            } else {
                webStatus = false;
            }

            System.out.println("correct code" + code);
            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            System.out.println("malformed code");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("io exc code");

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
