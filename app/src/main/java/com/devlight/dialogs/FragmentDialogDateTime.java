package com.devlight.dialogs;

import java.util.Calendar;

import com.devlight.task.*;
import com.devlight.taskmanager.*;



import android.content.Context;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class FragmentDialogDateTime extends DialogFragment{


	public interface ITimeUpdater {

		public void UpdateTime(long mID,long timeStart,long timeEnd);

	}


	public static final int SET_START_TIME = 0; //input start data
	public static final int SET_END_TIME = 1; //input end data
	
	
	TextView tvHeader;
	DatePicker mDatePicker;
	TimePicker mTimePicker;
	

	int curTimeType = SET_START_TIME; // start  or end  time
	//Context mContext;

	long timeStart;
	long timeEnd;
	long mID;

	
	public static FragmentDialogDateTime newInstance(long mID,long timeStart,long timeEnd,int curTimeType) {
		FragmentDialogDateTime fInstance = new FragmentDialogDateTime();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        args.putInt("curTimeType", curTimeType);
        args.putLong("timeStart", timeStart);
		args.putLong("timeEnd", timeEnd);
		args.putLong("mID", mID);
        fInstance.setArguments(args);

        return fInstance;
    }


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() == null) return;


		curTimeType = getArguments().getInt("curTimeType" );
		timeEnd = getArguments().getLong("timeEnd");
		timeStart = getArguments().getLong("timeStart");
		mID = getArguments().getLong("mID");
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.dialog_date_time, container, false);
		
		tvHeader = (TextView) mView.findViewById(R.id.tvHeader);
		mDatePicker = (DatePicker) mView.findViewById(R.id.datePicker);
		mTimePicker = (TimePicker) mView.findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		

		long time;
		if (curTimeType == SET_START_TIME) 
		{
			   tvHeader.setText(R.string.set_start_time);
				time =timeStart;
		}
		else
		{
			   tvHeader.setText(R.string.set_end_time);
				time = timeEnd;
		}
		
		Calendar mCalendar = Calendar.getInstance();
		if (time == -1) time = mCalendar.getTimeInMillis();
		else mCalendar.setTimeInMillis(time);
		
		mDatePicker.updateDate(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), 
                mCalendar.get(Calendar.DAY_OF_MONTH));

		mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
		
		
		final Button btnOk = (Button) mView.findViewById(R.id.btnOk);
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Calendar mCalendar = Calendar.getInstance();
				
				mCalendar.set(Calendar.YEAR, mDatePicker.getYear());
				mCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
				mCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
				
				mCalendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
				mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
				
				if (curTimeType == SET_START_TIME) 
					{
					   if (timeEnd !=-1L)
						   if (timeEnd<=mCalendar.getTimeInMillis())
						   {
							   showErrorMsg(R.string.set_timestart_error);
							   return;
						   }

						timeStart = mCalendar.getTimeInMillis();
					}
				else 
					{
					  
					   if (timeStart !=-1L)
						   if (timeStart>=mCalendar.getTimeInMillis())
						   {
							   showErrorMsg(R.string.set_timeend_error);
							   return;
						   }
						timeEnd = mCalendar.getTimeInMillis();
					}
				
				
				((ITimeUpdater)getActivity()).UpdateTime(mID,timeStart,timeEnd); //Update
				
				FragmentDialogDateTime.this.dismiss();
				
			}
		});
		
		
		final Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				FragmentDialogDateTime.this.dismiss();
				
			}
		});

		
		return mView;
	}
	
	
	void showErrorMsg(int msg)
	{
		FragmentOkDialog mDialog = FragmentOkDialog.newInstance(getString(msg));
    	mDialog.show(getActivity().getSupportFragmentManager(), "");
	}

	
	
	public FragmentDialogDateTime()
	{
	
	}
	
	

}
