package com.aop.drawingboard;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public class ColorSelectorFragment extends Fragment {

    LinearLayout paintContainer;
    HorizontalScrollView paintSelector;


    public ColorSelectorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_color_selector,container,false);

        paintContainer = view.findViewById(R.id.paintContainer);
        paintSelector = view.findViewById(R.id.paintSelector);

        MainActivity main = (MainActivity)getActivity();
        InitializePaintSelection(main.getActiveCanvas());
        return view;
    }


    public void InitializePaintSelection(DrawingBoard board){
        Paint[] paints = board.getPaints();
        for(int i = 0; i< paints.length; i++){
            Paint p = paints[i];
            Button B = new Button(getContext());
            B.setBackgroundColor(p.getColor());
            B.setTag(p.getColor());


            B.setMinimumHeight(100);
            B.setMinimumWidth(100);
            B.setTag(i);
            B.setTextSize(28f);
            B.setOnClickListener(view -> {
                SelectPaint((int)view.getTag());
                updateButtonColor(B,p.getColor());
            });

            paintContainer.addView(B);
        }

    }

    private void SelectPaint(int paintNumber){

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getActiveCanvas().setActivePaintIndex(paintNumber);

        for(int i = 0; i< paintContainer.getChildCount(); i++){
            Button b = (Button)paintContainer.getChildAt(i);

            b.setText(i == paintNumber ? "✓":"");
            updateButtonColor(b,(int)b.getTag());
            b.invalidate();
        }
    }

    private void updateButtonColor(Button button, int backgroundColor) {

        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);


        double luminance = (0.2126 * red + 0.7152 * green + 0.0722 * blue) / 255;


        if (luminance > 0.5) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.WHITE);
        }
    }
}