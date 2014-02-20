package be.simonraes.dotadata.util;

import android.content.Context;
import android.net.ConnectivityManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.*;

/**
 * Created by Simon on 30/01/14.
 */
public class InternetCheck {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static boolean serviceAvailable() {
        boolean isOnline = true;

        HttpGet httpRequest = null;

        try {
            httpRequest = new HttpGet(new URI("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = null;
        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpResponse response = httpclient.execute(httpRequest);
        } catch (IOException e) {
            isOnline = false;
        }
        return isOnline;
    }

    public static boolean siteAvailable() {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547", 80);
            socket.connect(socketAddress, 1000);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getErrorCode() {
        String errormessage = "Could not connect to the Dota 2 webservice";
        HttpGet httpRequest = null;

        try {
            httpRequest = new HttpGet(new URI("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=EB5773FAAF039592D9383FA104EEA55D&account_id=6133547"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpEntity httpEntity = null;
        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpResponse response = httpclient.execute(httpRequest);
            //http://developer.android.com/reference/org/apache/http/HttpStatus.html

            //500 INTERNAL SERVER ERROR
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                errormessage = "Dota 2 webservice currently unavailable";
            }
        } catch (IOException e) {
        }

        return errormessage;
    }
}
