package com.sidm.mymgp;

/**
 * Created by User on 23/1/2017.
 */

public class SpawnTimer {
    SpawnTimer(float starttime_, float delay_, float period_)
    {
        original_time = starttime_;
        timer = starttime_;
        delay = delay_;
        period = period_;
        can_run = false;
    }
    public float timer;
    public float delay; // in seconds
    public float period; // before it executes again
    public float original_time;
    public boolean can_run;

    public void Update(float dt)
    {
        if (timer < 0)
            return;
        if (timer >= period) {
            if (dt < delay && timer < delay) {
                timer += dt;
            }
            else {
                can_run = true;
                timer = original_time;
            }
        }
    }
}
