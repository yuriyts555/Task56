package com.devlight.taskmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.devlight.task.Task;

import io.realm.RealmResults;


public class AlarmWork {

    static public void setAllAlarms (Context mContext,RealmResults<Task> mTasks)
    {


        AlarmManager mAlarmManger = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        for (Task mTask: mTasks )
        {
            cancelAlarm(mAlarmManger,mTask,mContext);
            setAlarm(mAlarmManger, mTask, mContext);
        }



    }



    static public void cancelAllAlarms (Context mContext,RealmResults<Task> mTasks)
    {


        AlarmManager mAlarmManger = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        for (Task mTask: mTasks )
            cancelAlarm(mAlarmManger,mTask,mContext);



    }


    static   public void setAlarm(Task mTask,Context mContext)
    {

        AlarmManager mAlarmManger = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        setAlarm(mAlarmManger, mTask, mContext);

    }




    static   void cancelAlarm(AlarmManager alarmManager,Task mTask,Context mContext)
    {


        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setAction("com.devlight.taskmanager.ACTION");
        //intent.putExtra(Task.KEY_ID, taskID);

        int code = long2Int(mTask.getID());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, code, intent, 0);
        try {
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            Log.e("Task", "AlarmManager update was not canceled. code=" + Integer.toString(code));
        }

    }


    static   void cancelAlarm(Task mTask,Context mContext)
    {

        AlarmManager mAlarmManger = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setAction("com.devlight.taskmanager.ACTION");
        //intent.putExtra(Task.KEY_ID, taskID);

        int code = long2Int(mTask.getID());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, code, intent, 0);
        try {
            mAlarmManger.cancel(pendingIntent);
        } catch (Exception e) {
            Log.e("Task", "AlarmManager update was not canceled. code=" + Integer.toString(code));
        }

    }


    public static int long2Int(long id) {

        if (id<Integer.MAX_VALUE) return (int) id;
        else return (int) (id - (long)Integer.MAX_VALUE);

    }


    static  public void setAlarm(AlarmManager alarmManager,Task mTask,Context mContext)
    {

        if (mTask.getAutoFinishMinutes()<=0 || mTask.getStartTime() == -1L ) return; //not started or not set auto finish
        if (mTask.getEndTime() != -1L ) return; //already ended


        long mWakeTime =  mTask.getStartTime() + mTask.getAutoFinishMinutes()*1000L*60L;

        if (mTask.getResetTime() >0 ) mWakeTime = mTask.getResetTime() + mTask.getAutoFinishMinutes()*1000L*60L;

        if (mWakeTime <= System.currentTimeMillis() - 2000L) return; //It is already fired or will fire next 2 seconds

        Intent intent = new Intent(mContext.getApplicationContext(), AlarmReceiver.class);
        intent.setAction("com.devlight.taskmanager.ACTION");

        Long mID= mTask.getID();
        intent.putExtra(Task.KEY_ID,mID );


        int code = long2Int(mID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), code, intent, 0);



        alarmManager.set(AlarmManager.RTC_WAKEUP, mWakeTime, pendingIntent);


        Log.e(MainActivity.TAG,"!set alarm to "+Task.getTimeByString(mWakeTime)+" id= "+Long.toString(mID));

    }





}
