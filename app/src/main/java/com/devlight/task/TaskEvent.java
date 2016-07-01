package com.devlight.task;

import com.devlight.taskmanager.MApp;
import com.devlight.taskmanager.MainActivity;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Context;


public class TaskEvent extends RealmObject {

    @Ignore
    public final static int TASK_STARTED = 0;
    @Ignore
    public final static int TASK_RESETENDTIME = 1;
    @Ignore
    public final static int TASK_AUTOFINISHED = 2;
    @Ignore
    public final static int TASK_FINISHEDMANUALLY = 3;
    @Ignore
    public final static int TASK_RESETSTARTTIME = 4;
    @Ignore
    public final static int TASK_PAUSED = 5;
    @Ignore
    public final static int TASK_RESUMED = 6;
    @Ignore
    public final static int TASK_RESTARTED = 7;



    @PrimaryKey
    private long mEventTime;  //event time
    private long mTaskID; //Task ID
    private int mEvent; //Event: TASK_STARTED, TASK_RESETENDTIME, TASK_AUTOFINISHED,TASK_FINISHEDMANUALLY



    public int getEvent() {
        return mEvent;
    }

    public void setEvent(int mEvent) {
        this.mEvent = mEvent;
    }

    public long getEventTime() {
        return mEventTime;
    }

    public void setEventTime(long mEventTime) {
        this.mEventTime = mEventTime;
    }

    public long getTaskID() {
        return mTaskID;
    }

    public void setTaskID(long mTaskID) {
        this.mTaskID = mTaskID;
    }



    public static void writeNewEvent(android.content.Context mContext,long mID,int mEvent,long mTime)
    {
        Realm instance = ((MApp)mContext.getApplicationContext()).realm;
        writeNewEvent(instance,mID,mEvent,mTime);
       // instance.close();
    }


    public static void writeNewEvent(Realm realm, long mID, int mEventCode, long mTime)
    {
        TaskEvent mOldEvent = realm.where(TaskEvent.class).equalTo("mEventTime", mTime).findFirst();

        while( mOldEvent!=null)
        {
            mTime++; //mTime is primary key, increment it on one millisecond, if we have event in the same millisecond
            mOldEvent = realm.where(TaskEvent.class).equalTo("mEventTime", mTime).findFirst();
        }


        writeCheckedEvent(realm,mID,mEventCode,mTime);


    }


    private static void writeCheckedEvent(Realm realm, long mID, int mEventCode, long mTime)
    {
        realm.beginTransaction();

        TaskEvent mEvent = realm.createObject(TaskEvent.class);
        mEvent.setTaskID(mID);
        mEvent.setEventTime(mTime);
        mEvent.setEvent(mEventCode);

        realm.commitTransaction();
    }

    public static void deleteAll(MainActivity mContext, long mID) {

        Realm instance = ((MApp)mContext.getApplicationContext()).realm;


        RealmResults<Task> results = instance.where(Task.class).equalTo("id", mID).findAll();

        instance.beginTransaction();
        results.deleteAllFromRealm();
        instance.commitTransaction();

     //   instance.close();


    }
}
