package com.aop.drawingboard;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DrawingBoard activeCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_activity);


        activeCanvas = findViewById(R.id.drawingBoard);
    }

    public DrawingBoard getActiveCanvas() {
        return activeCanvas;
    }


}