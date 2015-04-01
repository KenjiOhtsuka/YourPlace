package com.improve_future.yourplace.Library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by k_110_000 on 10/30/2014.
 */
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static File BUG_REPORT_FILE = null;
    static {
        String sdcard = Environment.getExternalStorageDirectory().getPath();
        String path = sdcard + File.separator + "bug.txt";
        BUG_REPORT_FILE = new File(path);
    }

    private static Context sContext;
    private static PackageInfo sPackInfo;
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    public MyUncaughtExceptionHandler(Context context) {
        sContext = context;
        try {
            //パッケージ情報
            sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread th, Throwable t) {
        try {
            saveState(t);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mDefaultUEH.uncaughtException(th, t);
    }

    private void saveState(Throwable e) throws FileNotFoundException {
        StackTraceElement[] stacks = e.getStackTrace();
        File file = BUG_REPORT_FILE;
        PrintWriter pw = null;
        pw = new PrintWriter(new FileOutputStream(file));
        StringBuilder sb = new StringBuilder();
        int len = stacks.length;
        pw.print("LocalizedMessage: ");
        pw.println(e.getLocalizedMessage());
        pw.print("Message: ");
        pw.println(e.getMessage());
        for (int i = 0; i < len; i++) {
            StackTraceElement stack = stacks[i];
            sb.setLength(0);
            sb.append(stack.getClassName()).append("#");
            sb.append(stack.getMethodName()).append(":");
            sb.append(stack.getLineNumber());
            pw.println(sb.toString());
            Log.e("Handled", sb.toString());
        }
        pw.close();
    }

    public static final void showBugReportDialogIfExist() {
        File file = BUG_REPORT_FILE;
        if (file != null & file.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(sContext);
            String yesString;
            String noString;
            if (Locale.getDefault().getLanguage().equals(Locale.JAPANESE.toString())) {
                builder.setTitle("バグレポート");
                builder.setMessage("バグ発生状況を開発者に送信しますか？");
                yesString = "はい";
                noString = "いいえ";
            } else {
                builder.setTitle("Report a Bug");
                builder.setMessage("Would you send a bug detail to the developer?");
                yesString = "Yes";
                noString = "No";
            }
            builder.setNegativeButton(noString, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    finish(dialog);
                }});
            builder.setPositiveButton(yesString, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    postBugReportInBackground(); // report bug
                    dialog.dismiss();
                    String toastMessage;
                    if (Locale.getDefault().getLanguage().equals(Locale.JAPANESE.toString())) {
                        toastMessage = "ご協力ありがとうございます！";
                    } else {
                        toastMessage = "Thank you for your cooperation!";
                    }
                    Toast.makeText(sContext, toastMessage, Toast.LENGTH_LONG).show();
                }});
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private static void postBugReportInBackground() {
        new Thread(new Runnable(){
            public void run() {
                postBugReport();
                File file = BUG_REPORT_FILE;
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }).start();
    }

    private static void postBugReport() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("device", Build.DEVICE));
        nvps.add(new BasicNameValuePair("model", Build.MODEL));
        nvps.add(new BasicNameValuePair("sdk_version", String.valueOf(Build.VERSION.SDK_INT)));
        nvps.add(new BasicNameValuePair("version_code", String.valueOf(sPackInfo.versionCode)));
        nvps.add(new BasicNameValuePair("stack_trace", getFileBody(BUG_REPORT_FILE)));
        try {
            HttpPost httpPost = new HttpPost("http://yourplace.improve-future.com/bug_report.php");
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFileBody(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static void finish(DialogInterface dialog) {
        File file = BUG_REPORT_FILE;
        if (file.exists()) {
            file.delete();
        }
        dialog.dismiss();
    }
}