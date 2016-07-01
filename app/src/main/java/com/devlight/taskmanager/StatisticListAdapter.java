package com.devlight.taskmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.devlight.task.Task;
import com.devlight.task.TaskEvent;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class StatisticListAdapter extends BaseExpandableListAdapter {


   // private String MONTHESNAMES[] = new String[12];

    private MainActivity mContext;

    private ArrayList<Month> mMonthes; // header titles
    private HashMap<Month, List<TaskTime>> _listDataChild;

   private  boolean isUpdating = false;

    public StatisticListAdapter(MainActivity context) {
        this.mContext = context;

    //    MONTHESNAMES[0] = mContext.getString(R.string.month0);

        mMonthes = new ArrayList<Month>();
        _listDataChild = new HashMap<Month, List<TaskTime>>();

       updateResults(null);
     //   notifyDataSetChanged();
    }


    public void updateResults(final MenuItem item)
    {
       new Thread(new Runnable() {
            public void run() {
                ///////////////////////////////////////////////////
                isUpdating = true;

                //clear all
                _listDataChild.clear();
                mMonthes.clear();



                RealmConfiguration realmConfiguration = ((MApp)mContext.getApplicationContext()).realmConfiguration;
                Realm mRealm = Realm.getInstance(realmConfiguration);
                //find all monthes
                RealmResults<TaskEvent> resultsFinished = mRealm
                        .where(TaskEvent.class)
                        .equalTo("mEvent", TaskEvent.TASK_AUTOFINISHED)
                        .or()
                        .equalTo("mEvent", TaskEvent.TASK_FINISHEDMANUALLY)
                        .findAll();



                for (TaskEvent mEvent:  resultsFinished    )
                {
                    Month mMonth = createOrFindMonth(mEvent.getEventTime());

                    TaskTime mTaskTime = new TaskTime();
                    mTaskTime.id = mEvent.getTaskID();
                    mTaskTime.header = getTaskHeader(mRealm,mTaskTime.id);

                    calculateTaskTime(mRealm,mTaskTime,mEvent);

                    if (_listDataChild.get(mMonth) ==null) _listDataChild.put(mMonth,new ArrayList<TaskTime>());
                    _listDataChild.get(mMonth).add(mTaskTime);

                    //  Log.e("Mtask time ",mTaskTime.header);
                }

                mRealm.close();


                SystemClock.sleep(1000);

                ///////////////////////////////////////////////////
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       notifyDataSetChanged();

                        if (item!=null)
                        {
                            item.getActionView().clearAnimation();
                            item.setActionView(null);
                        }
                    }
                });

                isUpdating = false;



                ///////////////////////////////////////////////////
              }
        }).start();
    }


    public boolean isUpdating()
    {
       return  isUpdating;
    }


    private void calculateTaskTime(Realm mRealm,TaskTime mTaskTime, TaskEvent mEvent)
    {
        RealmResults<TaskEvent> mEventsResults = mRealm
                .where(TaskEvent.class)
                .equalTo("mTaskID", mEvent.getTaskID())
                .lessThan("mEventTime", mEvent.getEventTime())
                .findAll();

        mEventsResults.load();
        mEventsResults =  mEventsResults.sort("mEventTime", Sort.DESCENDING);

        long taskExecutionTime = 0;
        long lastEventTime = mEvent.getEventTime();

        for (TaskEvent curEvent:    mEventsResults  )
        {

            if (curEvent.getEvent() == TaskEvent.TASK_PAUSED)
            {
                lastEventTime = curEvent.getEventTime();
                //Log.e("TASK_PAUSED ",Long.toString(curEvent.getTaskID()));
                continue;
            }


            if (curEvent.getEvent() == TaskEvent.TASK_RESUMED)
            {
                taskExecutionTime = taskExecutionTime +   (lastEventTime - curEvent.getEventTime());
               // Log.e("TASK_RESUMED ",Long.toString(curEvent.getTaskID()));
                continue;
            }
            //if (curEvent.getEvent() == TaskEvent.TASK_STARTED) break;

            if (curEvent.getEvent() == TaskEvent.TASK_STARTED || curEvent.getEvent() == TaskEvent.TASK_RESTARTED)
            {
                taskExecutionTime = taskExecutionTime +   (lastEventTime - curEvent.getEventTime());
                //Log.e("TASK_STARTED ",Long.toString(curEvent.getTaskID()));
                break;
            }
        }

        taskExecutionTime/= (1000L*60L); //get in minutes

        mTaskTime.hours= (int)(taskExecutionTime/60L);
        mTaskTime.minutes= (int)(taskExecutionTime - mTaskTime.hours*60L);

        mTaskTime.header = mTaskTime.header + "  "+getTwoSymbolsInt(mTaskTime.hours)+":"+getTwoSymbolsInt(mTaskTime.minutes);
    }

    private String getTwoSymbolsInt(int value) {

        if (value==0) return "00";
        if (value<10) return  "0"+Integer.toString(value);

        return  Integer.toString(value);
    }


    String getTaskHeader(Realm mRealm,long id)
    {

        Task mTask = mRealm.where(Task.class).equalTo("id", id).findFirst();

        if (mTask != null) return mTask.getHeader();


        Log.e("Statistic list","Can't find header for id="+Long.toString(id));

        return null;
    }

    Month createOrFindMonth(long time)
    {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);

        for ( Month mMonth: mMonthes     )
            if (mMonth.year == year && mMonth.month == month) return mMonth;



        Month mMonth = new Month();
        mMonth.year = year;
        mMonth.month = month;



        mMonth.name = mCalendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault())+" "+Integer.toString(year);

        mMonthes.add(mMonth);

        return  mMonth;
    }

    @Override
    public String getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this.mMonthes.get(groupPosition)).get(childPosititon).header;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);
       // Log.e("getChildView",childText);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this.mMonthes.get(groupPosition))
                .size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return this.mMonthes.get(groupPosition).name;
    }

    @Override
    public int getGroupCount() {
        return this.mMonthes.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle =  getGroup(groupPosition);
      //  Log.e("getGroupView",headerTitle);

        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    public class Month
    {
        public String name;
        public int year;
        public int month;
    }


    public class TaskTime
    {
        public String header;
        public long id;
        public int hours;
        public int minutes;
    }
}
