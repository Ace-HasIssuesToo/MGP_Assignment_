package com.sidm.mymgp;

import android.graphics.Bitmap;

/**
 * Created by User on 23/1/2017.
 */
//This is the computer main frame you are protecting
public class MainFrameOBJ extends Objects
{

    //Constructor
    public MainFrameOBJ(Bitmap bitmap_, float positionX, float positionY)
    {
        bitmap = bitmap_;
        position.x = positionX;
        position.y = positionY;

    }

    public void update(float dt)
    {
    }
}
