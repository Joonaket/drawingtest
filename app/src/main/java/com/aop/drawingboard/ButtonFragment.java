package com.aop.drawingboard;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;



public class ButtonFragment extends Fragment {
    private static final int STORAGE_PERMISSION_CODE = 1;
    private DrawingBoard drawingBoard;

    public ButtonFragment() {

        super(R.layout.fragment_button);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawingBoard = requireActivity().findViewById(R.id.drawingBoard);

        Button saveButton = view.findViewById(R.id.saveButton);

        Button newButton = view.findViewById(R.id.newButton);


        saveButton.setOnClickListener(e ->{
           saveDrawing();
        });


        newButton.setOnClickListener(e ->{
            newDialog(this.getView());
        });
    }

    private void newDialog(View view) {
        MaterialAlertDialogBuilder builderBob = new MaterialAlertDialogBuilder(this.getView().getContext());
        builderBob.setMessage(R.string.dialog_new_info);
        builderBob.setPositiveButton(R.string.dialog_confirm,(dialogInterface,i) ->drawingBoard.clearEverything());

        builderBob.setNegativeButton(R.string.dialog_decline,(dialogInterFace,i) -> {});

        builderBob.show();
    }


    private void saveDrawing() {
        if(drawingBoard !=null){
            drawingBoard.saveBitmap(requireContext());
        }
        else{
            Toast.makeText(requireContext(),"No drawing board found",Toast.LENGTH_SHORT).show();
        }

    }




}