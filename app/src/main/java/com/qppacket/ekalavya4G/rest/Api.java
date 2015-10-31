package com.qppacket.ekalavya4G.rest;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gorillalogic on 6/12/15.
 */
public class Api {
    private static InputStream mInStream;

    public static InputStream getData(String url) {
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.addRequestProperty("Content-Type", "text/xml");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                mInStream = new BufferedInputStream(connection.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mInStream = null;
        } catch (IOException e) {
            e.printStackTrace();
            mInStream = null;
        }
        return mInStream;
    }
}
