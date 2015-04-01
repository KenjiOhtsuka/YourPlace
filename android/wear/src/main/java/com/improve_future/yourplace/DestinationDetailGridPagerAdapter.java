package com.improve_future.yourplace;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.improve_future.yourplace.Activity.DestinationDetailActivity;
import com.improve_future.yourplace.Fragment.SpotActionFragment;
import com.improve_future.yourplace.Fragment.DestinationFragment;
import com.improve_future.yourplace.Fragment.SpotFragment;
import com.improve_future.yourplace.common.Model.Spot;
import com.improve_future.yourplace.common.Model.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by k_110_000 on 11/3/2014.
 */
public class DestinationDetailGridPagerAdapter extends FragmentGridPagerAdapter {
    private Station station;
    private ArrayList<Spot> spots;
    private Context mContext;

    public DestinationDetailGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        final DestinationDetailActivity activity = (DestinationDetailActivity)mContext;
        this.station = activity.getStation();
        this.spots = activity.getSpots();
    }

    @Override
    public int getRowCount() {
        return spots.size() + 1;
    }

    @Override
    public int getColumnCount(final int i) {
        switch (i) {
            case 0: return 1;
            default: return 2;
        }
    }

    @Override
    public Fragment getFragment(final int row, final int column) {
        switch (row) {
            case 0:
                switch(column) {
                    case 0:
                        String stationName;
                        if (Locale.getDefault().getLanguage().equals(
                                Locale.JAPANESE.toString())) {
                            stationName = station.getName();
                        } else {
                            stationName = station.getGlobalName();
                        }
                        int lineColor;
                        final Resources resources = mContext.getResources();
                        final String[] lineCodes = resources.getStringArray(R.array.line_codes);
                        final int lineIndex =
                                Arrays.asList(lineCodes).indexOf(station.getLineCodeName().replace("Branch", ""));
                        String lineName;
                        if (lineIndex < 0) {
                            lineName = station.getLineCodeName().substring(
                                    station.getLineCodeName().lastIndexOf('.') + 1);
                            lineColor = 0xff888888;
                        } else {
                            lineName = resources.
                                    getStringArray(R.array.line_names)[lineIndex];
                            lineColor = resources.
                                    getIntArray(R.array.line_colors)[lineIndex];
                        }

                        return DestinationFragment.newInstance(
                                lineName, stationName, station.getCode(), lineColor);
                    default:
                        return null;
                }
            default:
                final Spot spot = spots.get(row - 1);
                switch(column) {
                    case 0:
                        return SpotFragment.newInstance(
                                spot.getName(), spot.getLatitude(), spot.getLongitude());
                    default:
                        return SpotActionFragment.newInstance(
                                spot.getName(), spot.getLatitude(), spot.getLongitude(),
                                station.getLatitude(), station.getLongitude());
                }
        }
    }
}
