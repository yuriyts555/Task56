package com.devlight.taskmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TabHost;

import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.dialogs.FragmentOkCancelDialog;
import com.devlight.dialogs.FragmentResetTimeDialog;
import com.devlight.task.Task;
import com.devlight.utils.PrefsHelper;

import java.util.Random;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;



public class MainActivity extends AppCompatActivity   implements FragmentOkCancelDialog.DialogYesNoListener,
		                                                         FragmentDialogDateTime.ITimeUpdater,
																	FragmentResetTimeDialog.DialogResetTimeListener{


	public static final String BROADCAST_UPDATE = "com.devlight.taskmanager.UPDATEMAINWINDOW";
	public static final String TAG = "TASKMANAGER";


	ExpandableListView mExpListView = null;
	StatisticListAdapter mStatisticListAdapter = null;

	Toolbar toolBar;

    boolean isExitSnackBarShowing= false; //is snackbar showing now (Press back again to exit)

   
    public final static int DIALOG_CLEAR =0; //ID Dialog "Are you really want to clear all items?"  
    

	
	final static int RESULT_CREATE_ACTIVITY =1;  //ID for Create/Edit activity
	final static int RESULT_PREFS_ACTIVITY =2;  //ID for Preferences activity
	
	TaskRecycleAdapter mRecycleAdapter;  //List adaptor
	RealmRecyclerView mRecyclerView;

	Animation mRotationAnim;

	com.devlight.utils.PrefsHelper mPrefsHelper;  //save list in sharedpreferences helper
	
	Random mRandom = new Random();

	UpdateReceiver mUpdateReceiver = null;
	Boolean mUpdateReceiverIsRegistered = false;

	@Override
	protected void onResume() {
		super.onResume();

		if (mUpdateReceiver == null) mUpdateReceiver=new UpdateReceiver();

		if (!mUpdateReceiverIsRegistered) {
			registerReceiver(mUpdateReceiver, new IntentFilter(BROADCAST_UPDATE));
			mUpdateReceiverIsRegistered = true;
		}
	}

	@Override
	protected void onPause() {

		if (mUpdateReceiverIsRegistered) {

			unregisterReceiver(mUpdateReceiver);
			mUpdateReceiverIsRegistered = false;
		}

		super.onPause();
	}

	@Override
	public void onResetStartTime(long mID) {

		mRecycleAdapter.onResetStartTime(mID);
	}

	@Override
	public void onResetEndTime(long mID) {

		mRecycleAdapter.onResetEndTime(mID);

	}

	public class UpdateReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {

			if (mRecycleAdapter!=null) mRecycleAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onBackPressed() {
	  tryExit();
	
	}
	
	void tryExit()
	{

		if (isExitSnackBarShowing) //Second back pressed
			finish();
		else { //First tie pressed, show snakbar
			


			android.support.design.widget.CoordinatorLayout mainLayout = (android.support.design.widget.CoordinatorLayout) findViewById(
					R.id.mainLayout);
			Snackbar snackbar = Snackbar.make(mainLayout, R.string.confirmexit, Snackbar.LENGTH_SHORT);
			snackbar.show();
			isExitSnackBarShowing = true;



			snackbar.setCallback(new Snackbar.Callback() {

				@Override
				public void onDismissed(Snackbar snackbar, int event) {

					isExitSnackBarShowing = false;
				}

				@Override
				public void onShown(Snackbar snackbar) {
					
				}
			});

		}
	}



	
	
    //Save items list








   

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_activity);


		//load animation
		mRotationAnim = AnimationUtils.loadAnimation(this, R.anim.roation);
		mRotationAnim.setRepeatCount(Animation.INFINITE);


		//TABHOST
		setContentView(R.layout.main_activity);

		TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
		tabHost.setup();



		TabHost.TabSpec spec = tabHost.newTabSpec(getString(R.string.tab1));
		spec.setContent(R.id.tabContent);
		spec.setIndicator(getString(R.string.tab1));
		tabHost.addTab(spec);


		spec = tabHost.newTabSpec(getString(R.string.tab2));
		spec.setContent(R.id.tabStatistic);
		spec.setIndicator(getString(R.string.tab2));
		tabHost.addTab(spec);

		tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.btn_selector);
		tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.btn_selector);
        
        toolBar = (Toolbar) findViewById(R.id.toolbar); //Use toolbar
        setSupportActionBar(toolBar);  
        //getSupportActionBar().setTitle(R.string.app_name);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
        

        
        mRecyclerView = (RealmRecyclerView) findViewById(R.id.recycler_view);



        mPrefsHelper = new PrefsHelper(this);


		//resetRealm();

		RealmResults<Task> results = ((MApp)getApplicationContext()).realm.where(Task.class).findAll();

		mRecycleAdapter = new TaskRecycleAdapter(this,results);


		mRecyclerView.setAdapter(mRecycleAdapter);
		mRecyclerView.disableShowLoadMore();


		AlarmWork.setAllAlarms(this,results);





		//ViewCompat.setNestedScrollingEnabled(mRecyclerView,true);
        
        
        

		
		
		//Add new task button
		FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
		//fabAdd.setBackgroundColor(getResources().getColor(R.color.orange));
		fabAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddNewTask();
				
			}
		});
		
		


		//Expandable list view
		mExpListView = (ExpandableListView) findViewById(R.id.lvExpListView);

		mStatisticListAdapter = new StatisticListAdapter(this);

		mExpListView.setAdapter(mStatisticListAdapter);

    }

	@Override
	protected void onDestroy() {

		if (mRecycleAdapter!=null) mRecycleAdapter.freeResources();
		super.onDestroy();
	}


	//Change item start/end date/time
	void showDateDialog(long mID,long timeStart, long timeEnd,int timeType) {

	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogDateTime");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    FragmentDialogDateTime newFragment = FragmentDialogDateTime.newInstance(mID,timeStart,timeEnd,timeType);
	    newFragment.show(ft, "dialogDateTime");
	}
    
    
    void AddNewTask()
    {
		Intent mIntent = new Intent(MainActivity.this, CreateTaskActivity.class);
		mIntent.putExtra(Task.KEY_ID,0);
		mIntent.putExtra(Task.KEY_STARTTIME,-1L);
		mIntent.putExtra(Task.KEY_ENDTIME,-1L);
		mIntent.putExtra(Task.KEY_AUTOFINISH,mPrefsHelper.getDefaultAutoFinishTime());
		startActivityForResult(mIntent, RESULT_CREATE_ACTIVITY);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //After edit/create activity
    	if (requestCode == RESULT_CREATE_ACTIVITY) {
        	
        	//Button save was pressed, we got result
            if(resultCode == Activity.RESULT_OK){



				long mID = data.getLongExtra(Task.KEY_ID, 0);
				long mAutoFinishAfterMinutes  = data.getLongExtra(Task.KEY_AUTOFINISH, -1);
				String mHeader = data.getStringExtra(Task.KEY_HEADER);
				String mComment = data.getStringExtra(Task.KEY_COMMENT);
				long mStartTime  = data.getLongExtra(Task.KEY_STARTTIME, -1);
				long mEndTime  = data.getLongExtra(Task.KEY_ENDTIME, -1);

				long mPeriodicHours  = data.getLongExtra(Task.KEY_PERIODICHOURS, -1);
				String mAvatarPath = data.getStringExtra(Task.KEY_AVATARPATH);
				boolean mIsPeriodic = data.getBooleanExtra(Task.KEY_ISPERIODIC,false);
				boolean mIsPaused = data.getBooleanExtra(Task.KEY_ISPAUSED,false);
                
                if (mID ==0 )
				{
					mID = mPrefsHelper.getUniqueID();

					Realm instance = ((MApp)getApplicationContext()).realm;
					instance.beginTransaction();

					Task mTask = instance.createObject(Task.class);
					mTask.setAutoFinishMinutes(mAutoFinishAfterMinutes);
					mTask.setHeader(mHeader);
					mTask.setComment(mComment);
					mTask.setID(mID);
					mTask.setStartTime(mStartTime);
					mTask.setEndTime(mEndTime);

					mTask.setPeriodicHours(mPeriodicHours);
					mTask.setAvatarPath(mAvatarPath);
					mTask.setIsPaused(mIsPaused);
					mTask.setIsPeriodic(mIsPeriodic);

					instance.commitTransaction();
					//instance.close();



					mRecycleAdapter.notifyItemRangeChanged(0, mRecycleAdapter.realmResults.size());


					AlarmWork.setAlarm(mTask,this);

				}
                else
				{

					Realm instance = ((MApp)getApplicationContext()).realm;
					Task mTask = instance.where(Task.class).equalTo("id", mID).findFirst();

					if (mTask == null)
					{
						Log.e(MainActivity.TAG,"Can't find task with id="+Long.toString(mID));
						//instance.close();
						return;
					}


					instance.beginTransaction();

					mTask.setAutoFinishMinutes(mAutoFinishAfterMinutes);
					mTask.setHeader(mHeader);
					mTask.setComment(mComment);
					mTask.setID(mID);
					mTask.setStartTime(mStartTime);
					mTask.setEndTime(mEndTime);

					mTask.setPeriodicHours(mPeriodicHours);
					mTask.setAvatarPath(mAvatarPath);
					mTask.setIsPaused(mIsPaused);
					mTask.setIsPeriodic(mIsPeriodic);

					instance.commitTransaction();
					//instance.close();



					//mRecycleAdapter.realmResults.set(mPosition, task);
					mRecycleAdapter.notifyDataSetChanged();


					AlarmWork.setAlarm(mTask,this);
				}
                

                
            }
            

            //Button exit was pressed, no result
            //if (resultCode == Activity.RESULT_CANCELED) {
           
          //  }
        }
        
        
        //After preferences activity
        if (requestCode == RESULT_PREFS_ACTIVITY && resultCode == Activity.RESULT_OK) {
        	mRecycleAdapter.updateColors();
        	//mRecyclerView.invalidateViews();
        	mRecycleAdapter.notifyDataSetChanged();
        }
    }//onActivityResult
    
    
    




	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
       
        //Check default sort mode in menu
        int mDefaultSortMode = mRecycleAdapter.getDefautlSortMode();
        
        MenuItem mItem;
        
        switch (mDefaultSortMode) {
        
		case TaskRecycleAdapter.SORTMODE_A_Z:
			mItem = menu.findItem(R.id.action_a_z) ;
			mItem.setChecked(true);
			mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_A_Z);
			break;
			
		case TaskRecycleAdapter.SORTMODE_Z_A:
			mItem = menu.findItem(R.id.action_z_a) ;
			mItem.setChecked(true);
			mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_Z_A);
			break;
			
		case TaskRecycleAdapter.SORTMODE_DATE_DEC:
			mItem = menu.findItem(R.id.action_date_dec) ;
			mItem.setChecked(true);
			mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_DATE_DEC);
			break;
			
		case TaskRecycleAdapter.SORTMODE_DATE_INC:
			mItem = menu.findItem(R.id.action_date_inc) ;
			mItem.setChecked(true);
			mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_DATE_INC);
			break;

	
		}
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


		if (id == R.id.action_update_stats) {

			if (mStatisticListAdapter == null) return true;
			if (mStatisticListAdapter.isUpdating()) return true;

			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ImageView iv = (ImageView) inflater.inflate(R.layout.rotate_btn, null);

			Animation rotation = AnimationUtils.loadAnimation(this, R.anim.roation);
			rotation.setRepeatCount(Animation.INFINITE);
			iv.startAnimation(rotation);

			item.setActionView(iv);

			mStatisticListAdapter.updateResults(item);

		}
      
        
        if (id == android.R.id.home) {
      
        	tryExit();
           return true;
        }


        if (id == R.id.action_fill) { //Fill list
        	
        	addTasksToListView();        	
           return true;
        }
        
        if (id == R.id.action_add) { //Add new task
        	
        	AddNewTask();    	
           return true;
        }
        
        if (id == R.id.action_clear) {
        	
        	//new CutomFragmentDialog(DIALOG_CLEAR,getString(R.string.confirm_clear)).show(getSupportFragmentManager(), "");
        	FragmentOkCancelDialog mDialog = FragmentOkCancelDialog.newInstance(DIALOG_CLEAR,getString(R.string.confirm_clear));
        	mDialog.show(getSupportFragmentManager(), "");
            return true;
         }
        
        if (id == R.id.action_a_z) { //Sort A-z
        	
        	item.setChecked(true);
        
        	mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_A_Z);
            return true;
         }
        
        if (id == R.id.action_z_a) { //Sort Z-a
        	
        	item.setChecked(true);
        	mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_Z_A);
            return true;
         }
        
        if (id == R.id.action_date_dec) { //Sort date decrement
        	
        	item.setChecked(true);
        	mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_DATE_DEC);
            return true;
         }
        
        if (id == R.id.action_date_inc) { //Sort date increment
        	
        	item.setChecked(true);
        	mRecycleAdapter.changeSortMode(TaskRecycleAdapter.SORTMODE_DATE_INC);
            return true;
         }
        
        if (id == R.id.action_prefs) { //Open preferences
        	
			Intent mIntent = new Intent(MainActivity.this, PrefsActivity.class);
			startActivityForResult(mIntent, RESULT_PREFS_ACTIVITY);
            return true;
         }
        
        
        if (id == R.id.action_exit) {  //Exit
        	
        	finish();
            return true;
         }



        return super.onOptionsItemSelected(item);
    }
    
    
    




	 void addRandomTask()
	{
		String mHeader = ""+mRandom.nextInt(10000);
		String mComment = "Body"+mRandom.nextInt(10000);

		long mID = mPrefsHelper.getUniqueID();

		Realm instance = ((MApp)getApplicationContext()).realm;
		instance.beginTransaction();

		Task mTask = instance.createObject(Task.class);
		mTask.setAutoFinishMinutes(0);
		mTask.setHeader(mHeader);
		mTask.setComment(mComment);
		mTask.setID(mID);
		mTask.setStartTime(-1);
		mTask.setEndTime(-1);

		instance.commitTransaction();
	//	instance.close();
	}






    void addTasksToListView() //Fill list, with item count three time much more, than items visible
    {

		int curSize = mRecycleAdapter.realmResults.size();

		android.support.design.widget.CoordinatorLayout mainLayout = (android.support.design.widget.CoordinatorLayout) findViewById(
				R.id.mainLayout);

		int listViewHeight = mainLayout.getHeight() - toolBar.getHeight();


		int elementHeight = (int) getResources().getDimension(R.dimen.swipe_height);

		int mElementsNeedCount = (listViewHeight/elementHeight)*3 - curSize;

		for (int i=0; i <mElementsNeedCount; i++) addRandomTask();

		mRecycleAdapter.notifyDataSetChanged();

    	



    }






	@Override
	public void onDialogYes(int mDialogCode) {
		

		if (mDialogCode == DIALOG_CLEAR) //Was yes pressed? on dialog clear all
			{
			   mRecycleAdapter.ClearTasks();
			   //mPrefsHelper.saveTasks(mRecycleAdapter.getTasks());
			}
		
	}




	@Override
	public void onDialogNo(int mDialogCode) {
		
		
	}

	@Override
	public void UpdateTime(long mID, long timeStart, long timeEnd) {

		Realm instance = ((MApp)getApplicationContext()).realm;
		Task mTask = instance.where(Task.class).equalTo("id", mID).findFirst();

		if (mTask == null)
		{
			Log.e(MainActivity.TAG,"Can't find task with id="+Long.toString(mID));
			//instance.close();
			return;
		}

		instance.beginTransaction();


		mTask.setStartTime(timeStart);
		mTask.setEndTime(timeEnd);

		instance.commitTransaction();
		//instance.close();


		mRecycleAdapter.notifyDataSetChanged();

	}


}
