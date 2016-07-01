package com.devlight.taskmanager;


import com.daimajia.swipe.SwipeLayout;
import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.dialogs.FragmentOkCancelDialog;
import com.devlight.dialogs.FragmentResetTimeDialog;
import com.devlight.task.Task;
import com.devlight.task.TaskEvent;
import com.devlight.utils.PrefsHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;

import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;

public class TaskRecycleAdapter extends RealmBasedRecyclerViewAdapter<Task,TaskRecycleAdapter.TaskViewHolder>{
//	public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.TaskViewHolder>{




	public static final int SORTMODE_A_Z = 0;
	public static final int SORTMODE_Z_A = 1;
	public static final int SORTMODE_DATE_DEC = 2;
	public static final int SORTMODE_DATE_INC = 3;
	
	MainActivity mContext;
	LayoutInflater mLayoutInflater;

	RealmResults<Task> realmResults;
	
	int mColors[] = new int[3];
	
	int mDefaultSortMode = SORTMODE_A_Z;
	
	PrefsHelper mPrefsHelper;
	IconWorker mIconWorker;



	public void freeResources() {

		if (mIconWorker  != null) mIconWorker.free();
	}


	public class TaskViewHolder extends RealmViewHolder {
    	
        public TextView tvHeader, tvComment;

		public Button btnDelete, btnEdit, btnResetTime, btnStartFinish, btnPauseResume,btnRepeat;
				//btnResetTimeBeg, btnResetTimeEnd;
        
        public com.daimajia.swipe.SwipeLayout mSwipe;
        public LinearLayout layoutTaskItem;

		public ImageView ivIcon;
 
        public TaskViewHolder(View container) {
        	
            super(container);

            tvHeader = (TextView) container.findViewById(R.id.tvHeader);
            tvComment = (TextView) container.findViewById(R.id.tvComment);
            layoutTaskItem = (LinearLayout) container.findViewById(R.id.layoutTaskItem);

			btnDelete = (Button) container.findViewById(R.id.btnDelete);
			btnEdit = (Button) container.findViewById(R.id.btnEdit);

			btnStartFinish = (Button) container.findViewById(R.id.btnStartFinish);
			btnPauseResume = (Button) container.findViewById(R.id.btnPauseResume);

			btnResetTime = (Button) container.findViewById(R.id.btnResetTime);
			btnRepeat = (Button) container.findViewById(R.id.btnRepeat);

			ivIcon = (ImageView)  container.findViewById(R.id.ivIcon);

			mSwipe = (com.daimajia.swipe.SwipeLayout) container.findViewById(R.id.swipe);
        }


    }
 
 
    public TaskRecycleAdapter(MainActivity context,RealmResults<Task> realmResults) {

		super(context, realmResults, true, true);

		this.realmResults = realmResults;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		
		mPrefsHelper = new PrefsHelper(mContext);
		
		mDefaultSortMode = mPrefsHelper.getDefaultSortMode();
		
        updateColors();

		mIconWorker = new IconWorker(this,realmResults);
        
    }


	public void updateColors() {


		
		SharedPreferences mPrefs = mContext.getSharedPreferences(PrefsHelper.PREFS_NAME, Context.MODE_PRIVATE);
		mColors[0] = mPrefs.getInt(PrefsHelper.KEY_COLOR0, ContextCompat.getColor(mContext,R.color.green_bkg));
		mColors[1] = mPrefs.getInt(PrefsHelper.KEY_COLOR1, ContextCompat.getColor(mContext,R.color.yellow_bkg));
		mColors[2] = mPrefs.getInt(PrefsHelper.KEY_COLOR2, ContextCompat.getColor(mContext,R.color.red_bkg));
		
		notifyDataSetChanged();
		
		
	}
    
    
    public void changeSortMode(int mode)
	{
		mDefaultSortMode = mode;
		mPrefsHelper.saveDefaultSortMode(mDefaultSortMode);	
		
		sortItems();
	}
	
	
	public void sortItems()
	{
		

		
		switch (mDefaultSortMode) {
		case SORTMODE_A_Z:	
			//Collections.sort(realmResults, Task.mCompAZ);
			realmResults= realmResults.sort("mHeader", Sort.ASCENDING);
			break;
		case SORTMODE_Z_A:	
			//Collections.sort(realmResults, Task.mCompZA);
			realmResults = realmResults.sort("mHeader", Sort.DESCENDING);
			break;
		case SORTMODE_DATE_DEC:		
			//Collections.sort(realmResults, Task.mCompDateDec);
			realmResults = realmResults.sort("mStartTime", Sort.DESCENDING);
			break;
		case SORTMODE_DATE_INC:
			//Collections.sort(realmResults, Task.mCompDateInc);
			realmResults = realmResults.sort("mStartTime", Sort.ASCENDING);
			break;


		}		
		
		
		notifyDataSetChanged();
	}




	


	@Override
	public TaskViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {


		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_item, viewGroup, false);
		return new TaskViewHolder(itemView);
	}

	@Override
	public void onBindRealmViewHolder(final TaskViewHolder holder, final int position) {



		final Task mTask = realmResults.get(position);

		holder.tvHeader.setText(mTask.getHeader());
		holder.tvComment.setText(mTask.getStrTimePresentation() );


		//set icon

		holder.ivIcon.setImageBitmap(null);
		holder.ivIcon.setVisibility(View.GONE);

		if (mTask.getAvatarPath()!=null && !mIconWorker.isLoading())
		{
			Bitmap mIcon = mIconWorker.getBitmap(mTask.getID(),mTask.getAvatarPath());

				if (mIcon != null)
				{
					holder.ivIcon.setImageBitmap(mIcon);
					holder.ivIcon.setVisibility(View.VISIBLE);


				}


		}//if (mTask.getAvatarPath()!=null)



		//set background colors if task started, finished or didn't started
		if(mTask.getStartTime() ==-1 && mTask.getEndTime()==-1) { //not started

			holder.layoutTaskItem.setBackgroundColor(mColors[0]);

            holder.btnPauseResume.setVisibility(View.GONE);
			holder.btnStartFinish.setVisibility(View.VISIBLE);
			holder.btnRepeat.setVisibility(View.GONE);
			holder.btnResetTime.setVisibility(View.GONE);


			holder.btnStartFinish.setText(mContext.getString(R.string.start));

		}

		if(mTask.getStartTime() !=-1 && mTask.getEndTime()==-1) { //started, not finished

			holder.layoutTaskItem.setBackgroundColor(mColors[1]);

			holder.btnPauseResume.setVisibility(View.VISIBLE);
			holder.btnStartFinish.setVisibility(View.VISIBLE);
			holder.btnRepeat.setVisibility(View.GONE);
			holder.btnResetTime.setVisibility(View.VISIBLE);

			holder.btnStartFinish.setText(mContext.getString(R.string.finish));

			if (mTask.isPaused())
			{
				holder.btnPauseResume.setText(mContext.getString(R.string.resume));

				holder.btnStartFinish.setVisibility(View.GONE);
				holder.btnRepeat.setVisibility(View.GONE);
				holder.btnResetTime.setVisibility(View.GONE);
			}
			else holder.btnPauseResume.setText(mContext.getString(R.string.pause));
		}

		if(mTask.getStartTime() !=-1 && mTask.getEndTime()!=-1)   //finished
		{
			holder.layoutTaskItem.setBackgroundColor(mColors[2]);

			holder.btnPauseResume.setVisibility(View.GONE);
			holder.btnStartFinish.setVisibility(View.GONE);

			if (mTask.isPeriodic()) holder.btnRepeat.setVisibility(View.VISIBLE);
			else holder.btnRepeat.setVisibility(View.GONE);

			holder.btnResetTime.setVisibility(View.GONE);

		}


		holder.btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {



				//Task mTask = realmResults.get(position);
				long ID = mTask.getID();


				//Log.e("delete item"," id = "+Long.toString(ID)+" header="+mTask.getHeader());

				AlarmWork.cancelAlarm(mTask,mContext);

				Realm instance =((MApp)mContext.getApplicationContext()).realm;
				instance.beginTransaction();
				mTask.deleteFromRealm();
				instance.commitTransaction();
				//instance.close();


				TaskEvent.deleteAll(mContext,ID);
				mIconWorker.deleteBitmap(ID);

				notifyDataSetChanged();


				//Log.e("delete item"," after delete");

			}
		});



		holder.btnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//Task mTask = realmResults.get(position);

				Intent mIntent = new Intent(mContext, CreateTaskActivity.class);
				mIntent.putExtra(Task.KEY_ID, mTask.getID() );
				mIntent.putExtra(Task.KEY_AUTOFINISH, mTask.getAutoFinishMinutes() );
				mIntent.putExtra(Task.KEY_ENDTIME, mTask.getEndTime() );
				mIntent.putExtra(Task.KEY_STARTTIME, mTask.getStartTime() );
				mIntent.putExtra(Task.KEY_COMMENT, mTask.getComment() );
				mIntent.putExtra(Task.KEY_HEADER, mTask.getHeader() );

				mIntent.putExtra(Task.KEY_PERIODICHOURS, mTask.getPeriodicHours());
				mIntent.putExtra(Task.KEY_AVATARPATH, mTask.getAvatarPath() );
				mIntent.putExtra(Task.KEY_ISPERIODIC, mTask.isPeriodic());
				mIntent.putExtra(Task.KEY_ISPAUSED, mTask.isPaused() );




				mContext.startActivityForResult(mIntent, MainActivity.RESULT_CREATE_ACTIVITY);


			}
		});



		holder.btnStartFinish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

                 boolean isStarted =false;
				//AlarmWork.cancelAlarm(mTask,mContext);
				if (mTask.getStartTime() == -1L ) isStarted =true;

				Realm instance = ((MApp)mContext.getApplicationContext()).realm;
				instance.beginTransaction();

				if (isStarted )
				{
					mTask.setStartTime(System.currentTimeMillis());
					holder.btnStartFinish.setText(R.string.finish);
					AlarmWork.setAlarm(mTask,mContext);
				}
				else
				{
					AlarmWork.cancelAlarm(mTask,mContext);
					mTask.setEndTime(System.currentTimeMillis());
					holder.btnStartFinish.setVisibility(View.GONE);
				}

				instance.commitTransaction();
			//	instance.close();




				notifyItemChanged(position);


				if (isStarted) writeNewTaskState(mTask, TaskEvent.TASK_STARTED,System.currentTimeMillis());
				else writeNewTaskState(mTask, TaskEvent.TASK_FINISHEDMANUALLY,System.currentTimeMillis());


			}
		});


		holder.btnPauseResume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (mTask.getStartTime() == -1L ) return;

				//AlarmWork.cancelAlarm(mTask,mContext);

				Realm instance =((MApp)mContext.getApplicationContext()).realm;
				instance.beginTransaction();

				boolean mNewState = !mTask.isPaused();

				mTask.setIsPaused(mNewState);

				instance.commitTransaction();
				//instance.close();


				AlarmWork.setAlarm(mTask,mContext);


				if (mNewState)
				{
					holder.btnPauseResume.setText(R.string.resume);
					writeNewTaskState(mTask, TaskEvent.TASK_PAUSED,System.currentTimeMillis());
				}
				else
				{
					holder.btnPauseResume.setText(R.string.pause);
					writeNewTaskState(mTask, TaskEvent.TASK_RESUMED,System.currentTimeMillis());
				}

				notifyItemChanged(position);

			}
		});



		holder.btnResetTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (mTask.getStartTime() == -1L ) return;


				FragmentResetTimeDialog mDialog = FragmentResetTimeDialog.newInstance(mTask.getID());
				mDialog.show(mContext.getSupportFragmentManager(), "");

			}
		});


		holder.btnRepeat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				Realm instance = ((MApp)mContext.getApplicationContext()).realm;
				instance.beginTransaction();




				mTask.setIsPaused(false);
				mTask.setStartTime(System.currentTimeMillis());
				mTask.setEndTime(-1L);
				mTask.setResetTime(-1L);

				instance.commitTransaction();
			//	instance.close();

				notifyItemChanged(position);


				writeNewTaskState(mTask, TaskEvent.TASK_RESTARTED,System.currentTimeMillis());

			}
		});

		/*
		holder.btnResetTimeBeg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Task mTask=realmResults.get(position);

				Realm instance = Realm.getInstance(mContext.getRealmConfig());
				instance.beginTransaction();

				mTask.setStartTime(-1);
				mTask.setEndTime(-1);
				mTask.setResetTime(-1);


				instance.commitTransaction();
				instance.close();


				notifyItemChanged(position);

				AlarmWork.setAlarm(mTask,mContext);

			}
		});

		holder.btnResetTimeEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			

				Task mTask=realmResults.get(position);

				Realm instance = Realm.getInstance(mContext.getRealmConfig());
				instance.beginTransaction();

				mTask.setResetTime(System.currentTimeMillis());
				mTask.setEndTime(-1);

				instance.commitTransaction();
				instance.close();


				notifyItemChanged(position);


				AlarmWork.setAlarm(mTask,mContext);

			}
		});
		*/

/*
		holder.mSwipe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			

				if (holder.mSwipe.getOpenStatus() != SwipeLayout.Status.Close) return;

				Task mTask = realmResults.get(position);

				if (mTask.getStartTime()!=-1L && mTask.getEndTime()!=-1L) return; //All time filled, to correct it - edit item

				int curTimeType = FragmentDialogDateTime.SET_START_TIME;
				if (mTask.getStartTime()!=-1L) curTimeType = FragmentDialogDateTime.SET_END_TIME;

				//mDialogSetDateTime.showForTask(MainActivity.this, mTask, curTimeType, position);
				mContext.showDateDialog(mTask.getID(),mTask.getStartTime(),mTask.getEndTime(), curTimeType);


			}
		});
*/
	} //onBindRealmViewHolder



	@Override
	public void updateRealmResults(RealmResults<Task> queryResults) {
		super.updateRealmResults(queryResults);
	}


	public void onResetStartTime(long mID) {

		Realm instance =((MApp)mContext.getApplicationContext()).realm;
		Task mTask = instance.where(Task.class).equalTo("id", mID).findFirst();

		if (mTask == null)
		{
			Log.e(MainActivity.TAG,"Can't find task with id="+Long.toString(mID));
			//instance.close();
			return;
		}


		instance.beginTransaction();


		mTask.setStartTime(-1L);
		mTask.setEndTime(-1L);
		mTask.setResetTime(-1L);

		instance.commitTransaction();
	//	instance.close();

		//mRecycleAdapter.realmResults.set(mPosition, task);
		notifyDataSetChanged();

		AlarmWork.setAlarm(mTask,mContext);

		writeNewTaskState(mTask,TaskEvent.TASK_RESETSTARTTIME,System.currentTimeMillis());

	}


	public void onResetEndTime(long mID) {

		Realm instance = ((MApp)mContext.getApplicationContext()).realm;
		Task mTask = instance.where(Task.class).equalTo("id", mID).findFirst();

		if (mTask == null)
		{
			Log.e(MainActivity.TAG,"Can't find task with id="+Long.toString(mID));
		//	instance.close();
			return;
		}


		instance.beginTransaction();


		mTask.setEndTime(-1L);
		mTask.setResetTime(System.currentTimeMillis());

		instance.commitTransaction();
	//	instance.close();

		//mRecycleAdapter.realmResults.set(mPosition, task);
		notifyDataSetChanged();

		AlarmWork.setAlarm(mTask,mContext);

		writeNewTaskState(mTask,TaskEvent.TASK_RESETENDTIME,System.currentTimeMillis());

	}

	private void writeNewTaskState(Task mTask, int mState,long mTime) {

		TaskEvent.writeNewEvent(mContext,mTask.getID(),mState,mTime);
	}





	public void ClearTasks() {


		AlarmWork.cancelAllAlarms(mContext,realmResults);
		mIconWorker.free();

		//Realm.deleteRealm(mContext.getRealmConfig());
		Realm instance = ((MApp)mContext.getApplicationContext()).realm;
		instance.beginTransaction();
		instance.deleteAll();
		instance.commitTransaction();



		//instance.close();
	}


    
	public int getDefautlSortMode() {
		
		return mDefaultSortMode;
	}



}
