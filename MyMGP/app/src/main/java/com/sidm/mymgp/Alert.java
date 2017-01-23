package com.sidm.mymgp;

/**
 * Created by 153942B on 1/12/2017.
 */

import android.os.Handler;
import android.os.Looper;

public class Alert {
    private Gamepanelsurfaceview Game;

    public Alert(Gamepanelsurfaceview Game) {

        this.Game = Game;
    }

    public void RunAlert() {
        Handler handler = new Handler(Looper.getMainLooper());
        // Returns the application's main looper, which lives in the main thread of the application

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Game.alert.show();
            }
        }, 1000); // Delay in milliseconds until the runnable is executed
    }
}

// Handler is used to handle messages using a provided looper
// Implements timeout, ticks and other timing based behaviours
// https://developer.android.com/reference/android/os/Handler.html#postDelayed(java.lang.Runnable,%20long)
// Run is to create a thread to execute a task
// Runnable is an interface
// Looper class is used to run a message loop for a thread. Use handler class to interact with the messgae loop
