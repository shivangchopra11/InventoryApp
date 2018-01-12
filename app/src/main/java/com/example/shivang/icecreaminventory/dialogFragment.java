package com.example.shivang.icecreaminventory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by shivang on 12/01/18.
 */

public class dialogFragment extends DialogFragment {
    private static final int CAMERA_REQUEST = 1888;
    DialogCallback dialogCallback;
    ImageView ivClick;

    public dialogFragment setCallBack(DialogCallback dialogCallback){
        this.dialogCallback = dialogCallback;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_item, container, false);
        Button clickItem = view.findViewById(R.id.clickItem);
        ivClick = view.findViewById(R.id.ivClick);
        clickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dialogCallback.getResults(CAMERA_REQUEST,ivClick);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
//        dialogCallback.getResults("hello");
    }

}
