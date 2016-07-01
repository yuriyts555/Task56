package com.devlight.taskmanager;


import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.dialogs.FragmentListDialog;

import com.devlight.dialogs.FragmentOkDialog;
import com.devlight.task.*;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateTaskActivity extends AppCompatActivity implements FragmentDialogDateTime.ITimeUpdater,
																	 FragmentListDialog.DialogListResultListener{

	//public static final int NEWBITMAPSIZE_DP = 100;
	public static final int SPEECH_RESULTS_HEADER = 5;
	public static final int SPEECH_RESULTS_COMMENT= 6;
//	private static final int PICK_IMAGE_ACTIVITY = 7;

	private Uri mCropImageUri;
	//Task mTask;
	//int mPosition;
	
	EditText etHeader = null;
	EditText etComment = null;
	TextView tvStartTimeDate = null;
	TextView tvEndTimeDate = null;
	TextView tvSpentTime =  null;
	EditText etAutoFinishMinutes = null;

	EditText etPeriodicHours = null;
	CheckBox cbIsPeriodic = null;

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
	boolean mIsPaused = false;
	boolean mIsPeriodic = false;
	long mPeriodicHours = -1;
	String mAvatarPath;


   //Speech recognition
	ArrayList<String> mWordList = null;
	boolean mShowHeaderWordSelect = false;
	boolean mShowCommentWordSelect = false;

	ImageView mLogoView = null;
	
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

		outState.putLong(Task.KEY_PERIODICHOURS, mPeriodicHours);
		outState.putBoolean(Task.KEY_ISPERIODIC, mIsPeriodic);
		outState.putBoolean(Task.KEY_ISPAUSED, mIsPaused);
		outState.putString(Task.KEY_AVATARPATH, mAvatarPath);

		super.onSaveInstanceState(outState);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.create_task_activity);
        
        toolBar = (Toolbar) findViewById(R.id.tbCreateEdit); //Use toolbar
        setSupportActionBar(toolBar);  
        getSupportActionBar().setTitle("");
        
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);




        
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

				mIsPaused = extras.getBoolean(Task.KEY_ISPAUSED);
				mIsPeriodic = extras.getBoolean(Task.KEY_ISPERIODIC);
				mAvatarPath = extras.getString(Task.KEY_AVATARPATH);
				mPeriodicHours =  extras.getLong(Task.KEY_PERIODICHOURS);

			}
        } else {

			mID = savedInstanceState.getLong(Task.KEY_ID);
			mAutoFinishAfterMinutes = savedInstanceState.getLong(Task.KEY_AUTOFINISH);
			mHeader = savedInstanceState.getString(Task.KEY_HEADER);
			mComment = savedInstanceState.getString(Task.KEY_COMMENT);
			mStartTime = savedInstanceState.getLong(Task.KEY_STARTTIME);
			mEndTime = savedInstanceState.getLong(Task.KEY_ENDTIME);

			mIsPaused = savedInstanceState.getBoolean(Task.KEY_ISPAUSED);
			mIsPeriodic = savedInstanceState.getBoolean(Task.KEY_ISPERIODIC);
			mAvatarPath = savedInstanceState.getString(Task.KEY_AVATARPATH);
			mPeriodicHours =  savedInstanceState.getLong(Task.KEY_PERIODICHOURS);

        }
        

        
          etHeader = (EditText) findViewById(R.id.etHeader);
          etComment = (EditText) findViewById(R.id.etComment);
          etAutoFinishMinutes = (EditText) findViewById(R.id.etAutoFinishMinutes);


		etPeriodicHours = (EditText) findViewById(R.id.etPeriodicHours);
		  cbIsPeriodic = (CheckBox) findViewById(R.id.cbIsPeriodic);

		cbIsPeriodic.setChecked(mIsPeriodic);
		etPeriodicHours.setText(Long.toString(mPeriodicHours));
        
/*       final  OnClickListener mOnClickStartTime = new OnClickListener() {
		
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
	  };*/
	  
	  
	  
	  
	  tvStartTimeDate = (TextView) findViewById(R.id.tvStartTimeDate);	  
	  tvEndTimeDate = (TextView) findViewById(R.id.tvEndTimeDate);	 
	  tvSpentTime = (TextView) findViewById(R.id.tvSpentTime);
	  
	  
	  //final TextView tvEndTimeHeader = (TextView) findViewById(R.id.tvEndTimeHeader);
	 // final TextView tvStartTimeHeader = (TextView) findViewById(R.id.tvStartTimeHeader);
	  //final TextView tvSpentTimeHeader = (TextView) findViewById(R.id.tvSpentTimeHeader);
	  
	  etComment.setText(mComment);
	  etHeader.setText(mHeader);
		etAutoFinishMinutes.setText(Long.toString(mAutoFinishAfterMinutes));
	  
	  tvStartTimeDate.setText(Task.getTimeByString(mStartTime) );
	  tvEndTimeDate.setText(Task.getTimeByString(mEndTime));
	  tvSpentTime.setText(Task.getTimeDifString(mStartTime,mEndTime));
	  
	  
	  
/*	  tvStartTimeHeader.setOnClickListener(mOnClickStartTime);
	  tvStartTimeDate.setOnClickListener(mOnClickStartTime);	  
	  tvEndTimeHeader.setOnClickListener(mOnClickEndTime);
	  tvEndTimeDate.setOnClickListener(mOnClickEndTime);*/



		//ICon
		if (mAvatarPath == null)
		{
			getSupportActionBar().setIcon(R.drawable.ic_launcher);

		} else updateIcon();


		mLogoView = (ImageView) getToolbarLogoIcon(toolBar);
		if (mLogoView !=null)
			mLogoView.setOnClickListener(new View.OnClickListener()
		 {

			 @Override
			 public void onClick(View view) {
				changeIcon();
			 }
		 }
		);

		//Voice recognition

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


	@SuppressLint("NewApi")
	void changeIcon()
	{

/*		Intent intent = new Intent();
		intent.setType("image*//*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE_ACTIVITY);*/

		if (CropImage.isExplicitCameraPermissionRequired(this)) {
			requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
		} else {
			CropImage.startPickImageActivity(this);
		}


	}



	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


		if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {

			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				CropImage.startPickImageActivity(this);
			} else {
				//Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
			}
		}
		if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
			if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// required permissions granted, start crop image activity
				startCropImageActivity(mCropImageUri);
			} else {
				Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
			}
		}
	}


	private void startCropImageActivity(Uri imageUri) {
		CropImage.activity(imageUri)
				.setGuidelines(CropImageView.Guidelines.ON)
				.setAllowRotation(false)
				.setActivityTitle("")
				.setAspectRatio(1, 1)
				.setFixAspectRatio(true)
				.start(this);
	}


	public static View getToolbarLogoIcon(Toolbar toolbar){

		//check if contentDescription previously was set
		boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
		String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
		toolbar.setLogoDescription(contentDescription);
		ArrayList<View> potentialViews = new ArrayList<View>();
		//find the view based on it's content description, set programatically or with android:contentDescription
		toolbar.findViewsWithText(potentialViews,contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		//Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
		View logoIcon = null;
		if(potentialViews.size() > 0){
			logoIcon = potentialViews.get(0);
		}
		//Clear content description if not previously present
		if(hadContentDescription)
			toolbar.setLogoDescription(null);
		return logoIcon;
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


	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {


/*
		if (requestCode == PICK_IMAGE_ACTIVITY && resultCode == Activity.RESULT_OK) {

			if (data == null) return;


			CropImage.activity(data.getData())
					//.setGuidelines(CropImageView.Guidelines.ON)
					.setAspectRatio(1,1)
					.setGuidelines(CropImageView.Guidelines.ON)
					.setFixAspectRatio(true)
					.start(this);

		}
*/


		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK)
		{
			CropImage.ActivityResult result = CropImage.getActivityResult(data);


			Rect mRect = result.getCropRect();
	 		Uri mUri = result.getUri();

			String newPath = saveNewIcon(mUri,mRect);
			if (newPath == null) return;


			if (mAvatarPath !=null) //Free current bitmap in logo imageview
				if (mLogoView !=null)
				{
					Drawable drawable = mLogoView.getDrawable();
					if (drawable instanceof BitmapDrawable) {
						BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
						Bitmap bitmap = bitmapDrawable.getBitmap();
						bitmap.recycle();
					}
				}



			deleteOldIcon(mAvatarPath);



			mAvatarPath = newPath;

			updateIcon();

		}

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




		if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			Uri imageUri = CropImage.getPickImageResultUri(this, data);

			// For API >= 23 we need to check specifically that we have permissions to read external storage.
			if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
				// request permissions and handle the result in onRequestPermissionsResult()
				mCropImageUri = imageUri;
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
			} else {
				// no permissions required or already grunted, can start crop image activity
				startCropImageActivity(imageUri);
			}
		}


	}

	private String saveNewIcon(Uri mUri, Rect mSrcRect)
	{

		Bitmap mLargeBmp = null;

		try {
			mLargeBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mUri);
		}catch (Exception e)
		{
			Log.e(MainActivity.TAG,"Can't load application");

			LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layoutMain);
			Snackbar snackbar = Snackbar.make(mainLayout, R.string.errloadimage, Snackbar.LENGTH_SHORT);
			snackbar.show();

			return null;
		}



		int newBmpSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

		Bitmap mNewBitmap = Bitmap.createBitmap(newBmpSize,newBmpSize, Bitmap.Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mNewBitmap);

		Rect destRect = new Rect();
		destRect.left = 0;
		destRect.top = 0;
		destRect.right = newBmpSize;
		destRect.bottom = newBmpSize;

		mSrcRect.left = 0;
		mSrcRect.top = 0;
		mSrcRect.bottom = mLargeBmp.getHeight();
		mSrcRect.right = mLargeBmp.getWidth();

		mCanvas.drawBitmap(mLargeBmp,mSrcRect,destRect,null);


		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		File directory = cw.getDir("icons", Context.MODE_PRIVATE);
		File mypath=new File(directory, UUID.randomUUID().toString()+".png");

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(mypath);
			mNewBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();


			getContentResolver().delete(mUri, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}


		mNewBitmap.recycle();
		mLargeBmp.recycle();

		return mypath.getAbsolutePath();
	}

	void deleteOldIcon(String path)
	{
        if ( path ==null ) return;

		try {
			File file = new File(path);
			boolean deleted = file.delete();
		}catch (Exception e)
		{
			e.printStackTrace();
		}



	}

	void updateIcon()
	{

		if (mAvatarPath == null) return;

		BitmapFactory.Options options = new BitmapFactory.Options();


		try {

			Bitmap bitmap = BitmapFactory.decodeFile(mAvatarPath, options);
			Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
			getSupportActionBar().setIcon(mDrawable);
		//	bitmap.recycle();

		}catch (Exception e)
		{
			e.printStackTrace();
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
				long i = getLongFromTextEdit(etAutoFinishMinutes);
				if (i<0) i=0;
				mAutoFinishAfterMinutes = i;

			} else mAutoFinishAfterMinutes = 0;


			mIsPeriodic = cbIsPeriodic.isChecked();
			mPeriodicHours = getLongFromTextEdit(etPeriodicHours);



			//return task to previous activity
	        Intent returnIntent = new Intent();



			returnIntent.putExtra(Task.KEY_ID,mID);
			returnIntent.putExtra(Task.KEY_AUTOFINISH,mAutoFinishAfterMinutes);
			returnIntent.putExtra(Task.KEY_HEADER,mHeader);
			returnIntent.putExtra(Task.KEY_COMMENT,mComment);
			returnIntent.putExtra(Task.KEY_STARTTIME,mStartTime);
			returnIntent.putExtra(Task.KEY_ENDTIME,mEndTime);
			returnIntent.putExtra(Task.KEY_PERIODICHOURS,mPeriodicHours);
			returnIntent.putExtra(Task.KEY_ISPERIODIC,mIsPeriodic);
			returnIntent.putExtra(Task.KEY_ISPAUSED,mIsPaused);
			returnIntent.putExtra(Task.KEY_AVATARPATH,mAvatarPath);



	        setResult(Activity.RESULT_OK,returnIntent);
	        finish();
        	return true;
        }
        
        
        return super.onOptionsItemSelected(item);
    }

	private long getLongFromTextEdit(EditText mEditText)
	{
		String _str = mEditText.getText().toString();
		long i=0;

		try {

			i = Long.parseLong(_str);
			return i;
		}catch(Exception e)
		{
			return 0;
		}
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
