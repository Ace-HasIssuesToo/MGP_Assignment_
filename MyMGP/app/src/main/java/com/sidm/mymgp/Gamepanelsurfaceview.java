package com.sidm.mymgp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by 153942B on 11/24/2016.
 */

public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback{
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering

    // 1a) Variables used for background rendering
    private Bitmap bg, scalebg; //bg = background and scaledbg = version of bg
    // 1b) Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;
    // 1c) Variables for defining background start and end point
    private short bgX = 0, bgY = 0;
    // 4a) bitmap array to stores 4 images of the spaceship
    private Bitmap[] ship = new Bitmap[3];
    // 4b) Variable as an index to keep track of the spaceship images
    private short shipIndex = 0;
    // 2 more variables to place my ship where it will be based on the touch of the screen
    private short mX = 0, mY = 0;

    // var for flying coin
    private int coinX = 0, coinY = 0;
    protected boolean moveship = false;

    // var for one grid circle
    private float cirX = 0, cirY = 0;
    private Bitmap circle;

    // Variables for FPS
    public float FPS;
    float deltaTime;
    long dt;

    // Variable for Game State check // EDIT Scenemanager
    private short GameState;

    // Init the Sprite Animation
private Spriteanimation flyingcoins;

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview (Context context){

        // Context is the current state of the application/object
        super(context);

        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        // 1d) Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;
        // 1e)load the image when this class is being instantiated
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.gamescene);
        scalebg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);

        circle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);

        // 4c) Load the images of the spaceships
        ship[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1), Screenwidth / 10, Screenheight / 10, true);
        ship[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2), Screenwidth /10, Screenheight/10, true);
        ship[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3), Screenwidth/10, Screenheight/10, true);
        //ship[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4), Screenwidth, Screenheight, true);

        // Load the sprite sheet
        flyingcoins = new Spriteanimation(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flystar), Screenwidth/4, Screenheight/4, true), 320, 64, 5, 5);
        // Create the game loop thread
        myThread = new Gamethread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){
        // Create the thread
        if (!myThread.isAlive()){
            myThread = new Gamethread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        // Destroy the thread
        if (myThread.isAlive()){
            myThread.startRun(false);


        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    public void RenderGameplay(Canvas canvas) { // edit
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scalebg, bgX, bgY, null);
        canvas.drawBitmap(scalebg, bgX + Screenwidth, bgY, null);

        // Draw spaceships
        canvas.drawBitmap(ship[shipIndex], mX, mY, null); // location of the ship is based on the touch

        //location of ship based on touch


        // Bonus) To print FPS on the screen
        RenderTextOnScreen(canvas, "FPS: " + FPS, 130, 75, 50);
        // draw the sprite
        flyingcoins.draw(canvas);

        flyingcoins.setX(coinX);
        flyingcoins.setY(coinY);

        // draw the grid
        canvas.drawBitmap(circle, cirX, cirY, null); // location of the ship is based on the touch
        cirX = Screenwidth / 10;
        cirY = Screenheight * 0.5f;
    }


    //Update method to update the game play
    public void update(float dt, float fps){ // edit
        FPS = fps;

        switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                bgX -= 500 * dt; // temp value to speed the panning
                if (bgX < -Screenwidth)
                {
                    bgX=0;
                }


                // 4e) Update the spaceship images / shipIndex so that the animation will occur.
                shipIndex++;
                shipIndex%=3;

                // Make Sprite animate
                flyingcoins.update(System.currentTimeMillis());
            }
            break;
        }
    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas){ // edit
        switch (GameState)
        {
            case 0:
                RenderGameplay(canvas);
                break;
        }
    }

    // Print text on screen
    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize)
    {
        Paint paint = new Paint();
        paint.setARGB(255, 255, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);
        canvas.drawText(text, posX, posY, paint);
    }

    //collision check
    boolean CheckCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
    {
        if (x2 >= x1 && x2 <= x1 + w1) {
        if (y2 >= y1 && y2 <= y1 + h1)
            return true;
    }
        if (x2+w2>=x1 && x2+w2<=x1+w1) { // Top right corner
            if (y2 >= y1 && y2 <= y1 + h1)
                return true;
        }
//        if (x2 >= x1 && x2 <= x1 + w1) {
//            if (y2 <= y1 && y2 + h2 >= y1 + h1)
//                return true;
        //}
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){ // edit
        short X = (short)event.getX(); // t
        short Y = (short)event.getY();
        int action = event.getAction(); // check for the action of touch
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                // to check finger touch x,y with image (spaceship)
                if (CheckCollision(mX, mY, ship[shipIndex].getWidth() / 2, ship[shipIndex].getHeight() / 2, X, Y, 0, 0) || CheckCollision(mX, mY, ship[shipIndex].getWidth(), ship[shipIndex].getHeight(), X, Y, 0, 0))
                {
                      moveship = true;
                }
                else
                {
                      moveship = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveship == true)
                {
                    mX = (short)(X - ship[shipIndex].getWidth() /2 );
                    mY = (short)(Y - ship[shipIndex].getHeight() /2);
                }
                // Check collision with flying coin
                if (CheckCollision(mX, mY, ship[shipIndex].getWidth() / 2, ship[shipIndex].getHeight() / 2, coinX, coinY, flyingcoins.getSpriteWidth() / 2, flyingcoins.getSpriteHeight() / 2))
                {
                    Random randomnum = new Random();
                    coinX = randomnum.nextInt(Screenwidth);
                    coinY = randomnum.nextInt(Screenheight);
                }
                break;
        }          return true;
        // 5) In event of touch on screen, the spaceship will relocate to the point of touch
//        short X = (short)event.getX(); // temp value of the screen touch
//        short Y = (short)event.getY();
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            mX = (short)(X - ship[shipIndex].getWidth() * 0.5);
//            mY = (short)(Y - ship[shipIndex].getHeight() * 0.5);
//        }
//
//        return super.onTouchEvent(event);
    }
}
