package com.sidm.mymgp;

import android.graphics.Bitmap;

/**
 * Created by User on 7/12/2016.
 */

public class Grid {
    float x;
    float y;
    // For making sure grid nodes cannot be connected more than once
    boolean active;
    Spriteanimation spriteanimation;

    public Grid()
    {
        x =0;
        y = 0;
        active = false;
    }

    public void SetGridNodePosition(float x_, float y_)
    {
        x = x_;
        y = y_;
    }
}
