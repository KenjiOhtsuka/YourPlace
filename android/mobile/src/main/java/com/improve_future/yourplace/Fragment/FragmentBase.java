package com.improve_future.yourplace.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by k_110_000 on 10/18/2014.
 */
public class FragmentBase extends Fragment {
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface OnFragmentInteractionListener {
        public void showSimpleAlert(final int titleId, final int messageId);
        public void showSimpleAlert(final int titleId, final int messageId, final int icon);
        public void showProgressDialog(
                final int titleId, final int messageId, final Boolean cancelable);
        public void showProgressDialog(
                final int titleId, final int messageId, final Boolean cancelable, final int icon);
        public void dismissProgressDialog();
    }
}
