package com.improve_future.yourplace.Library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by k_110_000 on 10/14/2014.
 */
public class WebAccessor {
    public static String get(String urlString) throws IOException, SocketTimeoutException {
        URL url = new URL(urlString);
        HttpURLConnection huc = (HttpURLConnection)url.openConnection();
        huc.setRequestMethod("GET");
        huc.setConnectTimeout(100000);
        huc.setReadTimeout(100000);
        huc.connect();
        InputStream is = huc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        isr.close();
        is.close();
        huc.disconnect();
        return sb.toString();
    }
}