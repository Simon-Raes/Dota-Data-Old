package be.simonraes.dotadata.util;

import android.content.Context;
import android.net.ConnectivityManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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

    public static boolean isConnectedToServer(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            connection.getInputStream();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public static boolean isConnectable(String url) {
        boolean connectable = true;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 5000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 6000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpGet);
        } catch (SocketTimeoutException stex) {
            connectable = false;
        } catch (ClientProtocolException cpex) {
            connectable = false;
        } catch (IOException ioex) {
            connectable = false;
        }
        return connectable;
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
