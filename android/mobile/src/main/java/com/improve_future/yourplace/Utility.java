package com.improve_future.yourplace;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by k_110_000 on 10/18/2014.
 */
public class Utility {
    /**
     * Show simple alert dialog with a button showing "OK"
     * @param context   Context on witch the alert dialog is to be shown
     * @param titleId   TitleId on the alert dialog
     * @param messageId Message shown on the alert dialog
     */
    public static void showSimpleAlert(
            final Context context, final int titleId, final int messageId) {
        showSimpleAlert(context, titleId, messageId, 0);
//        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        alert.setTitle(titleId);
//        alert.setMessage(messageId);
//        alert.setPositiveButton("OK", null);
//        alert.create().show();
    }

    public static void showSimpleAlert(
            final Context context, final int titleId, final int messageId, final int icon) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setIcon(icon);
        alert.setTitle(titleId);
        alert.setMessage(messageId);
        alert.setPositiveButton("OK", null);
        alert.create().show();
    }

    public static ProgressDialog createProgressDialog(
            final Context context, final String title, final String message,
            final Boolean cancelable) {
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setCancelable(cancelable);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        return progress;
    }
}
