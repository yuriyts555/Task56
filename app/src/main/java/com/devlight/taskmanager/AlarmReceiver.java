package com.devlight.taskmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.devlight.task.Task;

import io.realm.Realm;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {




        Bundle bundle = intent.getExtras();
        Long mID = bundle.getLong(Task.KEY_ID);


        Realm instance = Realm.getInstance(MainActivity.getRealmConfig(context));
        Task mTask = instance.where(Task.class).equalTo("id", mID).findFirst();

        if (mTask == null)
        {
            Log.e(MainActivity.TAG,"Can't find task with id="+Long.toString(mID));
            instance.close();
            return;
        }





        Intent mActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mContentIntent = PendingIntent.getActivity(context, 0, mActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(mTask.getHeader())
                .setContentTitle(mTask.getHeader())
                .setContentText(context.getText(R.string.task_auto_finished))
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(mContentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AlarmWork.long2Int(mTask.getID()), b.build());


        instance.close();

    }


}
