package com.improve_future.yourplace.Adapter;

import com.improve_future.yourplace.Library.WebAccessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FacebookAdapter {
    private static final String APP_ID = "YOUR_APP_ID";
    private static final String APP_SECRET = "YOUR_APP_SECRET";

    /**
     * 周辺施設を取得する。
     * @param latitude      緯度
     * @param longitude     経度
     * @param rangeInMeter 検索範囲(メートル)
     * @param limit          最大出力件数
     * @return              周辺施設情報を含むJSON
     */
    public static JSONObject getAroundEstablishments(
            final double latitude,
            final double longitude,
            final int rangeInMeter,
            final int limit,
            final String query) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder("https://graph.facebook.com/search?type=place&");
        sb.append("center=").append(latitude).append(",").append(longitude);
        sb.append("&distance=").append(rangeInMeter);
        sb.append("&limit=").append(limit);
        sb.append("&access_token=").append(APP_ID).append("|").append(APP_SECRET);
        if (query != null) {
            sb.append("&q=").append(query);
        }

        String apiResult = WebAccessor.get(sb.toString());
        JSONObject resultJson = new JSONObject(apiResult);

        // ToDo: Api がエラーコードを返した場合に Exception を発生させる。

        return resultJson;
    }

    /**
     * 周辺施設を取得する。
     * @param latitude      緯度
     * @param longitude     経度
     * @param rangeInMeter 検索範囲(メートル)
     * @param limit          最大出力件数
     * @return              周辺施設情報を含むJSON
     */
    public static JSONObject getAroundEstablishments(
            final double latitude,
            final double longitude,
            final int rangeInMeter,
            final int limit) throws IOException, JSONException {
        return getAroundEstablishments(
                latitude, longitude, rangeInMeter, limit, null);
    }
}
