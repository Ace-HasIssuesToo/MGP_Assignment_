package com.sidm.mymgp;

/**
 * Created by User on 8/12/2016.
 */

import static java.lang.Math.sqrt;


public class Vector2D {


    public float x;
    public float y;

    public Vector2D(float x1, float y1)
    {
        x = x1;
        y = y1;
    }


    public float Length(){

        return (float)(sqrt(x * x + y * y));

    }

    public Vector2D Normalize(){

        float d = Length();
        return new Vector2D((x / d), (y / d));

    }

}