package com.aop.drawingboard;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import androidx.annotation.Nullable;


public class DrawingBoard extends View {
    private LinkedList<PaintedPath> Paths = new LinkedList<PaintedPath>();

    private PaintedPath activePath;
    private int selectedPaint = 0;
    private Paint[] paints;

    public DrawingBoard(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        loadPaints();
    }


    private void loadPaints(){
        int[] colorCodes = new int[]{Color.BLACK,Color.WHITE,Color.RED,Color.GREEN,Color.BLUE
        ,Color.YELLOW,Color.DKGRAY,Color.CYAN,Color.MAGENTA,Color.LTGRAY};

        paints = new Paint[colorCodes.length];

        for(int i=0; i<colorCodes.length;i++){
            int colorCode = colorCodes[i];


            Paint paint = new Paint();
            paint.setColor(colorCode);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(15F);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.MITER);

            paints[i] = paint;
            setBackgroundColor(Color.WHITE);
        }

    }



    private class PaintedPath {
        public PaintedPath(Paint paint) {
            this.path = new Path();
            this.paint = paint;
        }

        public Path path;
        public Paint paint;
    }

    @Override
    protected void onDraw(Canvas targetCanvas){
        super.onDraw(targetCanvas);
        for(PaintedPath p: Paths){
            targetCanvas.drawPath(p.path,p.paint);
        }
    }

    public void clearEverything(){
        Paths = new LinkedList<PaintedPath>();

        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x= e.getX();
        float y = e.getY();

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                activePath= new PaintedPath(paints[selectedPaint]);
                activePath.path.moveTo(x,y);
                Paths.add(activePath);
                break;
            case MotionEvent.ACTION_MOVE:
                activePath.path.lineTo(x,y);
                break;
        }
        invalidate();
        return true;
    }


    public Paint[] getPaints(){
        return paints;
    }

    public void setActivePaintIndex(int paintNumber){
        selectedPaint = paintNumber;
    }


    private Bitmap createBitmap(){
        return Bitmap.createBitmap(
                getWidth(),
                getHeight(),
                Bitmap.Config.ARGB_8888);
    }

    public Bitmap getBitMapFromCanvas(){
        Canvas c = new Canvas();

        Bitmap bits = createBitmap();
        c.setBitmap(bits);

        c.drawColor(Color.WHITE);
        draw(c);
        return bits;

    }

    public void saveBitmap(Context context) {
        Bitmap bitmap = getBitMapFromCanvas();
        String fileName = "Drawing_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DrawingApp");

        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = null;

        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                try (OutputStream out = resolver.openOutputStream(imageUri)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                }
                Toast.makeText(context, "File has been successfully hidden behind a stack of other files", Toast.LENGTH_SHORT).show();
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING,0);
                resolver.update(imageUri,values,null,null);
                Toast.makeText(context, "Believe it or not, saved", Toast.LENGTH_SHORT).show();
            }




        } catch (IOException e) {
            if (imageUri != null) {
                resolver.delete(imageUri, null, null);
            }
            Toast.makeText(context, "Something went wrong...somewhere (Saving error)", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

