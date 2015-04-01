package com.improve_future.yourplace.common;

import com.improve_future.yourplace.common.Model.Station;

import java.util.Comparator;

/**
 * Created by k_110_000 on 10/14/2014.
 */
public class StationComparator implements Comparator<Station> {
    @Override
    public int compare(final Station lhs, final Station rhs) {
        return lhs.getCode().compareTo(rhs.getCode());
    }
}
