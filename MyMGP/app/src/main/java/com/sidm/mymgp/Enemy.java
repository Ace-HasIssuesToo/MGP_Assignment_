package com.sidm.mymgp;

import android.graphics.Bitmap;
import android.os.Debug;
import android.util.Log; // remove when used finished

/**
 * Created by User on 22/1/2017.
 */

public class Enemy extends Objects {
    public Enemy(float x_, float y_, int num_waypoints_, boolean active_)
    {
        position.x = x_;
        position.y = y_;
        MAX_WAYPOINTS = num_waypoints_;
        waypoints = new Vector2D[MAX_WAYPOINTS];
        state = STATE.STATE_MOVE;
        type = PATTERN.TYPE_DOWN;
        for (int i = 0; i < MAX_WAYPOINTS; ++i)
        {
            waypoints[i] = Vector2D.Zero;
        }
        waypoint_index = 0;
        setActive(active_);
    }
    private int MAX_WAYPOINTS;
    // current waypoint enemy is at
    public int waypoint_index;
    public Vector2D[] waypoints;

    public enum STATE
    {
        STATE_IDLE,
        STATE_MOVE,
        STATE_MAX_STATE
    }

    public enum PATTERN
    {
        TYPE_LEFT,
        TYPE_RIGHT,
        TYPE_UP,
        TYPE_DOWN,
        TYPE_MAX_TYPE
    }

    public STATE state;
    public PATTERN type;

    @Override
    public void Update(float dt)
    {
        super.Update(dt);
        switch (state)
        {
            case STATE_MOVE:
                // Speed of enemy
                float speed = 200;
                // Delta time speed of enemy, frame rate independant
                float delta_speed = speed * dt;
                if (!position.equals(waypoints[waypoint_index], delta_speed)) // Not at destination
                {
                    Vector2D dir = new Vector2D((waypoints[waypoint_index].x - position.x), (waypoints[waypoint_index].y - position.y)); // the direction towards destination
                    dir = dir.Normalize();
                    if (delta_speed < speed) { // check if dt is haywire, some kind of 'e' float value
                        float next_x = dir.x * delta_speed; // I can't do position += dir * dt;
                        float next_y = dir.y * delta_speed;
                        position.x += next_x;
                        position.y += next_y; // Move by frame towards destination's direction until reach destination
                    }
                }
                else if (!waypoints[waypoint_index + 1].equals(Vector2D.Zero, dt)) // if the next place is not a zero vector
                    ++waypoint_index; // Go to next waypoint
                //else
                    //state = STATE.STATE_IDLE;
            break;
            default:

                break;
        }
    }
}
