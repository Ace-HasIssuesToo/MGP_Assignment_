package com.sidm.mymgp;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by 153942B on 11/24/2016.
 */
// Multi-threading
public class Gamethread extends Thread {
    // The actual view that handles inputs and draws to the surface
    private Gamepanelsurfaceview myView;

    // Surface holder that can access the physical surface like editing pixels
    private SurfaceHolder holder;

    // Flag to hold game state
    private boolean isRun;

    private boolean isPause;

    // get actual fps
    int frameCount; // how many frames
    long lastTime = 0; // last frame time
    long lastFPSTime = 0; // last time fps was calculated
    float fps; // frames per second
    float dt; // delta time, time between each frame

    // Constructor for this class
    public Gamethread(SurfaceHolder holder, Gamepanelsurfaceview myView){
        super(); // super() is used to invoke immediate parent class constructor. Call Thread constructor
        isRun = true; // for running
        isPause = false; // for pause
        this.myView = myView; // Get the game surface image
        this.holder = holder;
    }

    // Starts thread running
    public void startRun(boolean r){
        isRun = r;
    }

    public void pause(){
        synchronized (holder) { // synchronized prevents thread inteference and memory problems like accessing the same memory of variable for each thread and having different values
            isPause = true; // only one thread can execute that block of code at a time.
        }
    }

    public void unPause(){
        synchronized (holder) {
            isPause = false;
            holder.notifyAll(); // Tell all threads to wake up and fight for rice bowl after unpausing
        }
    }

    //Return Pause
    public boolean getPause() {
        return isPause;
    }

    public void calculateFPS() // Editable
    {
        frameCount++;

        long currentTime = System.currentTimeMillis(); // Current value of the running Java Virtual Machine's high-resolution time source, in nanoseconds. used to calculate elapsed time
        dt = (currentTime - lastTime) / 1000.f; // find out how much elapsed time using curr - prev time then make it a smaller number
        lastTime = currentTime;

        if(currentTime - lastFPSTime > 1000) // time passed is more than a 1000 milli-seconds
        {
            fps = (frameCount * 1000.f) / (currentTime - lastFPSTime); // fps is current frame num * 1000 / 1000+ number, i think framecount is random since it depends on how many times this function is called
            lastFPSTime = currentTime; // sort of like restarting the timer ot count to a thousand
            frameCount = 0; // reset the frame num so we know how many times this function is run, 1000 milliseconds later
        }
    }

    @Override
    public void run(){ // This function *must* be ran since you inherited Thread
        while (isRun){
            //Update game state and render state to the screen
            Canvas c = null;
            try {
                c = this.holder.lockCanvas();
                // lockCanvas() creates a surface area that you will write to, until you call unlockCanvasAndPost() no other code can call lockCanvas() and write to the surface until your code is finished
                // A lock is a synchronization primitive that is used to guard against simultaneous accessing of resources/code by multiple threads
                // Can result in deadlock which means thread is not unlocked, one threads holds the key to unlocking a thread at any time
                synchronized(holder){ // only one thread can access this at a time
                    if (myView != null){ // if screen view exists
                        if (getPause() == false)
                        {
                            myView.update(dt, fps); // Call GamePanelSurfaceView class update
                            myView.doDraw(c); // draw, if more than one thread can access at one time, it is messy
                        }
                    }
                }
                synchronized(holder){
                    while (getPause()==true){
                        try {
                            holder.wait(); // "chotomate", stop!
                        } catch (InterruptedException e) { // catch errors and exceptions if they come
                        }
                    }
                }
            }

            finally{
                if (c!=null){
                    holder.unlockCanvasAndPost(c); // unlock canvas and let everything run
                }
            }
            calculateFPS(); // this function may run more than 1 time per frame
        }

    }
}
