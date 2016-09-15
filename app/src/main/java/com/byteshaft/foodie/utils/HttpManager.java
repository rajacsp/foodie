package com.byteshaft.foodie.utils;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shahid on 19/04/2016.
 */
public class HttpManager {

    public static String getData(String uri, String userName, String password) {

        BufferedReader reader = null;

        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));


        HttpURLConnection connection;
        StringBuilder builder = new StringBuilder();

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Authorization" , loginBuilder.toString());
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return builder.toString();
    }
}
