package com.improve_future.yourplace.common.Model;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by k_110_000 on 10/14/2014.
 */
public class Station {
    private String lineCodeName;
    /** 駅コード */
    private String code;
    private String codeName;
    /** 駅名称(日本語) */
    private String name;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public String getLineCodeName() {
        return lineCodeName;
    }

    public String getCode() {
        return code;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getGlobalName() {
        return codeName.substring(codeName.lastIndexOf('.') + 1);
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }
    /**
     * コンストラクタ
     * @param lineCodeName      路線コード名
     * @param code              駅コード 例: H20
     * @param codeName          駅コード名 例: odpt.Station:TokyoMetro.Tozai.Nakano
     * @param name              駅名 例
     */
    public Station(final String lineCodeName, final String code,
                   final String codeName, final String name,
                   final double latitude, final double longitude) {
        this.lineCodeName = lineCodeName;
        this.code = code;
        this.codeName = codeName;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Station(final JSONObject stationJsonObject) throws JSONException {
        this(
                stationJsonObject.getString("odpt:railway"),
                stationJsonObject.getString("odpt:stationCode"),
                stationJsonObject.getString("owl:sameAs"),
                stationJsonObject.getString("dc:title"),
                stationJsonObject.getDouble("geo:lat"),
                stationJsonObject.getDouble("geo:long"));
    }
}
