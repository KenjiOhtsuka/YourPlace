package com.improve_future.yourplace.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Model.Spot;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpotFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpotFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SpotFragment extends Fragment {
    private static final String SPOT_NAME_KEY = "spot_name";
    private static final String SPOT_LATITUDE_KEY = "latitude";
    private static final String SPOT_LONGITUDE_KEY = "longitude";

    private String spot_name;
    private double spot_latitude;
    private double spot_longitude;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotFragment newInstance(
            final String spotName, final double spotLatitude, final double spotLongitude) {
        final SpotFragment fragment = new SpotFragment();
        final Bundle args = new Bundle();
        args.putString(SPOT_NAME_KEY, spotName);
        args.putDouble(SPOT_LATITUDE_KEY, spotLatitude);
        args.putDouble(SPOT_LONGITUDE_KEY, spotLongitude);
        fragment.setArguments(args);
        return fragment;
    }
    public SpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        final View view = inflater.inflate(R.layout.fragment_spot, container, false);
        ((TextView)view.findViewById(R.id.spot_name))
                .setText(arguments.getString(SPOT_NAME_KEY));
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
