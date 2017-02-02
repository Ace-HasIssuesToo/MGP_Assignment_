package com.sidm.mymgp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by User on 1/2/2017.
 */

public class TutorialPage extends Activity implements View.OnClickListener {
    // Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;
    /*To detect swipe*/
    boolean is_down = false;
    boolean is_up = false;
    Vector2D down_pos;
    Vector2D up_pos;
    @Override
    // initialize activity
    protected void onCreate(Bundle savedInstanceState) {
        // call parent class's onCreate, super allows access to overridden methods
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title so can be full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        // Set the UI layout for mainmenu to follow
        setContentView(R.layout.tutorial);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;
    }

    // *Must* be implemented since this is an interface function
    public void onClick(View v)
    {
        // Sort of the glue or transition between activities
        Intent intent = new Intent();
        //if (v == btn_start)
        {
            intent.setClass(this, Gamepage.class);
        }
        startActivity(intent); // Go to gamepage class
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        short X = (short)event.getX(); // t
        short Y = (short)event.getY();
        int action = event.getAction(); // check for the action of touch
        //int action = MotionEventCompat.getActionMasked(event);
        down_pos = up_pos = Vector2D.Zero;
        /**/
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                is_down = true;
                down_pos = new Vector2D(X, Y);
                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_UP) :
                is_up = true;
                up_pos = new Vector2D(X, Y);
                if (is_down && is_up) {
                    if (CheckIfSwipedRight(down_pos, up_pos))
                    {
                        Intent intent = new Intent();
                        //if (v == btn_start)
                        {
                            intent.setClass(this, Gamepage.class);
                        }
                        startActivity(intent); // Go to gamepage class
                    }
                }
                else
                {
                    is_up = false;
                    is_down = false;
                }
                break;
            default :
                return super.onTouchEvent(event);
        }
        return true;
    }

    private boolean CheckIfSwipedRight(Vector2D down, Vector2D up)
    {
        Vector2D dist = new Vector2D(up.x - down.x, up.y - up.y);
        float threshold = Screenwidth * 0.7f;
        if (dist.LengthSquared() > (threshold * threshold))
        {
            return true;
        }
        return false;
    }
}
