package com.devlight.task;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Locale;


import io.realm.annotations.*;


import io.realm.RealmObject;



public class Task extends RealmObject {



	@PrimaryKey
    private long id;



	@Ignore
	public final static String KEY_ID = "id";
	@Ignore
	public final static String KEY_AUTOFINISH = "AUTOFINISH";
	@Ignore
	public final static String KEY_HEADER = "mHeader";
	@Ignore
	public final static String KEY_COMMENT = "mComment";
	@Ignore
	public final static String KEY_STARTTIME = "mStartTime";
	@Ignore
	public final static String KEY_ENDTIME = "mEndTime";

	@Ignore
	public final static int MIN_HEADER_LENGTH = 5;


	private String mHeader;
	private String mComment;

	private long mStartTime = - 1; //Task start time
	private long mEndTime = -1;  //Task end time

	private long mResetTime = -1;  // task end was reseted

	private long mAutoFinishAfterMinutes = -1;  //Task auto finish after this minutes

	
	
	public static final String DATE_TIME_PRESENTATION = "yyyy.MM.dd HH:mm";
	


	public void setID(long _id)
	{
	  id = _id;
	}

	public long  getID()
	{
       return  id;
	}


	public void setResetTime(long value)
	{
		mResetTime = value;
	}

	public long  getResetTime()
	{
		return  mResetTime;
	}

	public void setAutoFinishMinutes(long value)
	{
		mAutoFinishAfterMinutes = value;
	}

	public long  getAutoFinishMinutes()
	{
		return  mAutoFinishAfterMinutes;
	}
	
	
	public Task()
	{

	}
	
	public long getStartTime()
	{
		return mStartTime;
	}
	
	public long getEndTime()
	{
		return mEndTime;
	}
	
	
	public void setStartTime(long value)
	{
		mStartTime = value;
	}
	
	public void setEndTime(long value)
	{
		mEndTime = value;
	}
	
	
	public void setHeader(String mHeader)
	{
		this.mHeader = mHeader;
	}


	public void setComment(String mComment)
	{

		this.mComment = mComment;
	}
	
	
	public String getHeader()
	{
		return mHeader;
	}
	
	public String getComment()
	{
		return mComment;
	}
	
	
	public String getStrStartTime()  //get Start date and time as String
	{
		if (mStartTime ==-1) return "-";
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_TIME_PRESENTATION,Locale.getDefault());
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mStartTime);
		
		return mDateFormat.format(mCalendar.getTime());
	}


	static public String getTimeByString(long value)  //get Start date and time as String
	{
		if (value ==-1) return "-";

		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_TIME_PRESENTATION,Locale.getDefault());

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(value);

		return mDateFormat.format(mCalendar.getTime());
	}
	
	public String getStrEndTime()//get End date and time as String
	{
		if (mEndTime ==-1) return "-";
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_TIME_PRESENTATION,Locale.getDefault());
		
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mEndTime);		

		
		return mDateFormat.format(mCalendar.getTime());
	}
	
	public String getStrSpentTime() //get spend time as String
	{
		if (mStartTime == -1 || mEndTime == -1) return "-";
		
		long mDifMinutes = (mEndTime - mStartTime)/(1000L*60L);  //Get difference in minutes
		
			   
	   long mHours = mDifMinutes/60L;  //Get hours
	   long mMinutes = mDifMinutes- mHours*60L;  //Get minutes
	   
	   return getTwoDigitString(mHours)+":"+getTwoDigitString(mMinutes);
	}


	public static String getTimeDifString(long mStartTime, long mEndTime) {

		if (mStartTime == -1 || mEndTime == -1) return "-";

		long mDifMinutes = (mEndTime - mStartTime)/(1000L*60L);  //Get difference in minutes


		long mHours = mDifMinutes/60L;  //Get hours
		long mMinutes = mDifMinutes- mHours*60L;  //Get minutes

		return getTwoDigitString(mHours)+":"+getTwoDigitString(mMinutes);

	}
	
	
	static String getTwoDigitString(long value) //return string from long in minimum two digit representation
	{
		
		if (value==0) return "00";
		
		if (value<10L) return "0"+Long.toString(value);
		
		return Long.toString(value);
	}
	
	
	public String getStrTimePresentation()  //get string dd:MM:yyyy hh:mm - dd:MM:yyyy hh:mm   hh:mm
	{
	  if (mStartTime == -1 && mEndTime == -1) return "";
	  if ( mEndTime == -1) return getStrStartTime();
		
		return getStrStartTime()+" - "+getStrEndTime()+" "+getStrSpentTime();
	}



}
