package com.improve_future.yourplace.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Constant;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpotActionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpotActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SpotActionFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String STATION_LATITUDE_KEY = "station_latitude";
    private static final String STATION_LONGITUDE_KEY = "station_longitude";
    private static final String SPOT_NAME_KEY = "spot_name";
    private static final String SPOT_LATITUDE_KEY = "spot_latitude";
    private static final String SPOT_LONGITUDE_KEY = "spot_longitude";

    private String spotName;
    private double spotLatitude;
    private double spotLongitude;
    private double stationLatitude;
    private double stationLongitude;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActionFragment.
     */
    public static SpotActionFragment newInstance(
            final String spotName, final double spotLatitude, final double spotLongitude,
            final double stationLatitude, final double stationLongitude) {
        SpotActionFragment fragment = new SpotActionFragment();
        Bundle args = new Bundle();
        args.putString(SPOT_NAME_KEY, spotName);
        args.putDouble(SPOT_LATITUDE_KEY, spotLatitude);
        args.putDouble(SPOT_LONGITUDE_KEY, spotLongitude);
        args.putDouble(STATION_LATITUDE_KEY, stationLatitude);
        args.putDouble(STATION_LONGITUDE_KEY, stationLongitude);
        fragment.setArguments(args);
        return fragment;
    }
    public SpotActionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final View view = inflater.inflate(R.layout.fragment_action, container, false);
        final CircledImageView actionCircle =
                (CircledImageView)view.findViewById(R.id.circleImageView);
        final TextView actionText = (TextView)view.findViewById(R.id.action_title);
        actionText.setText(R.string.open_the_map);
        spotName = arguments.getString(SPOT_NAME_KEY);
        spotLatitude = arguments.getDouble(SPOT_LATITUDE_KEY);
        spotLongitude = arguments.getDouble(SPOT_LONGITUDE_KEY);
        stationLatitude = arguments.getDouble(STATION_LATITUDE_KEY);
        stationLongitude = arguments.getDouble(STATION_LONGITUDE_KEY);
        actionCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder sb = new StringBuilder();
                sb.append("https://maps.google.co.jp/maps?ll=");
                sb.append(stationLatitude).append(',').append(stationLongitude);
                sb.append("&q=");
                sb.append(spotLatitude).append(',').append(spotLongitude);
//                sb.append("?q=");
//                sb.append(spot.getLatitude()).append(',').append(spot.getLongitude());
                sb.append("+(").append(spotName).append(')');

                new Thread(new Runnable() {
                    final byte[] sentData = sb.toString().getBytes();
                    public void run() {
                        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                                .addApi(Wearable.API)
                                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(Bundle bundle) {

                                    }

                                    @Override
                                    public void onConnectionSuspended(int i) {

                                    }
                                })
                                .build();
                        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                                10, TimeUnit.SECONDS);

                        Boolean hasSent = false;
                        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
                            NodeApi.GetConnectedNodesResult nodes =
                                    Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                            for (Node node : nodes.getNodes()) {
                                Wearable.MessageApi.sendMessage(
                                        googleApiClient, node.getId(), Constant.MAP_URL_PATH,
                                        sentData);
                                hasSent = true;
                            }
                        }
                        googleApiClient.disconnect();
                        if (hasSent) {
                            Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                    ConfirmationActivity.SUCCESS_ANIMATION);
                            intent.putExtra(
                                    ConfirmationActivity.EXTRA_MESSAGE, R.string.map_opened);
                            startActivity(intent);
                        }
                    }
                }).start();
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
    public interface OnFragmentInteractionListener {
    }

}
