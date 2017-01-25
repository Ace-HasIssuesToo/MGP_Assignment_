package com.sidm.mymgp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.InputType;
import android.text.style.EasyEditSpan;
import android.text.style.LineBackgroundSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Random;
import java.util.Vector;

import static android.R.attr.bottom;
import static android.R.attr.x;

/**
 * Created by 153942B on 11/24/2016.
 */
// Provides a dedicated drawing surface embedded inside of a view. SurfaceView takes care of placing the surface at the correct location on the screen
public class Gamepanelsurfaceview extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener{
    // Implement this interface to receive information about changes to the surface.

    private Gamethread myThread = null; // Thread to control the rendering
    // Variables used for background rendering
    private Bitmap bg, scalebg; //bg = background and scaledbg = version of bg
    // Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;
    // Variables for defining background start and end point
    private short bgX = 0, bgY = 0;
    // Number of nodes in the grid
    final int numGrids = 9;
    // Create an array of nodes to form a grid
    private Grid[] gridarray = new Grid[numGrids];
    //Create a computer mainframe
    private MainFrameOBJ mainframe;
    // Create a list of enemies, use a EnemyManager class is needed in future
    final int NUM_ENEMIES = 5;
    Vector2D enemyorigin;
    final int NUM_WAYPOINT = 25; // How many waypoints are placed for enemy to follow
    Vector<Vector2D> des;
    private Enemy[] enemy_list = new Enemy[NUM_ENEMIES];
    // Determines which pattern player is going to dish out
    Enemy.PATTERN finger_pattern = Enemy.PATTERN.TYPE_MAX_TYPE;
    // Hardcoded according to cirX, cirY, cirX1, cirY1... These are used to improve readability in TouchEvent()
    private static final int INDEX_TOP_LEFT = 0;
    private static final int INDEX_TOP_RIGHT = 1;
    private static final int INDEX_BOTTOM_LEFT = 2;
    private static final int INDEX_BOTTOM_RIGHT = 3; // not final so it can be changed in the constructor during cirX declaraction
    private int[] finger_index; // Guess you can say this game is best played with the index_finger, badumtss.
    private int INDEX = 0;

    /*Timer for enemy spawning*/
    SpawnTimer spawn_timer;
    /**/
    /*Wall objects and kill-line*/
    public Objects[] wall_list;
    public Objects kill_line;
    /**/

    // var for one grid node
    private float cirX = 0, cirY = 0;
    private Spriteanimation circle;
    private Spriteanimation circleP;

    //Ebola Hardcode for now
    private float cirX1 = 0, cirY1 = 0;
    private float cirX2 = 0, cirY2 = 0;
    private float cirX3 = 0, cirY3 = 0;

    // The sprite animation for a pressed animation of the grid circle
    private Spriteanimation circlePressed;

    // Creating an array of lines for connecting the grid
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
    //long dt;
    // Checker class for intersection between lines
    intersection intersectCheck;
    Typeface myfont;
    // Image for star rating
    private Bitmap rating;
    // Amount of rating
    int numRate;
    // How much health is left
    int numHealth;
    private boolean isPaused = false;
    private Objects PauseB1;
    private Objects PauseB2;
    // Switch between activities/scenes/interactable classes
    Activity activityTracker; // used to track and launch different activity
    // A CharSequence is a readable sequence of char values. This interface provides uniform, read-only access to many different kinds of char sequences.
    CharSequence text;
    // Duration of message before disappearing
    int toastTime;
    // Pop up message
    Toast toast;

    // Alert dialog
    public boolean showAlert = false;
    AlertDialog.Builder alert = null;
    private Alert AlertObj; // based on Alert.java
    // Settings meant to be shared across all files
    // Interface, access and modify preference data using getSharedPreferences(String, int). There is a single instance of this class that all clients share. Modifications to the preferences must go through an SharedPreferences.Editor object
    SharedPreferences SharedPrefName;
    SharedPreferences.Editor editName;
    SharedPreferences SharedPrefScore;
    SharedPreferences.Editor editScore;
    // Names to be shared
    String Playername;
    // Highscore to be shared
    int HighScores;

    private SensorManager sensor;
    float[] SensorVar = new float[3];
    private float[] values = {0, 0, 0};
    private Bitmap ball;
    private float ballX = 0, ballY = 0;
    private long lastTime = System.currentTimeMillis();

    // Variable for Game State check // EDIT Scenemanager
    private short GameState;
    private SoundManager soundManager;
    public Vibrator m_vibrator;

    //constructor for this GamePanelSurfaceView class
    public Gamepanelsurfaceview (Context context, Activity activity){
        // Context is the current state of the application/object
        super(context);

        soundManager = new SoundManager(context);
        // Initiate vibrator
        m_vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Set information to get screen size, DisplayMetrics describe general information about display, size, density, and font scaling.
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;
        // getResources() returns you the app res folder, we then decode the image using its R.id
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.matrix);
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.dragonballpixel2);
        scalebg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);// For bigger/smaller version of bg
        circlePressed = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcirclespritesheet), 288, 96, 3, 3);
        // for grid circle and line
        for (int i = 0; i < gridarray.length; ++i)
        {
            gridarray[i] = new Grid();
        }
        circleP = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcircle2), 384 / 2, 128 / 2, 1, 1); // Default grid node texture
        circle = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcirclespritesheet2), 384 / 2, 128 / 2, 3, 3);
        cirX = Screenwidth * 0.42f - 150;
        cirY = Screenheight * 0.75f;
        cirX1 = Screenwidth * 0.42f + 150;
        cirY1 = Screenheight * 0.75f;
        cirX2 = Screenwidth * 0.42f - 150;
        cirY2 = Screenheight * 0.9f;
        cirX3 = Screenwidth * 0.42f + 150;
        cirY3 = Screenheight * 0.9f;

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

        // The computer mainframe position, which is also the "final destination"(badumtss) of enemies
        float comPosX = Screenwidth * 0.8f, comPosY = Screenheight * 0.2f;
        // Initialise enemies
        des = new Vector<>(); // Vector of waypoints
        des.add(new Vector2D(Screenwidth * 0.4f, Screenheight * 0.6f));
        des.add(new Vector2D(Screenwidth * 0.4f, comPosY));
        des.add(new Vector2D(comPosX, comPosY));
        enemyorigin = new Vector2D(Screenwidth * 0.15f, Screenheight * 0.6f); // starting position
        int rand_i = 0;
        for (int i = 0; i < enemy_list.length; ++i)
        {
            enemy_list[i] = new Enemy(enemyorigin.x, enemyorigin.y, NUM_WAYPOINT, false);
            for (int j = 0; j < des.size(); ++j)
                enemy_list[i].waypoints[j] = des.elementAt(j);
            rand_i = (int)(Math.random() * 10);
            if (rand_i % 2 == 0 && rand_i < 5)
                enemy_list[i].type = Enemy.PATTERN.TYPE_DOWN;
            else if (rand_i % 2 != 0 && rand_i < 5)
                enemy_list[i].type = Enemy.PATTERN.TYPE_UP;
            else if (rand_i % 2 == 0 && rand_i > 5)
                enemy_list[i].type = Enemy.PATTERN.TYPE_LEFT;
            else
                enemy_list[i].type = Enemy.PATTERN.TYPE_RIGHT;

            if (enemy_list[i].type == Enemy.PATTERN.TYPE_DOWN)
                enemy_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patterndown)), Screenwidth / 10, Screenwidth / 10, true);
            else if (enemy_list[i].type == Enemy.PATTERN.TYPE_UP)
                enemy_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternup)), Screenwidth / 10, Screenwidth / 10, true);
            else if (enemy_list[i].type == Enemy.PATTERN.TYPE_LEFT)
                enemy_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternleft)), Screenwidth / 10, Screenwidth / 10, true);
            else
                enemy_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternright)), Screenwidth / 10, Screenwidth / 10, true);
        }
        finger_index = new int[4];
        spawn_timer = new SpawnTimer(0.0f, 5, 0);

        Bitmap ComputerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.computer);
        mainframe = new MainFrameOBJ(ComputerSprite, comPosX, comPosY);

        wall_list = new Objects[5];
        for (int i = 0; i < wall_list.length; ++i)
        {
            wall_list[i] = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.hightechwall)), Screenwidth / 10, Screenheight / 10, true), 5, 5);
            wall_list[i].position.x = Screenwidth * 0.2f;
            wall_list[i].position.y = Screenheight * 0.5f - (Screenheight / 10) * i;
        }
        kill_line = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.laser)), (int)(Screenwidth * 0.6f - Screenwidth * 0.2f), Screenheight / 2, true), 5, 5);
        kill_line.position.Set(Screenwidth * 0.2f + (float)(Screenwidth / 10 * 0.5), des.elementAt(1).y  + 50.f); // Position is where the wall are plus the wall's sprite width, so the laser starts from the middle of the block

        //Load font
        myfont = Typeface.createFromAsset(getContext().getAssets(),"fonts/finalf.ttf");

        for (int i = 0; i < linelist.length; ++i)
        {
            linelist[i] = new Fingerline();
        }

        totalScore = 0; currScore = 0;
        numRate = 3;
        numHealth = 100;
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
        // filter param is to go through bilinear interpolation for getting a high quality image after scaling up
        rating = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.star), Screenwidth / 10, Screenwidth / 10, true);
        PauseB1 = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause)), (int)(Screenwidth)/15, (int)(Screenheight)/10, true), Screenwidth - 200, 30);
        PauseB2 = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause1)), (int)(Screenwidth)/15, (int)(Screenheight)/10, true), Screenwidth - 200, 30);
        myThread = new Gamethread(getHolder(), this);
        myfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
        activityTracker = activity;
        //Intent intent = new Intent();
        //intent.setClass(getContext(), Mainmenu.class);
       // activityTracker.startActivity(intent);
        // Adding the callback (this) to the surface holder to intercept events on physical surface/screen
        getHolder().addCallback(this);
        sensor = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor.registerListener(this, sensor.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
// Welcome toast message
        Toastmessage(context);
        // Alert dialog
        AlertObj = new Alert(this);
        alert = new AlertDialog.Builder(getContext());
        // Allow players to input their name
        final EditText input = new EditText(getContext());
        // Enter of text and with 'Enter' key is disabled
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Define max of 6 characters to be entered for 'Name' field
        int MaxLength = 6;
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(MaxLength);
        input.setFilters(FilterArray);
        // Setup the alert Dialog

        alert.setCancelable(false); // Cancel button
        alert.setView(input);
        //alert.setMessage("Game Over! Or is it?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            // what happens if button is pressed
            public void onClick(DialogInterface arg0, int arg1){
                Playername = input.getText().toString();
                editName.putString("PlayerUSERID", Playername);
                editName.commit();
                Intent intent = new Intent();
                intent.setClass(getContext(), Scorepage.class);
                // scorepage
                activityTracker.startActivity(intent);
            }
        });

        // shared preferences
        SharedPrefName = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        editName = SharedPrefName.edit();
        Playername = "Player1";
        Playername = SharedPrefName.getString("PlayerUSERID", "DEFAULT");
        SharedPrefScore = getContext().getSharedPreferences("UserScore", Context.MODE_PRIVATE);
        editScore = SharedPrefScore.edit();
        HighScores = 0;
        HighScores = SharedPrefScore.getInt("UserScore", 0);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){
        // Create the thread
        if (!myThread.isAlive()){
            myThread = new Gamethread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
        soundManager.PlayBGM();
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        // Destroy the thread
        if (myThread.isAlive()){
            myThread.startRun(false);
            stopVibrate();
            soundManager.StopBGM();
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
        sensor.unregisterListener(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        values = event.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void SensorMove()
    {
        // The following is to put a 4 wall border so object cannot move out of screen, can use spatial partioning to check if testX is in 1st, 2nd , 3rd quadrant of screen
        float testX, testY;
        testX = ballX + (values[1] * ((System.currentTimeMillis() - lastTime))) / 1000; // delay of some sort
        testY = ballY + (values[0] * ((System.currentTimeMillis() - lastTime))) / 1000;

        // ball is going out of the screen in Xaxis
        if (testX <= ball.getWidth() / 2 || testX >= Screenwidth - ball.getWidth() / 2)
        {// ball is within the screen in Yaxis
            if (testY > ball.getHeight() / 2 && testY < Screenwidth - ball.getHeight() / 2)
            {
                ballY = testY;
            }
        }
        // ball is going out of the screen in Yaxis
        else if (testY <= ball.getHeight() / 2 || testY >= Screenheight - ball.getHeight() / 2)
        {// ball is within the screen in Xaxis
            if (testX > ball.getWidth() / 2 && testX < Screenwidth - ball.getWidth() / 2)
            {
                ballX = testX;
            }
        }
        // Both axis of ball is within screen
        // Move ball in both axis independant of the frame
        else
        {
            ballX = testX;
            ballY = testY;
        }
    }

    private void RenderRating(Canvas canvas)
    {
        switch(numRate) {
            case 3:
                canvas.drawBitmap(rating, 28, Screenheight - 700, null);
                canvas.drawBitmap(rating, 78, Screenheight - 700, null);
                canvas.drawBitmap(rating, 128, Screenheight - 700, null);
                break;
            case 2:
                canvas.drawBitmap(rating, 28, Screenheight - 700, null);
                canvas.drawBitmap(rating, 78, Screenheight - 700, null);
                break;
            case 1:
                canvas.drawBitmap(rating, 28, Screenheight - 700, null);
                break;
            default:

                break;
        }
    }

    private void RenderHealthbar(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE); // FILL AND STROKE
        canvas.drawRect(Screenwidth / 20 + 5, Screenheight/20 - 5, 4*Screenwidth/20, 2 * Screenheight/25, paint);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(Screenwidth / 20 + 8, Screenheight/20, Screenwidth/20 + numHealth, 2 * Screenheight/25 - 5, paint);
    }

    public void RenderPause(Canvas canvas)
    {
        if (isPaused)
            canvas.drawBitmap(PauseB1.getBitmap(), PauseB1.getX(), PauseB1.getY(), null);
        else
            canvas.drawBitmap(PauseB2.getBitmap(), PauseB2.getX(), PauseB2.getY(), null);
    }

    // Render enemies at their position with no paint
    public void RenderEnemy(Canvas canvas)
    {
        for (int i = 0; i < enemy_list.length; ++i)
            if (enemy_list[i] != null && enemy_list[i].getActive())
                canvas.drawBitmap(enemy_list[i].getBitmap(), enemy_list[i].getPos().x, enemy_list[i].getPos().y, null);
    }

    public void RenderComputer(Canvas canvas)
    {
        if (mainframe != null && mainframe.getActive())
            canvas.drawBitmap(mainframe.getBitmap(), mainframe.getPos().x, mainframe.getPos().y, null);
    }

    public void RenderObjects(Canvas canvas)
    {
        for (int i = 0; i < wall_list.length; ++i)
            if (wall_list[i] != null && wall_list[i].getActive())
                canvas.drawBitmap(wall_list[i].getBitmap(), wall_list[i].getPos().x, wall_list[i].getPos().y, null);
        if (kill_line != null && kill_line.getActive())
            canvas.drawBitmap(kill_line.getBitmap(), kill_line.getPos().x, kill_line.getPos().y, null);
    }

    public void RenderGameplay(Canvas canvas) { // edit
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }

        canvas.drawBitmap(scalebg, bgX, bgY, null);
        canvas.drawBitmap(scalebg, bgX, bgY + Screenheight, null);
        canvas.drawBitmap(ball,ballX, ballY, null);
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
        RenderRating(canvas);
        RenderHealthbar(canvas);
        RenderPause(canvas);
        RenderEnemy(canvas);
        RenderComputer(canvas);
        RenderObjects(canvas);
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
                // currentTimeMillis is wall-clock time of machine, time passed since program ran
                circle.update(System.currentTimeMillis()); // Update sprite animation
                collisionEnemies();
                for (int i = 0; i < enemy_list.length; ++i)
                    if (enemy_list[i] != null && enemy_list[i].getActive())
                        enemy_list[i].Update(dt);
                    else if (enemy_list[i] == null)
                        enemy_list[i] = (Enemy)InitialiseEnemy();
                spawn_timer.Update(dt);
                int inactive_index = GetInactiveEnemy(enemy_list);
                if (inactive_index >= 0) {
                    if (spawn_timer.can_run) {
                        enemy_list[inactive_index].setActive(true);
                        spawn_timer.can_run = false;
                    }
                }
                SensorMove();
                deltaTime = dt;
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

    // Tells you whether grid is being touched, if not, -1 is returned, else return the index of grid node that is being touched
    public int touchGrid(MotionEvent event)
    {
        for (int i = 0; i < gridarray.length; ++i)
        {
            float x = gridarray[i].x;
            float y = gridarray[i].y;
            if(gridarray[i].spriteanimation != null && checkOnGrid(event, x, y, gridarray[i].spriteanimation.getSpriteWidth(), gridarray[i].spriteanimation.getSpriteHeight())) // Check whether finger is on grid node using its sprite width and height
            {
                return i; // which grid node is being interacted with
            }
        }
        return -1;
    }

    // Miss Tan AABB collision
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
        short X = (short)event.getX(); // Get finger positions
        short Y = (short)event.getY();
        if (fCheckCollision(x, y, width, height, X, Y, 0, 0)) // check whether finger pos is within bounds of the width and height of sprite, (grid node w, h)
        {
            return true;
        }
        return false;
    }

    // Check for collision between computer and enemies
    public void collisionEnemies()
    {
        if (mainframe != null && mainframe.getActive()) {
            for (int i = 0; i < enemy_list.length; i++) {
                if (enemy_list[i] != null) {
                    Vector2D distance_vec = enemy_list[i].position.Minus(mainframe.position);
                    float dist = distance_vec.LengthSquared();
                    float radiusSquared = 80.f;
                    if (dist <= radiusSquared) {
                        numHealth -= 50;
                        enemy_list[i] = null;
                        break;
                    }
                }
            }
            if (numHealth <= 0) {
                //Gameover conditions
                mainframe.setActive(false);
            }
        }
    }

    // Check if kill line has been crossed by enemy
    public boolean CheckCrossedKillLine(Vector2D pos, final int orientation, float dt) // Pos is the enemy pos, orientation is the way the enemy crosses the line, whether its higher, lower, further right, etc
    {
        //if (dt > 0 && dt < orientation + 1) {
            switch (orientation) {
                case 0:
                    float del_x = pos.x + dt;
                    float negdel_x = pos.x - dt;
                    float offsetY = kill_line.getBitmap().getHeight() * 0.5f * dt;
                    float offsetX = kill_line.getBitmap().getWidth() * 0.5f * dt;
                    if (/*((pos.x >= (kill_line.x - offsetX)) && (pos.x <= (kill_line.x + offsetX))) &&*/ pos.y <= kill_line.getPos().y + kill_line.getBitmap().getHeight() * 0.5f)
                        return true;
                    break;
            }
        //}
        return false;
    }

    public void getindex(final int index, int position)
    {
        finger_index[position] = index; // stores which grid node player puts his finger on
        ++INDEX; // to record where finger lays next
    }

    public final void getPattern(final int[] index)
    {
        int topleft, topright, bottomleft, bottomright;
        int current = 1;
        topleft = topright = bottomleft = bottomright = -1;
        for (int i = 0; i < index.length; ++i)
        {
            switch (index[i])
            {
                case INDEX_TOP_LEFT:
                    topleft = current;
                    break;
                case INDEX_TOP_RIGHT:
                    topright = current;
                    break;
                case INDEX_BOTTOM_LEFT:
                    bottomleft = current;
                    break;
                case INDEX_BOTTOM_RIGHT:
                    bottomright = current;
                    break;
                default:
                    break;
            }
            ++current;
        }
        if (topleft == 1)
        {
            if (bottomright == 2) // can be taken away to save cost, this is just for efficency, cuz player confirm plus chop will go through top left or bottom right one
            {
                if (bottomleft == 3)
                    finger_pattern = Enemy.PATTERN.TYPE_DOWN;
                else if(topright == 3)
                    finger_pattern = Enemy.PATTERN.TYPE_RIGHT;
            }
        }
        else if (topright == 1)
        {
            if (bottomright == 3)
                finger_pattern = Enemy.PATTERN.TYPE_DOWN;
            else if(topleft == 3)
                finger_pattern = Enemy.PATTERN.TYPE_LEFT;
        }
        else if (bottomleft == 1)
        {
            if (topleft == 3)
                finger_pattern = Enemy.PATTERN.TYPE_UP;
            else if (bottomright == 3)
                finger_pattern = Enemy.PATTERN.TYPE_RIGHT;
        }
        else if (bottomright == 1)
        {
            if (topright == 3)
                finger_pattern = Enemy.PATTERN.TYPE_UP;
            else if (bottomleft == 3)
                finger_pattern = Enemy.PATTERN.TYPE_LEFT;
        }
    }
    // Returns an enemy in enemy list that is in-active and ready to spawn, must check for -1(invalid index) to prevent crash
    public int GetInactiveEnemy(Enemy[] arr)
    {
        for (int i = 0; i < arr.length; ++i)
            if (arr[i] != null && !arr[i].getActive())
                return i;
        return -1; // none are inactive so return invalid index
    }
    private Objects InitialiseEnemy() {
        int rand_i = 0;
        Enemy enemy = new Enemy(enemyorigin.x, enemyorigin.y, NUM_WAYPOINT, false);
        for (int j = 0; j < des.size(); ++j)
            enemy.waypoints[j] = des.elementAt(j);
        rand_i = (int) (Math.random() * 10);
        if (rand_i % 2 == 0 && rand_i < 5)
            enemy.type = Enemy.PATTERN.TYPE_DOWN;
        else if (rand_i % 2 != 0 && rand_i < 5)
            enemy.type = Enemy.PATTERN.TYPE_UP;
        else if (rand_i % 2 == 0 && rand_i > 5)
            enemy.type = Enemy.PATTERN.TYPE_LEFT;
        else
            enemy.type = Enemy.PATTERN.TYPE_RIGHT;

        if (enemy.type == Enemy.PATTERN.TYPE_DOWN)
            enemy.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patterndown)), Screenwidth / 10, Screenwidth / 10, true);
        else if (enemy.type == Enemy.PATTERN.TYPE_UP)
            enemy.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternup)), Screenwidth / 10, Screenwidth / 10, true);
        else if (enemy.type == Enemy.PATTERN.TYPE_LEFT)
            enemy.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternleft)), Screenwidth / 10, Screenwidth / 10, true);
        else
            enemy.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.patternright)), Screenwidth / 10, Screenwidth / 10, true);
        return enemy;
    }

    public void startVibrate()
    {
        long pattern[] = {0, 50, 0};
        // vibrate to this pattern, 1st value is how long before vibrate, 2nd is duration, 3rd is duration to alternate on and off and -1 is don't repeat
        m_vibrator.vibrate(pattern, -1);
        Log.v("Vibrate", "the phone is vibrating!");
    }

    public void stopVibrate()
    {
        m_vibrator.cancel();
    }

    public void Toastmessage(Context context)
    {
        text = "WElcome to NGON.";
        toastTime = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, toastTime);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){ // edit
        short X = (short)event.getX(); // t
        short Y = (short)event.getY();
        int action = event.getAction(); // check for the action of touch

        // get the current grid node being touched
        currIndex = touchGrid(event);
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                if (currIndex >= 0 && linelist[numLines].isDrawn == false)
                {
                    /*The following is for grid system*/
                    gridarray[currIndex].spriteanimation = circle; // init the node sprite to the animated one
                    gridarray[currIndex].active = true; // grid node is now active, if active, do not allow player to reconnect again
                    // Draw the lines
                    linelist[numLines].setStartX((short)(gridarray[currIndex].x + gridarray[currIndex].spriteanimation.getSpriteWidth() / 2));
                    linelist[numLines].setStartY((short)(gridarray[currIndex].y + gridarray[currIndex].spriteanimation.getSpriteHeight() / 2));
                    linelist[numLines].isDraw = true; // Has started drawing is true
                    // store this node
                    prevIndex = currIndex;
                    /**/
                    /*The following is for checking of patterns or reading what is drawn on grid*/
                    if (INDEX < 4)
                    getindex(currIndex, INDEX); // store finger pos
                    /**/
                    invalidate();
                }
                /* Pause game */
                if (isPaused && CheckCollision(PauseB1.getX(), PauseB1.getY(), PauseB1.getWidth(), PauseB1.getHeight(), X, Y, 0, 0))
                {
                    isPaused = false;
                    myThread.pause();

                }
                else
                {
                    isPaused = true;
                    myThread.unPause();
                }
                /**/
                break;
            case MotionEvent.ACTION_MOVE:
                //if (currIndex < 0)
                    //break;
                // if one of the nodes are being touched and it is not the previous one, meaning the finger has moved out of the prev node position, and if line is not drawn
                if (currIndex >= 0 && !linelist[numLines].isDrawn)
                {
                    if (!gridarray[currIndex].active) // The grid node finger is at has not been activated yet so player is allowed to connect a line to it
                    {
                        gridarray[currIndex].spriteanimation = circle;
                        gridarray[currIndex].active = true;
                        if (numLines != 0) {
                            linelist[numLines].setStart(linelist[numLines - 1].getEndX(), linelist[numLines - 1].getEndY()); // Draw the line start at where the last line ended
                        }
                        linelist[numLines].setEndX((short) (gridarray[currIndex].x + gridarray[currIndex].spriteanimation.getSpriteWidth() / 2)); // draw the line end in the middle of the next node
                        linelist[numLines].setEndY((short) (gridarray[currIndex].y + gridarray[currIndex].spriteanimation.getSpriteHeight() / 2));
                        linelist[numLines].isDrawn = true; // set its used to true, must reset the line when all fingers are up later
                        ++numLines; // Prepare to draw next line
                        prevIndex = currIndex;
                        if (INDEX < 4) {
                            getindex(currIndex, INDEX);
                            getPattern(finger_index);
                        }
                        invalidate();
                    }
                }
                else if (/*prevIndex < 0 &&*/ !linelist[numLines].isDrawn) // If previous position was at a grid
                {
                    linelist[numLines].setEnd(X, Y); // line end is drawn at wherever finger is, since the finger has gone out of the old node but has not gone to a new node
                }
                break;
            case MotionEvent.ACTION_UP:
                // Check for intersection of lines after finger is up so as to calculate score
                /*if (currIndex < 0)
                    break;*/
               for (int i = 0; i < linelist.length; ++i)
               {
                   for (int j = 0; j < linelist.length; ++j)
                   {
                       if(i != j)
                       {
                            intersectCheck = new intersection(linelist[i],linelist[j]);
                            Vector2D temp1, temp2, temp3, temp4;
                            temp1 = new Vector2D(linelist[i].getStartX(),linelist[i].getStartY());
                            temp2 = new Vector2D(linelist[i].getEndX(),linelist[i].getEndY());
                            temp3 = new Vector2D(linelist[j].getStartX(),linelist[j].getStartY());
                            temp4 = new Vector2D(linelist[j].getEndX(),linelist[j].getEndY());
                            if(intersectCheck.algebraNonsense())
                            {
                                currScore++;
                                toast.show();
                                if (finger_pattern != Enemy.PATTERN.TYPE_MAX_TYPE)
                                {
                                    for (int e = 0; e < enemy_list.length; ++e)
                                        if (enemy_list[e] != null && enemy_list[e].getActive() && enemy_list[e].type == finger_pattern) {
                                            if (CheckCrossedKillLine(enemy_list[e].position, 0, deltaTime))
                                                enemy_list[e] = null;
                                        }
                                }
                                INDEX = 0;
                                finger_pattern = Enemy.PATTERN.TYPE_MAX_TYPE;
                                finger_index = new int[4];
                                //HighScores = 30;
                                //editScore.putInt("UserScore", HighScores);
                                //showAlert = true;
                                //if (showAlert == true) {
                                    //AlertObj.RunAlert();
                                   // showAlert = false;
                                //}
                            }
                           else
                            {
                                startVibrate();
                            }
                        }
                       else
                        {
                        }
                    }
                }
                // Reset all the lines for next pattern to draw
                for (int i = 0; i < linelist.length; ++i)
                    {
                        linelist[i].setEnd(linelist[i].getStartX(), linelist[i].getStartY());
                        linelist[i].isDraw = false;
                        linelist[i].isDrawn = false;
                    }
                // Reset all the sprites to default, non-animated sprite and reset active
                for(int i = 0; i < gridarray.length; i++)
                {
                    gridarray[i].spriteanimation = circleP;
                    gridarray[i].active = false;
                }
                // total score is added up
                totalScore += currScore;
                currScore = 0;
                numLines = 0;
                prevIndex = -1;
                currIndex = -1;
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
