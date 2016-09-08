package myapplication.flashcards;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Random;

public class mBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra("notification");

        Random random = new Random();
        int uid = random.nextInt(1000);

        notificationManager.notify(uid, notification);

    }


    public static String getNotification(){
        return "notification";
    }

}
