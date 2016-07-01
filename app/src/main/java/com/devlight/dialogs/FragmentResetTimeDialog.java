package com.devlight.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.devlight.task.Task;
import com.devlight.taskmanager.R;


public class FragmentResetTimeDialog extends DialogFragment {


    long mID;


    public FragmentResetTimeDialog()
    {

    }

    public static FragmentResetTimeDialog newInstance(long mID) {
        FragmentResetTimeDialog mInstance = new FragmentResetTimeDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong(Task.KEY_ID, mID);
        mInstance.setArguments(args);

        return mInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    public interface DialogResetTimeListener {
        void onResetStartTime(long mID);

        void onResetEndTime(long mID);
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

        mID = getArguments().getLong(Task.KEY_ID);



        return new AlertDialog.Builder(getActivity())
                //.setTitle(R.string.dialog_my_title)
                //.setMessage(mMessage)

                .setNeutralButton(R.string.reset_time_end, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogResetTimeListener) getActivity()).onResetEndTime(mID);
                    }
                })
                .setPositiveButton(R.string.reset_time_start, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DialogResetTimeListener) getActivity()).onResetStartTime(mID);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                })
                .create();
    }
}