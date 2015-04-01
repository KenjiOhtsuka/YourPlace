package com.improve_future.yourplace.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.improve_future.yourplace.Activity.DestinationDetailActivity;
import com.improve_future.yourplace.R;
import com.improve_future.yourplace.common.Constant;

import java.util.List;
import java.util.Locale;

/**
 * Created by k_110_000 on 11/3/2014.
 */
public class DestinationDetailListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equalsIgnoreCase("/test")) {
            final Intent destinationDetailActivity = new Intent(this, DestinationDetailActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, destinationDetailActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setTicker("test")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("TITLE")
                    .setContentText("CONTENT TEXT")
                    .addAction(R.drawable.card_frame, "NOTIFICATION", pendingIntent)
                    .build();
            //notification.icon = R.drawable.ic_launcher;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        for (DataEvent event : events) {
            DataItem dataItem = event.getDataItem();
            if (dataItem.getUri().getPath().equals(Constant.DESTINATION_PATH)) {
                DataMap dataMap = DataMap.fromByteArray(dataItem.getData());
                final Intent destinationDetailActivity = new Intent(this, DestinationDetailActivity.class);
                destinationDetailActivity.putExtra("dataMap", dataMap.toByteArray());
                final PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, destinationDetailActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                final String stationName;
                if (Locale.getDefault().getLanguage().equals(Locale.JAPANESE.toString())) {
                    stationName = dataMap.getDataMap("station").getString("name");
                } else {
                    final String codeName = dataMap.getDataMap("station").getString("codeName");
                    stationName = codeName.substring(codeName.lastIndexOf('.') + 1);
                }
                Notification notification = new Notification.Builder(this)
                        .setTicker(getString(R.string.notification_title))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.to_the_destination, stationName))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .addAction(R.drawable.ic_full_open_on_device, getString(R.string.check_detail),
                                pendingIntent)
                        .build();
                //notification.icon = R.drawable.ic_launcher;
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            }
        }
    }

}
