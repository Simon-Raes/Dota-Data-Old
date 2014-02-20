package be.simonraes.dotadata.util;

import android.os.AsyncTask;
import be.simonraes.dotadata.delegates.ASyncResponseHistory;
import be.simonraes.dotadata.delegates.ASyncResponseInternet;

import java.io.IOException;
import java.net.*;

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

        boolean webStatus = false;

        HttpURLConnection connection = null;
        try {
            URL u = new URL("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547");
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
            if (code == 200) {
                webStatus = true;
            } else {
                webStatus = false;
            }

            System.out.println("correct code" + code);
            // You can determine on HTTP return code received. 200 is success.
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("malformed code");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ioexc code");

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
