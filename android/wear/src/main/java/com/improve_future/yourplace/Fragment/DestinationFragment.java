package com.improve_future.yourplace.Fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Model.Station;

import java.util.Arrays;
import java.util.Locale;

import static com.improve_future.yourplace.common.TokyoMetroAPI.generateStationMark;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DestinationFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String STATION_NAME_KEY = "station_name";
    private static final String LINE_NAME_KEY = "line_name";
    private static final String STATION_CODE_KEY = "station_code";
    private static final String LINE_COLOR_KEY = "line_color";
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DestinationFragment.
     */
    public static DestinationFragment newInstance(
            final String line_name, final String station_name,
            final String station_code, final int line_color) {
        final DestinationFragment fragment = new DestinationFragment();
        final Bundle args = new Bundle();
        args.putString(LINE_NAME_KEY, line_name);
        args.putString(STATION_NAME_KEY, station_name);
        args.putString(STATION_CODE_KEY, station_code);
        args.putInt(LINE_COLOR_KEY, line_color);
        fragment.setArguments(args);
        return fragment;
    }
    public DestinationFragment() {
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
        final View view = inflater.inflate(
                R.layout.fragment_destination, container, false);
        final TextView stationNameTextView =
                (TextView)view.findViewById(R.id.station_name);
        final TextView lineNameTextView =
                (TextView)view.findViewById(R.id.line_name);
        lineNameTextView.setText(arguments.getString(LINE_NAME_KEY));
        stationNameTextView.setText(arguments.getString(STATION_NAME_KEY));
        final Bitmap bitmap = generateStationMark(
                arguments.getInt(LINE_COLOR_KEY),
                arguments.getString(STATION_CODE_KEY),
                Typeface.createFromAsset(
                        getActivity().getAssets(),
                        "Roboto-Bold.ttf"), 400);
        ((ImageView)view.findViewById(R.id.station_mark)).setImageBitmap(bitmap);

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
