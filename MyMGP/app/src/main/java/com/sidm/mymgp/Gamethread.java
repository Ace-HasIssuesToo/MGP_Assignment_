package com.sidm.mymgp;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by 153942B on 11/24/2016.
 */

public class Gamethread extends Thread {
    // The actual view that handles inputs and draws to the surface
    private Gamepanelsurfaceview myView;

    // Surface holder that can access the physical surface
    private SurfaceHolder holder;

    // Flag to hold game state
    private boolean isRun;

    private boolean isPause;

    // get actual fps
    int frameCount;
    long lastTime = 0;
    long lastFPSTime = 0;
    float fps;
    float dt;

    // Constructor for this class
    public Gamethread(SurfaceHolder holder, Gamepanelsurfaceview myView){
        super(); // super() is used to invoke immediate parent class constructor.
        isRun = true; // for running
        isPause = false; // for pause
        this.myView = myView;
        this.holder = holder;
    }

    public void startRun(boolean r){
        isRun = r;
    }

    public void pause(){
        synchronized (holder) {
            isPause = true;
        }
    }

    public void unPause(){
        synchronized (holder) {
            isPause = false;
            holder.notifyAll();
        }
    }

    //Return Pause
    public boolean getPause() {
        return isPause;
    }

    public void calculateFPS() // Editable
    {
        frameCount++;

        long currentTime = System.currentTimeMillis();
        dt = (currentTime - lastTime) / 1000.f;
        lastTime = currentTime;

        if(currentTime - lastFPSTime > 1000)
        {
            fps = (frameCount * 1000.f) / (currentTime - lastFPSTime);
            lastFPSTime = currentTime;
            frameCount = 0;
        }
    }

    @Override
    public void run(){
        while (isRun){
            //Update game state and render state to the screen
            Canvas c = null;
            try {
                c = this.holder.lockCanvas();
                synchronized(holder){
                    if (myView != null){
                        if (getPause() == false)
                        {
                            myView.update(dt, fps);
                            myView.doDraw(c);
                        }
                    }
                }
                synchronized(holder){
                    while (getPause()==true){
                        try {
                            holder.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }

            finally{
                if (c!=null){
                    holder.unlockCanvasAndPost(c);
                }
            }
            calculateFPS();
        }

    }
}
