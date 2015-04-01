package com.improve_future.yourplace.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.MapView;
import com.improve_future.yourplace.Adapter.MetroAdapter;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Model.Station;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DecideDestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DecideDestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DecideDestinationFragment extends FragmentBase {
    private Spinner lineSpinner;
    private Spinner stationSpinner;
    private int lineSpinnerSelectedIndex = 0;
    private ArrayAdapter<String> stationAdapter;
    private ArrayList<Station> stations = new ArrayList<Station>();
    private MapView mapView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BlankFragment.
     */
    public static DecideDestinationFragment newInstance() {
        DecideDestinationFragment fragment = new DecideDestinationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public DecideDestinationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stationAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item);
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stationAdapter.add("");
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
        public void setDestination(final String stationCodeName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_decide_destination, container, false);
        Button decideDestinationButton = (Button)v.findViewById(R.id.decide_destination_button);
        lineSpinner = (Spinner)v.findViewById(R.id.line_spinner);
        stationSpinner = (Spinner)v.findViewById(R.id.station_spinner);

        decideDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stationPosition = stationSpinner.getSelectedItemPosition();
                if (stationPosition > 0) {
                    final int position = ((Spinner)getView().findViewById(R.id
                            .enjoying_way_spinner))
                            .getSelectedItemPosition();
                    final int minFare = getResources().getIntArray(
                            R.array.min_fares)[position];
                    final int maxFare = getResources().getIntArray(
                            R.array.max_fares)[position];
                    mListener.showProgressDialog(R.string.accessing,
                            R.string.just_a_moment, false, android.R.drawable.ic_popup_sync);
                    new DestinationSearchThread(
                            stations.get(stationPosition - 1).getCodeName(),
                            minFare, maxFare)
                            .start();
                } else {
                    mListener.showSimpleAlert(
                            R.string.please_wait, R.string.select_dept,
                            android.R.drawable.ic_dialog_alert);
                }
            }
        });
        lineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItemPosition = lineSpinner.getSelectedItemPosition();
                if (lineSpinnerSelectedIndex != selectedItemPosition) {
                    lineSpinnerSelectedIndex = selectedItemPosition;
                    String lineCodeName =
                            getResources().getStringArray(R.array.line_codes)[selectedItemPosition];
                    mListener.showProgressDialog(R.string.accessing, R.string.just_a_moment,
                            false, android.R.drawable.ic_popup_sync);
                    new LineStationSearchThread(lineCodeName).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        stationSpinner.setAdapter(stationAdapter);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class DestinationSearchThread extends Thread implements Runnable {
        private String fromStation;
        private int minFare;
        private int maxFare;
        private JSONArray targetStations;

        /**
         * コンストラクタ
         * @param fromStation
         * @param minFare
         * @param maxFare
         */
        public DestinationSearchThread(String fromStation, int minFare, int maxFare) {
            this.fromStation = fromStation;
            this.minFare = minFare;
            this.maxFare = maxFare;
        }

        @Override
        public void run() {
            try {
                int selectedItemPosition =
                    ((Spinner)getView().findViewById(R.id.enjoying_way_spinner))
                            .getSelectedItemPosition();
                targetStations = MetroAdapter.getDestinationCandidates(fromStation, minFare, maxFare);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int count = targetStations.length();
                        if (count > 0) {
                            int index = (int) (Math.random() * count);
                            try {
                                String targetStationCodeName =
                                        targetStations.getJSONObject(index).getString("odpt:toStation");
                                mListener.setDestination(targetStationCodeName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mListener.dismissProgressDialog();
                                mListener.showSimpleAlert(
                                        R.string.error, R.string.error_try_again);
                            }
                        } else {
                            mListener.dismissProgressDialog();
                            mListener.showSimpleAlert(
                                    R.string.sorry, R.string.dest_not_found);
                        }
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

    /**
     * Class to search stations along one line
     */
    private class LineStationSearchThread extends Thread implements Runnable {
        private String lineCodeName;
        /**
         * Constructor
         * @param lineCodeName
         */
        public LineStationSearchThread(final String lineCodeName) {
            this.lineCodeName = lineCodeName;
        }

        @Override
        public void run() {
            try {
                stations = MetroAdapter.getLineStations(lineCodeName);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int count = stations.size();
                        stationAdapter.clear();
                        stationAdapter.add("");
                        stationSpinner.setSelection(0);
                        if (Locale.getDefault().getLanguage().equals(Locale.JAPANESE.toString())) {
                            for (int i = 0; i < count; ++i) {
                                stationAdapter.add(stations.get(i).getName());
                            }
                        } else {
                            for (int i = 0; i < count; ++i) {
                                stationAdapter.add(stations.get(i).getGlobalName());
                            }
                        }
                        stationSpinner.setVisibility(View.VISIBLE);
                        mListener.dismissProgressDialog();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.dismissProgressDialog();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.showSimpleAlert(
                                R.string.error, R.string.error_try_again);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                mListener.dismissProgressDialog();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.showSimpleAlert(
                                R.string.error, R.string.error_try_again);
                    }
                });
            }
        }
    }
}
