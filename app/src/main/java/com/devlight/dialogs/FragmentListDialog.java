package com.devlight.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class FragmentListDialog  extends DialogFragment {

    int mDialogCode;

    public FragmentListDialog()
    {

    }


    public interface DialogListResultListener {
        void onListSelected(int mDialogCode,String result);

    }




    public static FragmentListDialog newInstance(int mDialogCode, ArrayList<String> mList) {
        FragmentListDialog mInstance = new FragmentListDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putStringArrayList("mList",mList);
        args.putInt("mDialogCode", mDialogCode);
        mInstance.setArguments(args);

        return mInstance;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<String> mList = getArguments().getStringArrayList("mList");
        mDialogCode = getArguments().getInt("mDialogCode");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);

        arrayAdapter.addAll(mList);

        mBuilder.setNegativeButton(
                android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        mBuilder.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  String strName = arrayAdapter.getItem(which);
                        ((DialogListResultListener) getActivity()).onListSelected(mDialogCode,arrayAdapter.getItem(which));

                    }
                });


        return mBuilder.create();
    }
}
