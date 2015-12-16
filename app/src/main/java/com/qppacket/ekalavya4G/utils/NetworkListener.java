package com.qppacket.ekalavya4G.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gorillalogic on 12/16/15.
 */
public class NetworkListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(currentNetworkInfo != null && currentNetworkInfo.isConnected() && isInternetActive()){
            context.sendBroadcast(new Intent(context.getPackageName() + ".MainActivity"));
        }
    }

    private boolean isInternetActive() {
        // Just to make synccall.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            HttpURLConnection url = (HttpURLConnection) new URL("http://www.google.com").openConnection();
            url.setRequestProperty(HTTP.USER_AGENT, "Test");
            url.setRequestProperty("Connection", HTTP.CONN_CLOSE);
            url.setConnectTimeout(1500);
            url.connect();
            return url.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(".EKalvya4G", "Error while ping google.com");
        }
        return false;
    }
}
