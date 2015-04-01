package com.improve_future.yourplace.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.improve_future.yourplace.Adapter.FacebookAdapter;
import com.improve_future.yourplace.Adapter.MetroAdapter;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Model.Station;
import com.improve_future.yourplace.common.Model.Spot;
import com.improve_future.yourplace.common.TokyoMetroAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DestinationDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DestinationDetailFragment extends FragmentBase {
    private Station station;
    private String stationName;
    private TextView stationNameTextView;
    private String lineName;
    private TextView lineNameTextView;
    private ImageView stationMarkImageView;
    private ArrayList<Spot> spots = new ArrayList<Spot>();
    //private ArrayAdapter<Spot> spotAdapter;
    private ArrayAdapter<String> spotAdapter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DestinationDetailFragment.
     */
    public static DestinationDetailFragment newInstance() {
        DestinationDetailFragment fragment = new DestinationDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public DestinationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.list_row_spot, R.id.spot_name);
                //getActivity(), android.R.layout.simple_list_item_1);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_destination_detail, container, false);
        if (Locale.getDefault().getLanguage().equals(
                Locale.JAPANESE.toString())) {
            ((TextView) view.findViewById(R.id.post_destination_sentence))
                    .setVisibility(View.VISIBLE);
        } else {
            ((TextView) view.findViewById(R.id                    .post_destination_sentence))
                    .setVisibility(View.GONE);
        }
        (lineNameTextView = (TextView)view.findViewById(R.id.line_name)).setText(lineName);
        (stationNameTextView = (TextView)view.findViewById(R.id.station_name)).setText(stationName);
        final ListView spotListView = (ListView)view.findViewById(R.id.around_spot_list_view);
        spotListView.setAdapter(spotAdapter);
        spotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Spot spot = spots.get(position);
                StringBuilder sb = new StringBuilder();
                //sb.append("geo:");
                sb.append("https://maps.google.co.jp/maps?ll=");
                sb.append(station.getLatitude()).append(',').append(station.getLongitude());
                sb.append("&q=");
                sb.append(spot.getLatitude()).append(',').append(spot.getLongitude());
//                sb.append("?q=");
//                sb.append(spot.getLatitude()).append(',').append(spot.getLongitude());
                sb.append("+(").append(spot.getName()).append(')');
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(sb.toString()));
                startActivity(intent);
            }
        });
        //((MapView)view.findViewById(R.id.map)).getMap()
        stationMarkImageView = (ImageView)view.findViewById(R.id.station_mark);
        (view.findViewById(R.id.guidance)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener extends FragmentBase.OnFragmentInteractionListener {
        public void sendToWear(
                final Station station, final ArrayList<Spot> spots);
        public void showInputForm();
    }

    public void setDestination(final String stationCodeName) {
        new StationFindThread(stationCodeName).start();
    }

    private class StationFindThread extends Thread implements Runnable {
        private String stationCodeName;
        private JSONObject stationInformationJSON;

        /**
         * Constructor
         * @param stationCodeName
         */
        public StationFindThread(final String stationCodeName) {
            this.stationCodeName = stationCodeName;
        }
        @Override
        public void run() {
            try {
                stationInformationJSON = MetroAdapter.getStationInformation(stationCodeName);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final android.content.res.Resources resources = getResources();
                            station = new Station(stationInformationJSON);
                            if (Locale.getDefault().getLanguage().equals(
                                    Locale.JAPANESE.toString())) {
                                stationName = station.getName();
                            } else {
                                stationName = station.getGlobalName();
                            }
                            int lineColor;
                            final String[] lineCodes = resources.getStringArray(R.array.line_codes);
                            final int lineIndex =
                                    Arrays.asList(lineCodes).indexOf(
                                            station.getLineCodeName().replace("Branch", ""));
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

                            final Bitmap bitmap = TokyoMetroAPI.generateStationMark(
                                    lineColor, station.getCode(),
                                    Typeface.createFromAsset(
                                            getActivity().getAssets(),
                                            "Roboto-Bold.ttf"), 400);

                            stationMarkImageView.setImageBitmap(bitmap);
                            stationNameTextView.setText(stationName);
                            lineNameTextView.setText(lineName);

                            new AroundSpotFindThread(
                                    station.getLatitude(), station.getLongitude()).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mListener.dismissProgressDialog();
                            mListener.showSimpleAlert(
                                    R.string.error, R.string.error_try_again);
                        }
                        final View view = getView();
                        (view.findViewById(R.id.guidance)).setVisibility(View.GONE);
                        (view.findViewById(R.id.layout)).setVisibility(View.VISIBLE);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.dismissProgressDialog();
                mListener.showSimpleAlert(
                        R.string.error, R.string.error_try_again);
            } catch (IOException e) {
                e.printStackTrace();
                mListener.dismissProgressDialog();
                mListener.showSimpleAlert(
                        R.string.error, R.string.error_try_again);
            }
        }
    }

    private class AroundSpotFindThread extends Thread implements Runnable {
        private double latitude;
        private double longitude;
        private JSONObject aroundSpotJson;

        public AroundSpotFindThread(final double latitude, final double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void run() {
            try {
                aroundSpotJson = FacebookAdapter.getAroundEstablishments(
                        latitude, longitude, 300, 25); //,
                        //String.valueOf(new char[] {(char)('a' + (char)(Math.random() * 26))}));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONArray spotJsonArray = aroundSpotJson.getJSONArray("data");

                            int count = spotJsonArray.length();
                            spots.clear();
                            spotAdapter.clear();
                            for (int i = 0; i < count; ++i) {
                                final JSONObject spotJson = spotJsonArray.getJSONObject(i);
                                final JSONArray categoryJsonArray =
                                        spotJson.getJSONArray("category_list");
                                final int category_count = categoryJsonArray.length();
                                Boolean passFlag = false;
                                for (int j = 0; j < category_count; ++j) {
                                    final String categoryName =
                                            categoryJsonArray.getJSONObject(j).getString("name");
                                    if (categoryName.equals("Train Station") ||
                                            categoryName.equals("Language School") ||
                                            categoryName.equals("Blood Bank") ||
                                            categoryName.equals("Region") ||
                                            categoryName.equals("Real Estate") ||
                                            categoryName.equals("Consulting/Business Services")) {
                                        passFlag = true;
                                        break;
                                    }
                                }
                                if (!passFlag) {
                                    final JSONObject locationJson =
                                            spotJson.getJSONObject("location");
                                    spots.add(new Spot(
                                            locationJson.getDouble("latitude"),
                                            locationJson.getDouble("longitude"),
                                            spotJson.getString("name")));
                                    spotAdapter.add(spotJson.getString("name"));
                                }
                            }
                            if (spotAdapter.getCount() > 0) {
                                ((ListView)getView().findViewById(R.id.around_spot_list_view)).setSelection(0);
                            }
                            if (PreferenceManager.getDefaultSharedPreferences(getActivity()).
                                    getBoolean("send_to_wear_on_decision", true)) {
                                mListener.sendToWear(station, spots);
                            }
                            mListener.dismissProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mListener.dismissProgressDialog();
                            mListener.showSimpleAlert(
                                    R.string.error, R.string.error_try_again);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(this.getClass().toString(), e.getStackTrace().toString());
                mListener.dismissProgressDialog();
                mListener.showSimpleAlert(
                        R.string.error, R.string.error_try_again);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v(this.getClass().toString(), e.getStackTrace().toString());
                mListener.dismissProgressDialog();
                mListener.showSimpleAlert(
                        R.string.error, R.string.error_try_again);
            }
        }
    }
}
