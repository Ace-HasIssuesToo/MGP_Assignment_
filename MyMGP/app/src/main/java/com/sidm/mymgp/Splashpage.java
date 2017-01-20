package com.sidm.mymgp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by 153942B on 11/17/2016.
 */

// It inherits from activity so it can be switched to
public class Splashpage extends Activity {
    // Is it active?
    protected boolean _active = true;
    // How long to display?
    protected int _splashTime = 5000; // in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar

        //thread for displaying the Splash Screen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(200); // wait 0.2% of a second
                        if (_active) {
                            waited += 200;
                        }
                    }
                } catch (InterruptedException e) {
                    //do nothing
                } finally {
                    finish(); // Destroys the current activity using onDestroy();
                    //Create new activity based on and intent with CurrentActivity
                    Intent intent = new Intent(Splashpage.this, Mainmenu.class);
                    startActivity(intent); // Go to main menu

                }
            }
        };
        splashTread.start();

        setContentView(R.layout.splashpage);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { // Check for input from finger
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // Motion event provides info about the touch, like position, size and orientation
            _active = false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

