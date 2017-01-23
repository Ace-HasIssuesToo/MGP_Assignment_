package com.sidm.mymgp;

/**
 * Created by sidm on 2/12/16.
 */

import android.graphics.Bitmap;

public class Objects {
    protected Bitmap bitmap;
    protected Vector2D position = new Vector2D();
    protected int x;
    protected int y;
    protected int O_Width;
    protected int O_Height;

    public Objects()
    {

    }
    public Objects (Bitmap bitmap, int x, int y)
    {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        position.x = x;
        position.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    // Get position
    public Vector2D getPos() {
        return position;
    }

    public int getWidth() {
        return bitmap.getWidth();
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public int setWidth(int O_Width) {
        this.O_Width = O_Width;
        return O_Width;
    }

    public int setHeight(int O_Height) {
        this.O_Height = O_Height;
        return O_Height;
    }
    // takes in delta time
    public void Update(float dt)
    {
    }
}

