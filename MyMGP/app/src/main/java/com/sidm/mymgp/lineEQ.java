package com.sidm.mymgp;

/**
 * Created by User on 8/12/2016.
 */

public class lineEQ
{
    float y, m, x, c;
    public lineEQ(float m1, float x1, float c1)
    {
       m = m1;
       x = x1;
       c = c1;

       y = m * x + c;
    }
}
