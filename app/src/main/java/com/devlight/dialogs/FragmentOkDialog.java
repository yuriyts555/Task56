package com.devlight.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class FragmentOkDialog extends DialogFragment {
	


	String mMessage;
	
	
	public FragmentOkDialog()
	{

	}
	
    public static FragmentOkDialog newInstance(String mMessage) {
    	FragmentOkDialog mInstance = new FragmentOkDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("mMessage", mMessage);
        mInstance.setArguments(args);

        return mInstance;
    }
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		


	}

	
	public FragmentOkDialog(String mMessage)
	{
	
		this.mMessage  = mMessage;
	}





    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
	
		mMessage = getArguments().getString("mMessage");
		
		
        return new AlertDialog.Builder(getActivity())
                //.setTitle(R.string.dialog_my_title)
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	dialog.dismiss();
                    }
                })
         
                .create();
    }
}
