package com.sidm.mymgp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.LineBackgroundSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import static android.R.attr.x;

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
    final int numGrids = 9;
    private Grid[] gridarray = new Grid[numGrids];
    private endpoints[] endlist = new endpoints[100];

    // var for one grid circle
    private float cirX = 0, cirY = 0;
    private Spriteanimation circle;
    private Spriteanimation circleP;

    //Ebola Hardcode for now
    private float cirX1 = 0, cirY1 = 0;
    private float cirX2 = 0, cirY2 = 0;
    private float cirX3 = 0, cirY3 = 0;

    private Spriteanimation circlePressed;

    // var for line
    private Fingerline[] linelist = new Fingerline[numGrids + 1];
    int numLines = 0;
    int currIndex = 0;
    int prevIndex = 0;
    // var for score
    int totalScore;
    int currScore;
    // Variables for FPS
    public float FPS;
    float deltaTime;
    long dt;

    //define font type used
    Typeface myfont;

    // Variable for Game State check // EDIT Scenemanager
    private short GameState;

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
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.matrix);
        scalebg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);

        circlePressed = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcirclespritesheet), 288, 96, 3, 3);
        // for grid circle and line
        for (int i = 0; i < gridarray.length; ++i)
        {
            gridarray[i] = new Grid();
        }
        circleP = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcircle2), 384 / 2, 128 / 2, 1, 1);
        circle = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcirclespritesheet2), 384 / 2, 128 / 2, 3, 3);
        cirX = Screenwidth / 10;
        cirY = Screenheight * 0.5f;
        cirX1 = Screenwidth / 2;
        cirY1 = Screenheight * 0.5f;
        cirX2 = Screenwidth / 10;
        cirY2 = Screenheight * 0.25f;
        cirX3 = Screenwidth / 2;
        cirY3 = Screenheight * 0.25f;

        gridarray[0].spriteanimation = circleP;
        gridarray[0].x = cirX;
        gridarray[0].y = cirY;
        gridarray[1].spriteanimation = circleP;
        gridarray[1].x = cirX1;
        gridarray[1].y = cirY1;
        gridarray[2].spriteanimation = circleP;
        gridarray[2].x = cirX2;
        gridarray[2].y = cirY2;
        gridarray[3].spriteanimation = circleP;
        gridarray[3].x = cirX3;
        gridarray[3].y = cirY3;

        //Load font
        myfont = Typeface.createFromAsset(getContext().getAssets(),"fonts/finalf.ttf");

        for (int i = 0; i < linelist.length; ++i)
        {
            linelist[i] = new Fingerline();
        }
        for (int i = 0; i < endlist.length; ++i)
        {
            endlist[i] = new endpoints();
        }

        totalScore = 0; currScore = 0;
//        int posx = 2;
//        for (int i = 0; i < gridarray.length; ++i)
//        {
//            gridarray[i].bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), Screenwidth / 10, Screenheight / 10, true);
//            if (posx != 0) {
//                gridarray[i].x = Screenwidth / (posx);
//            }
//            else {
//                gridarray[i].x = Screenwidth;
//            }
//            posx += 5;
//            //Log.v("sam", "pos: " + posx + " index: " + i);
//            gridarray[i].y = Screenheight * 0.5f;
//        }

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
        canvas.drawBitmap(scalebg, bgX, bgY + Screenheight, null);

        // Bonus) To print FPS on the screen
        RenderTextOnScreen(canvas, "FPS: " + FPS, 130, 75, 50, 255, 0, 255, 255);

        // draw line
        if (numLines == 0)
            linelist[0].draw(canvas);
        for (int i = 0; i < numLines; ++i)
            linelist[i].draw(canvas);


        // draw the grid

        for (int i = 0; i < gridarray.length; ++i)
        {
            if (gridarray[i].spriteanimation != null)
            {
                gridarray[i].spriteanimation.draw(canvas);
                gridarray[i].spriteanimation.setX((int)gridarray[i].x);
                gridarray[i].spriteanimation.setY((int)gridarray[i].y);
            }
        }
        RenderTextOnScreen(canvas, "T-SCORE: " + totalScore, 110, 1000, 80, 0, 0, 255, 255);
    }

    public int touchGrid(MotionEvent event)
    {
        for (int i = 0; i < gridarray.length; ++i)
        {
            float x = gridarray[i].x;
            float y = gridarray[i].y;
            if(gridarray[i].spriteanimation != null && checkOnGrid(event, x, y, gridarray[i].spriteanimation.getSpriteWidth(), gridarray[i].spriteanimation.getSpriteHeight()))
            {
                return i; // which grid is being interacted
            }
        }
        return -1;
    }


    //Update method to update the game play
    public void update(float dt, float fps){ // edit
        FPS = fps;
        switch (GameState) {
            case 0: {
                // 3) Update the background to allow panning effect
                bgY -= 500 * dt; // temp value to speed the panning
                if (bgY < -Screenheight)
                {
                    bgY=0;
                }
                circle.update(System.currentTimeMillis());
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
    public void RenderTextOnScreen(Canvas canvas, String text, int posX, int posY, int textsize, int r, int g, int b, int alpha)
    {
        Paint paint = new Paint();
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        if (r < 0) r = 0;
        if (g < 0) g = 0;
        if (b < 0) b = 0;
        paint.setARGB(alpha, r, g, b);
        paint.setStrokeWidth(100);
        paint.setTextSize(textsize);
        paint.setTypeface(myfont);
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

        currIndex = touchGrid(event);
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                gridarray[currIndex].spriteanimation = circle;
                if (currIndex >= 0 && linelist[numLines].isDrawn == false)
                {
                    linelist[numLines].setStart(X, Y);
                    linelist[numLines].isDraw = true;
                    prevIndex = currIndex;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currIndex >= 0 && currIndex != prevIndex && !linelist[numLines].isDrawn)
                {
                    gridarray[currIndex].spriteanimation = circle;
                    if (numLines != 0)
                    {
                        linelist[numLines].setStart(linelist[numLines - 1].getEndX(), linelist[numLines- 1].getEndY());
                        Log.v("index", "circle " + currIndex + " light up!");
                    }
                    else if(numLines <= 2)
                    {
                        for (int i = 0; i < linelist.length; ++i)
                        {
                            for (int j = 0; j < linelist.length; ++j)
                            {
                                if(endlist[i] == endlist[j])
                                {
                                    linelist[numLines].isDrawn = false;
                                }
                            }
                        }
                    }
                    linelist[numLines].setEnd(X, Y);
                    linelist[numLines].isDrawn = true;
                    endlist[numLines].setEndPoint(linelist[numLines].getEndX(),linelist[numLines].getEndY());
                    ++numLines;
                    prevIndex = currIndex;

                    invalidate();
                }
                else if (!linelist[numLines].isDrawn)
                {
                    linelist[numLines].setEnd(X, Y);
                }
                break;
            case MotionEvent.ACTION_UP:
                    for (int i = 0; i < linelist.length; ++i)
                    {
                        linelist[i].setEnd(linelist[i].getStartX(), linelist[i].getStartY());
                        linelist[i].isDraw = false;
                        linelist[i].isDrawn = false;
                    }
                for(int i = 0; i < gridarray.length; i++)
                {
                    gridarray[i].spriteanimation = circleP;
                }
                currScore = numLines;
                numLines = 0;
                prevIndex = 0;
                totalScore += currScore;
                //}
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
