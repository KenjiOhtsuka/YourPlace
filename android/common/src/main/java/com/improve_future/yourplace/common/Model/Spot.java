package com.improve_future.yourplace.common.Model;

/**
 * Created by k_110_000 on 10/18/2014.
 */
public class Spot {
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    private double latitude;
    private double longitude;
    private String name;

    /**
     *
     * @param latitude      緯度
     * @param longitude     経度
     * @param name          名前
     */
    public Spot(
            final double latitude,
            final double longitude,
            final String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }
}
