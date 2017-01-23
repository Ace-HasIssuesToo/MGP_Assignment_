package com.sidm.mymgp;

import android.graphics.Bitmap;

/**
 * Created by User on 23/1/2017.
 */

public class Ally extends Objects
{
    public Ally(Bitmap bitmap_, float positionX_, float positionY_)
    {
        bitmap = bitmap_;
        position.x = positionX_;
        position.y = positionY_;

    }

    public enum states
    {
        State_move,
        State_idle,
        State_heal,
        State_Counter
    }

    public void update(float dt)
    {

    }
}
