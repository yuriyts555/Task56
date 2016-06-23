package com.devlight.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

public class FragmentOkCancelDialog extends DialogFragment {
	

	int mDialogCode;
	String mMessage;
	
	
	public FragmentOkCancelDialog()
	{

	}

    public static FragmentOkCancelDialog newInstance(int mDialogCode,String mMessage) {
        FragmentOkCancelDialog mInstance = new FragmentOkCancelDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("mDialogCode", mDialogCode);
        args.putString("mMessage", mMessage);
        mInstance.setArguments(args);

        return mInstance;
    }
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		


	}

	
/*	public FragmentOkCancelDialog(int mDialogCode,String mMessage)
	{
		this.mDialogCode = mDialogCode;
		this.mMessage  = mMessage;
	}*/

	public interface DialogYesNoListener {
        void onDialogYes(int mDialogCode);

        void onDialogNo(int mDialogCode);
    }

/*    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DialogYesNoListener)) {
            throw new ClassCastException(activity.toString() + " must implement YesNoListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
		mDialogCode = getArguments().getInt("mDialogCode");
		mMessage = getArguments().getString("mMessage");
		
		
        return new AlertDialog.Builder(getActivity())
                //.setTitle(R.string.dialog_my_title)
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogYesNoListener) getActivity()).onDialogYes(mDialogCode);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogYesNoListener) getActivity()).onDialogNo(mDialogCode);
                    }
                })
                .create();
    }
}
