package com.sidm.mymgp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import android.util.DisplayMetrics;
import android.util.Log;
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
    final int numGrids = 9;
    private Grid[] gridarray = new Grid[numGrids];
    // 4b) Variable as an index to keep track of the spaceship images
    private short shipIndex = 0;
    // 2 more variables to place my ship where it will be based on the touch of the screen
    private short mX = 0, mY = 0;

    // var for flying coin
    private int coinX = 0, coinY = 0;
    protected boolean moveship = false;

    // var for one grid circle
    private float cirX = 0, cirY = 0;
    private float cir2X = 0, cir2Y = 0;
    private float cir3X = 0, cir3Y = 0;
    private Bitmap circle;
    private Bitmap circle2;
    private Bitmap circle3;

    // var for line
    private Fingerline line;
    private Fingerline[] linelist = new Fingerline[numGrids];
    int numLines = 0;
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


        // for grid circle and line
        circle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);
        circle2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);
        circle3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);
        line = new Fingerline();
        line.setStart(-1, -1);
        for (int i = 0; i < linelist.length; ++i)
        {
            linelist[i] = new Fingerline();
        }
        // 4c) Load the images of the spaceships
//        ship[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1), Screenwidth / 10, Screenheight / 10, true);
//        ship[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_2), Screenwidth /10, Screenheight/10, true);
//        ship[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_3), Screenwidth/10, Screenheight/10, true);
        //ship[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_4), Screenwidth, Screenheight, true);

        for (int i = 0; i < 3; ++i)
        {
            ship[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ship2_1), Screenwidth / 10, Screenheight / 10, true);
        }

        for (int i = 0; i < gridarray.length; ++i)
        {
            gridarray[i] = new Grid();
        }

        int posx = 2;
        for (int i = 0; i < gridarray.length; ++i)
        {
            gridarray[i].bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);
            if (posx != 0) {
                gridarray[i].x = Screenwidth / (posx);
            }
            else {
                gridarray[i].x = Screenwidth;
            }
            posx += 5;
            //Log.v("sam", "pos: " + posx + " index: " + i);
            gridarray[i].y = Screenheight * 0.5f;
        }

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

        // draw line
        line.draw(canvas);
        for (int i = 0; i < numLines; ++i)
            linelist[i].draw(canvas);

        // draw the grid
        canvas.drawBitmap(circle, cirX, cirY, null); // location of the ship is based on the touch
        canvas.drawBitmap(circle2, cir2X, cir2Y, null); // location of the ship is based on the touch
        canvas.drawBitmap(circle3, cir3X, cir3Y, null); // location of the ship is based on the touch
//        for (int i = 0; i < gridarray.length; ++i)
//        {
//            canvas.drawBitmap(gridarray[i].bitmap, gridarray[i].x, gridarray[i].y, null); // location of the ship is based on the touch
//        }
        cirX = Screenwidth / 10;
        cirY = Screenheight * 0.5f;
        cir2X = Screenwidth / 2;
        cir2Y = Screenheight * 0.5f;
        cir3X = Screenwidth / 10;
        cir3Y = Screenheight * 0.2f;
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

    boolean fCheckCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2)
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

    public boolean checkOnGrid(MotionEvent event, float x, float y, float width, float height)
    {
        short X = (short)event.getX(); // t
        short Y = (short)event.getY();
        if (fCheckCollision(x, y, width, height, X, Y, 0, 0))
        {
            return true;
        }
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
                if (checkOnGrid(event, cirX, cirY, circle.getWidth(), circle.getHeight()) && line.isDrawn == false)
                {
                    X = (short)(cirX);
                    Y = (short)(cirY);
                    line.setStart(X, Y);
                    line.isDraw = true;
                    invalidate();
                }

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
                if (line.isDraw && !line.isDrawn) {
                    line.setEnd(X, Y);
                    invalidate();
                }
                if (checkOnGrid(event, cir2X, cir2Y, circle2.getWidth(), circle2.getHeight()) && line.isDrawn == false) {
                    line.setEnd(X, Y);
                    invalidate();
                    line.isDrawn = true;
                }

                if (checkOnGrid(event, cir2X, cir2Y, circle2.getWidth(), circle2.getHeight()) && line.isDrawn && numLines == 0) {
                    linelist[numLines].setStart(X, Y);
                }

                if (checkOnGrid(event, cir3X, cir3Y, circle3.getWidth(), circle3.getHeight()) && line.isDrawn && numLines == 0)
                {
                    linelist[numLines].setEnd(X, Y);
                    ++numLines;
                }
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
            case MotionEvent.ACTION_UP:
                if (!line.isDrawn) {
                    line.setStart(0, 0);
                    line.setEnd(line.getStartX(), line.getStartY());
                }
                for (int i = 0; i < linelist.length; ++i)
                {
                    linelist[i].setStart(0, 0);
                    linelist[i].setEnd(linelist[i].getStartX(), linelist[i].getStartY());
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
