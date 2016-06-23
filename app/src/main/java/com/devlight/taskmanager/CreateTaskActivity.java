package com.devlight.taskmanager;


import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.dialogs.FragmentListDialog;
import com.devlight.dialogs.FragmentOkCancelDialog;
import com.devlight.dialogs.FragmentOkDialog;
import com.devlight.task.*;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements FragmentDialogDateTime.ITimeUpdater,
																	 FragmentListDialog.DialogListResultListener{


	public static final int SPEECH_RESULTS_HEADER = 5;
	public static final int SPEECH_RESULTS_COMMENT= 6;
	

	//Task mTask;
	//int mPosition;
	
	EditText etHeader = null;
	EditText etComment = null;
	TextView tvStartTimeDate = null;
	TextView tvEndTimeDate = null;
	TextView tvSpentTime =  null;
	EditText etAutoFinishMinutes = null;

	FloatingActionButton fabVoiceHeader;
	FloatingActionButton fabVoiceComment;
	
	Toolbar toolBar;
	boolean isExitSnackBarShowing= false;  //is snackbar showing now (Press back again to exit)


	//
	private long mID;
	String mHeader;
	String mComment;
	long mStartTime = - 1; //Task start time
	long mEndTime = -1;  //Task end time
	long mAutoFinishAfterMinutes = -1;  //Task auto finish after this minutes

   //Speech recognition
	ArrayList<String> mWordList = null;
	boolean mShowHeaderWordSelect = false;
	boolean mShowCommentWordSelect = false;
	
	@Override
	public void onBackPressed() {
		tryExit();
		
	}
	
	
	void tryExit()
	{

		if (isExitSnackBarShowing) //Second back pressed
		{
	        Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
			finish();
		}
		else {//First tie pressed, show snakbar

			LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layoutMain);
			Snackbar snackbar = Snackbar.make(mainLayout, R.string.exit_without_save, Snackbar.LENGTH_LONG);
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

	
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	
    	//mTask.setStrings(etHeader.getText().toString(),etComment.getText().toString());
    	
    	outState.putLong(Task.KEY_ID, mID);
		outState.putLong(Task.KEY_AUTOFINISH, mAutoFinishAfterMinutes);
		outState.putString(Task.KEY_HEADER, mHeader);
		outState.putString(Task.KEY_COMMENT, mComment);
		outState.putLong(Task.KEY_STARTTIME, mStartTime);
		outState.putLong(Task.KEY_ENDTIME, mEndTime);

		super.onSaveInstanceState(outState);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.create_task_activity);
        
        toolBar = (Toolbar) findViewById(R.id.tbCreateEdit); //Use toolbar
        setSupportActionBar(toolBar);  
        getSupportActionBar().setTitle("");
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        
        //CutomFragmentDialog m = new CutomFragmentDialog(0,"ffff");
        
        
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {

            } else {

				mID = extras.getLong(Task.KEY_ID);
				mAutoFinishAfterMinutes = extras.getLong(Task.KEY_AUTOFINISH);
				mHeader = extras.getString(Task.KEY_HEADER);
				mComment = extras.getString(Task.KEY_COMMENT);
				mStartTime = extras.getLong(Task.KEY_STARTTIME);
				mEndTime = extras.getLong(Task.KEY_ENDTIME);

            }
        } else {

			mID = savedInstanceState.getLong(Task.KEY_ID);
			mAutoFinishAfterMinutes = savedInstanceState.getLong(Task.KEY_AUTOFINISH);
			mHeader = savedInstanceState.getString(Task.KEY_HEADER);
			mComment = savedInstanceState.getString(Task.KEY_COMMENT);
			mStartTime = savedInstanceState.getLong(Task.KEY_STARTTIME);
			mEndTime = savedInstanceState.getLong(Task.KEY_ENDTIME);

        }
        

        
          etHeader = (EditText) findViewById(R.id.etHeader);
          etComment = (EditText) findViewById(R.id.etComment);
          etAutoFinishMinutes = (EditText) findViewById(R.id.etAutoFinishMinutes);




        
       final  OnClickListener mOnClickStartTime = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Change start date

			showDateDialog(FragmentDialogDateTime.SET_START_TIME);
		}
	  };
	  
	  
      final  OnClickListener mOnClickEndTime = new OnClickListener() {
  		
		@Override
		public void onClick(View v) {
			
			//Change end date

			showDateDialog(FragmentDialogDateTime.SET_END_TIME);
			
		}
	  };
	  
	  
	  
	  
	  tvStartTimeDate = (TextView) findViewById(R.id.tvStartTimeDate);	  
	  tvEndTimeDate = (TextView) findViewById(R.id.tvEndTimeDate);	 
	  tvSpentTime = (TextView) findViewById(R.id.tvSpentTime);
	  
	  
	  final TextView tvEndTimeHeader = (TextView) findViewById(R.id.tvEndTimeHeader);
	  final TextView tvStartTimeHeader = (TextView) findViewById(R.id.tvStartTimeHeader);
	  //final TextView tvSpentTimeHeader = (TextView) findViewById(R.id.tvSpentTimeHeader);
	  
	  etComment.setText(mComment);
	  etHeader.setText(mHeader);
		etAutoFinishMinutes.setText(Long.toString(mAutoFinishAfterMinutes));
	  
	  tvStartTimeDate.setText(Task.getTimeByString(mStartTime) );
	  tvEndTimeDate.setText(Task.getTimeByString(mEndTime));
	  tvSpentTime.setText(Task.getTimeDifString(mStartTime,mEndTime));
	  
	  
	  
	  tvStartTimeHeader.setOnClickListener(mOnClickStartTime);
	  tvStartTimeDate.setOnClickListener(mOnClickStartTime);	  
	  tvEndTimeHeader.setOnClickListener(mOnClickEndTime);
	  tvEndTimeDate.setOnClickListener(mOnClickEndTime);


		 fabVoiceHeader = (FloatingActionButton) findViewById(R.id.fabVoiceHeader);
		 fabVoiceComment = (FloatingActionButton) findViewById(R.id.fabVoiceComment);


		List<ResolveInfo> intActivities = getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (intActivities.size() !=0){

			//use speech recognition
			fabVoiceComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					listenToSpeech(SPEECH_RESULTS_COMMENT);
				}
			});


			fabVoiceHeader.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					listenToSpeech(SPEECH_RESULTS_HEADER);
				}
			});


		} else
		{
			//no  speech recognition module
			fabVoiceHeader.setVisibility(View.GONE);
			fabVoiceComment.setVisibility(View.GONE);
		}
        

    }


	private void listenToSpeech(int returnCode) {

		//start the speech recognition intent passing required data
		Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		//indicate package
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		//message to display while listening
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
		//set speech model
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		//specify number of results to retrieve
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
		//start listening
		startActivityForResult(listenIntent, returnCode);

	}


	@Override
	public void onListSelected(int mDialogCode, String result) {

		if (mDialogCode == SPEECH_RESULTS_COMMENT) etComment.setText(result);
		if (mDialogCode == SPEECH_RESULTS_HEADER) etHeader.setText(result);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//get spech result
		if (requestCode == SPEECH_RESULTS_COMMENT && resultCode == Activity.RESULT_OK) {

			mWordList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (mWordList.size() ==0) return;



			mShowCommentWordSelect = true;


		//	etComment.setText(suggestedWords.get(0));

		}

		if (requestCode == SPEECH_RESULTS_HEADER && resultCode == Activity.RESULT_OK) {

			mWordList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (mWordList.size() ==0) return;


			//etHeader.setText(suggestedWords.get(0));
			mShowHeaderWordSelect = true;


			//FragmentListDialog mDialog = FragmentListDialog.newInstance(SPEECH_RESULTS_HEADER,suggestedWords);
			//mDialog.show(getSupportFragmentManager(), "");
		}


	}

	@Override
	protected void onResumeFragments()
	{
		super.onResumeFragments();

			if (mShowCommentWordSelect)
			{
				mShowCommentWordSelect = false;
				FragmentListDialog mDialog = FragmentListDialog.newInstance(SPEECH_RESULTS_COMMENT, mWordList);
				mDialog.show(getSupportFragmentManager(), "");
			}

			if (mShowHeaderWordSelect)
			{
				mShowHeaderWordSelect = false;
				FragmentListDialog mDialog = FragmentListDialog.newInstance(SPEECH_RESULTS_HEADER, mWordList);
				mDialog.show(getSupportFragmentManager(), "");
			}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_edit, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        
        if (id == android.R.id.home)
        {
        	tryExit();
        	return true;
        }
        
        
        if (id == R.id.action_cancel)
        {
        	//finish this activity
        	Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
	        finish();
        	return true;
        }
        
        if (id == R.id.action_ready)
        {

			if (etHeader.getText().toString().length()<Task.MIN_HEADER_LENGTH)
			{
				String _str = getString(R.string.min_header_length)+" "+Integer.toString(Task.MIN_HEADER_LENGTH);
				FragmentOkDialog mDialog = FragmentOkDialog.newInstance(_str);
				mDialog.show(getSupportFragmentManager(), "");
				return true;
			}


			mHeader = etHeader.getText().toString();
			mComment = etComment.getText().toString();

			if (etAutoFinishMinutes.getText().toString().length()>0)
			{
				long i = Long.parseLong(etAutoFinishMinutes.getText().toString());
				if (i<0) i=0;
				mAutoFinishAfterMinutes = i;

			} else mAutoFinishAfterMinutes = 0;



			//return task to previous activity
	        Intent returnIntent = new Intent();



			returnIntent.putExtra(Task.KEY_ID,mID);
			returnIntent.putExtra(Task.KEY_AUTOFINISH,mAutoFinishAfterMinutes);
			returnIntent.putExtra(Task.KEY_HEADER,mHeader);
			returnIntent.putExtra(Task.KEY_COMMENT,mComment);
			returnIntent.putExtra(Task.KEY_STARTTIME,mStartTime);
			returnIntent.putExtra(Task.KEY_ENDTIME,mEndTime);

	        setResult(Activity.RESULT_OK,returnIntent);
	        finish();
        	return true;
        }
        
        
        return super.onOptionsItemSelected(item);
    }



    
    
	void showDateDialog(int timeType) { //Change start/end time/date

	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogDateTime");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    FragmentDialogDateTime newFragment = FragmentDialogDateTime.newInstance(mID,mStartTime,mEndTime,timeType);
	    newFragment.show(ft, "dialogDateTime");
	}


	@Override
	public void UpdateTime(long mID,long timeStart, long timeEnd) {

		mStartTime = timeStart;
		mEndTime =  timeEnd;

		tvStartTimeDate.setText(Task.getTimeByString(mStartTime) );
		tvEndTimeDate.setText(Task.getTimeByString(mEndTime));
		tvSpentTime.setText(Task.getTimeDifString(mStartTime,mEndTime));

	}



}
