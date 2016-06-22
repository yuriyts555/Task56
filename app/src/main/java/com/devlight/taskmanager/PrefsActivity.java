package com.devlight.taskmanager;

import com.devlight.dialogs.ColorDialog;

import com.devlight.utils.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class PrefsActivity extends AppCompatActivity implements ColorDialog.IColorUpdate {
	
	Toolbar toolBar;
	boolean isExitSnackBarShowing= false;
	
	int mColors[] = new int[3];
	SharedPreferences mPrefs;
	Editor mEditor;
	
    LinearLayout l0;
    LinearLayout l1;
    LinearLayout l2;
	
	
	@Override
	public void onBackPressed() {
		tryExit();
		
	}
	
	
	void tryExit()
	{

		if (isExitSnackBarShowing)
		{
	        Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
			finish();
		}
		else {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.prefs_activity);
        
        toolBar = (Toolbar) findViewById(R.id.tbPrefs); //Use toolbar
        setSupportActionBar(toolBar);  
        getSupportActionBar().setTitle("");
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        //Get colors
		mPrefs = getSharedPreferences(PrefsHelper.PREFS_NAME, Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
		mColors[0] = mPrefs.getInt(PrefsHelper.KEY_COLOR0, getResources().getColor(R.color.green_bkg));
		mColors[1] = mPrefs.getInt(PrefsHelper.KEY_COLOR1, getResources().getColor(R.color.yellow_bkg));
		mColors[2] = mPrefs.getInt(PrefsHelper.KEY_COLOR2, getResources().getColor(R.color.red_bkg));
        
        
        l0=(LinearLayout) findViewById(R.id.layoutColor0);
        l1=(LinearLayout) findViewById(R.id.layoutColor1);
        l2=(LinearLayout) findViewById(R.id.layoutColor2);
        
        l0.setBackgroundColor(mColors[0]);        
        l1.setBackgroundColor(mColors[1]);       
        l2.setBackgroundColor(mColors[2]);
        
        
        l0.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showColorDialog(0);
				
			}
		});
        
        l1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showColorDialog(1);
				
			}
		});
        
        l2.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				showColorDialog(2);
				
			}
		});
        
    }
    
    
    void showColorDialog(final int colorToChange)
    {
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogColor");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    ColorDialog newFragment = ColorDialog.newInstance(mColors[colorToChange],colorToChange);
	    newFragment.show(ft, "dialogColor");
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prefs, menu);
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
	        Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
	        finish();
        	return true;
        }
        
        
        if (id == R.id.action_restore)
        {
    		mColors[0] = getResources().getColor(R.color.green_bkg);
    		mColors[1] = getResources().getColor(R.color.yellow_bkg);
    		mColors[2] = getResources().getColor(R.color.red_bkg);
    		
    		
            l0.setBackgroundColor(mColors[0]);        
            l1.setBackgroundColor(mColors[1]);       
            l2.setBackgroundColor(mColors[2]);
        	return true;
        }
        
        if (id == R.id.action_ready)
        {
			mEditor.putInt(PrefsHelper.KEY_COLOR0, mColors[0]);
			mEditor.putInt(PrefsHelper.KEY_COLOR1, mColors[1]);
			mEditor.putInt(PrefsHelper.KEY_COLOR2, mColors[2]);
			mEditor.commit();
        	
        	//return task to previous activity
	        Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_OK,returnIntent);
	        finish();
        	return true;
        }
        
        
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void UpdateColor(int value,int index ) {
		
		mColors[index] = value;
		
	
		
        l0.setBackgroundColor(mColors[0]);        
        l1.setBackgroundColor(mColors[1]);       
        l2.setBackgroundColor(mColors[2]);
		
		
	}

}
