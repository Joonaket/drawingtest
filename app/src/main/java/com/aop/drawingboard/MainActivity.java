package com.aop.drawingboard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private DrawingBoard activeCanvas;
    private ActivityResultLauncher<Intent> saveFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_activity);


        activeCanvas = findViewById(R.id.drawingBoard);
        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {

                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Uri uri = result.getData().getData();
                        try(OutputStream out = getContentResolver().openOutputStream(uri)){
                            activeCanvas.getBitMapFromCanvas().compress(Bitmap.CompressFormat.PNG,100, out);
                            Toast.makeText(this, "File saved well enough", Toast.LENGTH_SHORT).show();
                        } catch (IOException e){
                            Toast.makeText(this,"Something went wrong here",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        activeCanvas.setSaveFileLauncher(saveFileLauncher);
    }


    public DrawingBoard getActiveCanvas() {
        return activeCanvas;
    }


}