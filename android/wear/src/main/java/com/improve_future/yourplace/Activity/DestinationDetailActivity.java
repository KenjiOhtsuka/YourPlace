package com.improve_future.yourplace.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.improve_future.yourplace.DestinationDetailGridPagerAdapter;
import com.improve_future.yourplace.Fragment.DestinationFragment;
import com.improve_future.yourplace.Fragment.SpotActionFragment;
import com.improve_future.yourplace.Fragment.SpotFragment;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Model.Spot;
import com.improve_future.yourplace.common.Model.Station;

import java.util.ArrayList;
import java.util.Locale;

public class DestinationDetailActivity extends Activity implements
        DestinationFragment.OnFragmentInteractionListener,
        SpotFragment.OnFragmentInteractionListener,
        SpotActionFragment.OnFragmentInteractionListener {
    //private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        if (extras == null || extras.size() == 0) {
            setContentView(R.layout.activity_blank);
        } else {
            setContentView(R.layout.activity_destination);

            final GridViewPager gridViewPager = (GridViewPager) findViewById(R.id.grid_view_pager);
            gridViewPager.setAdapter(
                    new DestinationDetailGridPagerAdapter(this, getFragmentManager()));
        }
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//              //  .addConnectionCallbacks(this)
//                .build();
//        mGoogleApiClient.connect();
    }

    public Station getStation() {
        try {
            final DataMap dataMap = DataMap.fromByteArray(
                    getIntent().getExtras().getByteArray("dataMap"));
            final DataMap stationDataMap = dataMap.getDataMap("station");
            final Station station = new Station(
                    stationDataMap.getString("lineCodeName"),
                    stationDataMap.getString("code"),
                    stationDataMap.getString("codeName"),
                    stationDataMap.getString("name"),
                    stationDataMap.getDouble("latitude"),
                    stationDataMap.getDouble("longitude"));
            return station;
        } catch (Exception ex) {
            if (Locale.getDefault().getLanguage().equals(Locale.JAPANESE.toString())) {
                return new Station(
                        ".お使いの端末から", "???",
                        "", ".データを送信してください",
                        35.681382, 139.766084);
            } else {
                return new Station(
                        ".Please Send Data", "???", ".From Your Mobile Phone", "", 35.681382, 139.766084);
            }
        }
    }

    public ArrayList<Spot> getSpots() {
        try {
            final DataMap dataMap = DataMap.fromByteArray(
                    getIntent().getExtras().getByteArray("dataMap"));
            final ArrayList<Spot> spots = new ArrayList<Spot>();
            for (final DataMap spotDataMap : dataMap.getDataMapArrayList("spots")) {
                final Spot spot = new Spot(
                        spotDataMap.getDouble("latitude"),
                        spotDataMap.getDouble("longitude"),
                        spotDataMap.getString("name"));
                spots.add(spot);
            }
            return spots;
        } catch (Exception ex) {
            return new ArrayList<Spot>();
        }
    }
}
