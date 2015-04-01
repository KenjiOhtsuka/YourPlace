package com.improve_future.yourplace.Adapter;

import android.os.Build;

import com.improve_future.yourplace.common.Model.Station;
import com.improve_future.yourplace.common.StationComparator;
import com.improve_future.yourplace.Library.WebAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MetroAdapter {
    private static final String SEARCH_API_URL =
            "https://api.tokyometroapp.jp/api/v2/datapoints";
    private static final String CONSUMER_KEY =
            "YOUR_METRO_CONSUMER_KEY";

    public static JSONArray getDestinationCandidates(
            String fromStation, final int minFare, final int maxFare) throws JSONException, IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(SEARCH_API_URL);
        sb.append("?rdf:type=odpt:RailwayFare");
        sb.append("&acl:consumerKey=").append(CONSUMER_KEY);
        sb.append("&odpt:fromStation=").append(fromStation);
        String apiResult = WebAccessor.get(sb.toString());

        JSONArray apiJson = new JSONArray(apiResult);

        int count = apiJson.length();
        if (count > 0) {
            if (maxFare > 0 || minFare > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (int i = count - 1; i >= 0; --i) {
                        int fare = apiJson.getJSONObject(i).getInt("odpt:ticketFare");
                        if (fare > maxFare || fare < minFare) {
                            apiJson.remove(i);
                        }
                    }
                } else {
                    JSONArray resultJsonArray = new JSONArray();
                    for (int i = count - 1; i >= 0; --i) {
                        int fare = apiJson.getJSONObject(i).getInt("odpt:ticketFare");
                        if (fare <= maxFare && fare >= minFare) {
                            resultJsonArray.put(apiJson.getJSONObject(i));
                        }
                    }
                    apiJson = resultJsonArray;
                }
            }
        }
        return apiJson;
    }

    public static ArrayList<Station> getLineStations(String lineCodeName) throws JSONException,
            IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(SEARCH_API_URL);
        sb.append("?rdf:type=odpt:Station");
        sb.append("&acl:consumerKey=").append(CONSUMER_KEY);
        sb.append("&odpt:railway=").append(lineCodeName);
        String apiResult = WebAccessor.get(sb.toString());

        JSONArray apiJson = new JSONArray(apiResult);

        ArrayList<Station> stations = new ArrayList<Station>();
        int count = apiJson.length();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                stations.add(new Station(apiJson.getJSONObject(i)));
            }
            Collections.sort(stations, new StationComparator());
        }
        return stations;
    }

    /**
     *
     * @param stationCodeName
     * @return
     */
    public static JSONObject getStationInformation(String stationCodeName) throws
            IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(SEARCH_API_URL);
        sb.append("?rdf:type=odpt:Station");
        sb.append("&acl:consumerKey=");
        sb.append(CONSUMER_KEY);
        sb.append("&owl:sameAs=");
        sb.append(stationCodeName);
        String apiResult = WebAccessor.get(sb.toString());
        JSONArray apiJson = new JSONArray(apiResult);
        if (apiJson.length() == 0) {
            throw new JSONException("Can't get station information.");
        } else {
            return apiJson.getJSONObject(0);
        }
    }
}
