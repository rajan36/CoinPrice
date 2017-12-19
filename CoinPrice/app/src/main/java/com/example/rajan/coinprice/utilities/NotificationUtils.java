package com.example.rajan.coinprice.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.ContextCompat;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;


import com.example.rajan.coinprice.MainActivity;
import com.example.rajan.coinprice.R;

/**
 * Created by rajan on 18/12/17.
 */

public class NotificationUtils {

    private static final int PRICE_ALERT_PENDING_INTENT_ID = 4564;
    private static final int PRICE_ALERT_NOTIFICATION_ID = 56466;

    private static PendingIntent contentIntent(Context context) {

        Intent startMainActivity = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context, PRICE_ALERT_PENDING_INTENT_ID, startMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void dummyNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_notification_icon).setColor(ContextCompat.getColor(context, R.color.colorPrimary)).setContentTitle("Test Title").setContentText("Text body Text").setStyle(new NotificationCompat.BigTextStyle().bigText("Style Text")).setDefaults(Notification.DEFAULT_VIBRATE).setContentIntent(contentIntent(context)).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(PRICE_ALERT_NOTIFICATION_ID, notificationBuilder.build());

    }

    public static void simpleNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_notification_icon).setColor(ContextCompat.getColor(context, R.color.colorPrimary)).setContentTitle(title).setContentText(body).setStyle(new NotificationCompat.BigTextStyle().bigText("Style Text")).setDefaults(Notification.DEFAULT_VIBRATE).setContentIntent(contentIntent(context)).setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(PRICE_ALERT_NOTIFICATION_ID, notificationBuilder.build());

    }
}
