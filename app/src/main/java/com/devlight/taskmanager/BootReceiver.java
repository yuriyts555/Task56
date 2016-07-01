package com.devlight.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devlight.task.Task;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {


            Realm realm = ((MApp)context.getApplicationContext()).realm;
            RealmResults<Task> results = realm.where(Task.class).findAll();

            AlarmWork.setAllAlarms(context,results);


        }


    }

}
