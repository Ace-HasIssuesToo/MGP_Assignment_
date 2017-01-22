package com.sidm.mymgp;

/**
 * Created by User on 8/12/2016.
 */

import static java.lang.Math.sqrt;

public class Vector2D {
    public float x;
    public float y;

    public Vector2D()
    {
        x = 0;
        y = 0;
    }

    public Vector2D(float x1, float y1)
    {
        x = x1;
        y = y1;
    }

    // Override the equals function for Vector2 objects
    //@Override
    public boolean equals(Object obj, float dt) {
        // Returns true if the object is a child of Vector2D(can be cast to parent class) and it is not null
        if (Vector2D.class.isInstance(obj.getClass())) {
            return false;
        }
        final Vector2D other = (Vector2D) obj;
        if (((this.x - other.x <= dt) && (other.x - this.x <= dt)) && ((this.y - other.y <= dt) && (other.y - this.y <= dt)))
            return true;

        return false;
    }

    public float Length(){

        return (float)(sqrt(x * x + y * y));

    }

    public Vector2D Normalize(){

        float d = Length();
        return new Vector2D((x / d), (y / d));

    }

    public static final Vector2D Zero = new Vector2D(0, 0);
    public static final float EPSILON = 0.1f;
}