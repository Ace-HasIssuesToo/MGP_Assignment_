package com.sidm.mymgp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by User on 5/12/2016.
 */

public class Fingerline {
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private final Paint p;
    public boolean isDrawn;
    public boolean isDraw;

    public Fingerline() {
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.RED);
        p.setStrokeWidth(5);
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
        isDrawn = false;
        isDraw = false;
    }

    public void draw(Canvas canvas){
        canvas.drawLine(startX, startY, endX, endY, p);
    }

    public float getStartX()
    {
        return startX;
    }
    public float getStartY()
    {
        return startY;
    }
    public float getEndX()
    {
        return endX;
    }
    public float getEndY()
    {
        return endY;
    }

    public void setStart(float x, float y)
    {
        startX = x;
        startY = y;
    }
    public void setEnd(float x, float y)
    {
        endX = x;
        endY = y;
    }
}
