package com.aop.drawingboard;



import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;


public class DrawingBoard extends View {
    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> saveFileLauncher;
    private Bitmap currentBitmap;
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
        currentBitmap = getBitMapFromCanvas();

        String fileName = "Drawing_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        saveFileLauncher.launch(intent);
    }

    public void setSaveFileLauncher(ActivityResultLauncher<Intent> launcher){
        this.saveFileLauncher = launcher;
    }

    public void printScreen(){

        PrintManager manager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);

        Bitmap bits = getBitMapFromCanvas();

        String jobName = getContext().getString(R.string.app_name) + "Drawing" ;


        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                int pageHeight = newAttributes.getMediaSize().getHeightMils() /1000;
                int pageWidth = newAttributes.getMediaSize().getWidthMils()/1000;
                int bitmapWidth = bits.getWidth();
                int bitmapHeight = bits.getHeight();

                float scaleWidth = (float) pageWidth/(float) bitmapWidth;
                float scaleHeight =(float) pageWidth/(float) bitmapHeight;
                float scale = Math.min(scaleWidth,scaleHeight);

                PrintDocumentInfo.Builder builderBob = new PrintDocumentInfo.Builder(jobName).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(1);

                PrintDocumentInfo documentInfo = builderBob.build();
                callback.onLayoutFinished(documentInfo,true);

            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

                try(OutputStream out = new FileOutputStream(destination.getFileDescriptor())){
                    bits.compress(Bitmap.CompressFormat.PNG,100,out);
                } catch(IOException e){
                    e.printStackTrace();
                } finally {
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                }

            }
        };
        manager.print(jobName,printAdapter,null);

        //Toast.makeText(this.getContext(),"Placeholder proof of printing completion",Toast.LENGTH_SHORT).show();
    }
}

