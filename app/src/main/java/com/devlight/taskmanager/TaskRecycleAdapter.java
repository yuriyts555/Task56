package com.devlight.taskmanager;


import com.daimajia.swipe.SwipeLayout;
import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.task.Task;
import com.devlight.utils.PrefsHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;

import android.widget.TextView;

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




	public class TaskViewHolder extends RealmViewHolder {
    	
        public TextView tvHeader, tvComment;

		public Button btnDelete, btnEdit, btnResetTimeBeg, btnResetTimeEnd;
        
        public com.daimajia.swipe.SwipeLayout mSwipe;
        public LinearLayout layoutTaskItem;
 
        public TaskViewHolder(View container) {
        	
            super(container);

            tvHeader = (TextView) container.findViewById(R.id.tvHeader);
            tvComment = (TextView) container.findViewById(R.id.tvComment);
            layoutTaskItem = (LinearLayout) container.findViewById(R.id.layoutTaskItem);

			btnDelete = (Button) container.findViewById(R.id.btnDelete);
			btnEdit = (Button) container.findViewById(R.id.btnEdit);
			btnResetTimeBeg = (Button) container.findViewById(R.id.btnResetTimeBeg);
			btnResetTimeEnd = (Button) container.findViewById(R.id.btnResetTimeEnd);

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

		//set background colors if task started, finished or didn't started
		if(mTask.getStartTime() ==-1 && mTask.getEndTime()==-1)
			holder.layoutTaskItem.setBackgroundColor(mColors[0]);

		if(mTask.getStartTime() !=-1 && mTask.getEndTime()==-1)
			holder.layoutTaskItem.setBackgroundColor(mColors[1]);

		if(mTask.getStartTime() !=-1 && mTask.getEndTime()!=-1)
			holder.layoutTaskItem.setBackgroundColor(mColors[2]);


		holder.btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Task mTask = realmResults.get(position);
				AlarmWork.cancelAlarm(mTask,mContext);

				Realm instance = Realm.getInstance(mContext.getRealmConfig());
				instance.beginTransaction();
				realmResults.deleteFromRealm(position);
				instance.commitTransaction();
				instance.close();

			}
		});



		holder.btnEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Task mTask = realmResults.get(position);

				Intent mIntent = new Intent(mContext, CreateTaskActivity.class);
				mIntent.putExtra(Task.KEY_ID, mTask.getID() );
				mIntent.putExtra(Task.KEY_AUTOFINISH, mTask.getAutoFinishMinutes() );
				mIntent.putExtra(Task.KEY_ENDTIME, mTask.getEndTime() );
				mIntent.putExtra(Task.KEY_STARTTIME, mTask.getStartTime() );
				mIntent.putExtra(Task.KEY_COMMENT, mTask.getComment() );
				mIntent.putExtra(Task.KEY_HEADER, mTask.getHeader() );

				mContext.startActivityForResult(mIntent, MainActivity.RESULT_CREATE_ACTIVITY);


			}
		});

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

	} //onBindRealmViewHolder


	public void ClearTasks() {

		//Realm.deleteRealm(mContext.getRealmConfig());
		Realm instance = Realm.getInstance(mContext.getRealmConfig());
		instance.beginTransaction();

		realmResults.deleteAllFromRealm();

		instance.commitTransaction();
		instance.close();
	}


    
	public int getDefautlSortMode() {
		
		return mDefaultSortMode;
	}



}
