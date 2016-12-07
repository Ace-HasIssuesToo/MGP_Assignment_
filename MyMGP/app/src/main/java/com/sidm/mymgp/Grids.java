package com.sidm.mymgp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by User on 7/12/2016.
 */

public class Grids{
    float x, y;
    Bitmap bitMap;
    Context context;
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    int Screenwidth = metrics.widthPixels;
    int Screenheight = metrics.heightPixels;

    public Grids()
    {
        x = 0;
        y = 0;
    }
    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){

    }

    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }
}
