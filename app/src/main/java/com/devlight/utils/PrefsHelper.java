package com.devlight.utils;

import java.util.ArrayList;


import com.devlight.taskmanager.*;
import com.devlight.task.*;
import com.google.gson.Gson;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PrefsHelper {
	
	final static String TASKS_COUNT_KEY = "TASKSCOUNT"; 
	final static String TASK_KEY = "TASK"; 
	public final static String PREFS_NAME = "PREFS";
	
	public final static String KEY_COLOR0 = "COLOR0"; 
	public final static String KEY_COLOR1 = "COLOR1"; 
	public final static String KEY_COLOR2 = "COLOR2";
	public final static String DEFAULT_SORT_MODE = "DEFAULT_SORT_MODE"; 
	
	SharedPreferences mPrefs;
	Editor mEditor;

	RealmConfiguration realmConfig;
	Realm realm;
	
	public PrefsHelper(Context context)
	{
		mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);	
		mEditor = mPrefs.edit();

		realmConfig = new RealmConfiguration.Builder(context).build();
		Realm.setDefaultConfiguration(realmConfig);

		realm = Realm.getDefaultInstance();
	}
	
	
	public void save(String name,String value)
	{
		mEditor.putString(name, value);
		mEditor.commit();
	}


	public long getUniqueID() {

		long mID = mPrefs.getLong("UNIQUEID",1);

		mID++;
		if (mID ==0) mID =1;
		mEditor.putLong("UNIQUEID",mID);
		mEditor.commit();

		return mID;

	}
	
	
	public int getDefaultSortMode()
	{
		return mPrefs.getInt("DEFAULT_SORT_MODE", TaskRecycleAdapter.SORTMODE_A_Z);
	}
	
	public void save(String name,ArrayList<String> value)
	{
		for (int i=0;i<value.size();i++) 
		{
			mEditor.putString(name+Integer.toString(i), value.get(i));
		}
		mEditor.commit();
	}

			/*
		mEditor.putInt(TASKS_COUNT_KEY, tasks.size());

		for (int i=0;i<tasks.size();i++)
		{
			String mStr = tasks.get(i).getJSONString();
			mEditor.putString(TASK_KEY+Integer.toString(i), mStr);
		}
		mEditor.commit();
		*/

	/*
	public void saveTasks(final RealmList<Task> mTasks)
	{


       Log.e("Save tasks",Integer.toString(mTasks.size()));
		//mTasks.get(i).deleteFromRealm();


		Realm realm = Realm.getDefaultInstance();
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {

				realm.copyToRealmOrUpdate(mTasks);
			}
		});

	//realm.close();

	}*/


/*
	public void modifyTask(final Task task)
	{
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
               realm.copyToRealmOrUpdate(task);

			}
		});
	}


	public void deleteTask(final Task task)
	{
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {

				realm.
			}
		});
	}
	*/
			/*
		Gson gson = new Gson();
		int count=mPrefs.getInt(TASKS_COUNT_KEY, 0);

		for (int i=0;i<count;i++)
		{
			String mStr =mPrefs.getString(TASK_KEY+Integer.toString(i), "");
			//Task mTask = new Task(mStr);
			Task mTask = gson.fromJson(mStr, Task.class);
			tasks.add(mTask);

		}
		mEditor.commit();
		*/

	/*
	public void loadTasks(RealmList<Task> tasks)
	{



		Realm realm = Realm.getDefaultInstance();

		if (tasks.size()>0) tasks.clear();

		RealmResults<Task> mResults = realm.where(Task.class).findAll();

		Log.e("Load tasks",Integer.toString(mResults.size()));

		for (int i=0;i<mResults.size();i++) tasks.add(mResults.get(i));




		//mResults.deleteAllFromRealm();


		//realm.close();

	}*/


	public void saveDefaultSortMode(int mDefaultSortMode) {
		
		
		mEditor.putInt(DEFAULT_SORT_MODE, mDefaultSortMode);
		mEditor.commit();
		
	}


}
