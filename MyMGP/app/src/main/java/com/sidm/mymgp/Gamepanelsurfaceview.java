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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.nfc.Tag;
import android.os.Vibrator;
import android.provider.MediaStore;
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
    // The level manager
    LevelManager lvl_manager;
    // Create an array of nodes to form a grid
    private Grid[] gridarray;
    //Create a computer mainframe
    private MainFrameOBJ mainframe;
    float comPosX, comPosY;
    final int NUM_WAYPOINT = 25; // How many waypoints are placed for enemy to follow
    private Enemy[] enemy_list;
    private Ally[] ally_list;

    /*Timer for entity spawning*/
    SpawnTimer spawn_timer;
    SpawnTimer ally_spawnTimer;
    /**/
    /*Wall objects and kill-line*/
    public Objects[] wall_list;
    public Objects kill_line;
    float prev_enemyX; // Stores previous enemy position to make enemies line up
    float prev_enemyY; // Stores previous enemy position to make enemies line up
    float prev_allyX; // Stores previous enemy position to make enemies line up
    float prev_allyY; // Stores previous enemy position to make enemies line up
    /**/

    // Hardcoded according to cirX, cirY, cirX1, cirY1... These are used to improve readability in TouchEvent()
    public static final int INDEX_TOP_LEFT = 0;
    public static final int INDEX_TOP_RIGHT = 1;
    public static final int INDEX_BOTTOM_LEFT = 2;
    public static final int INDEX_BOTTOM_RIGHT = 3; // not final so it can be changed in the constructor during cirX declaraction

    /*shared preferences to pass selected level number from LevelPage to GamePlay page*/
    public static int CURRENT_LEVEL;
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
    private Fingerline[] linelist;
    int numLines = 0;
    int currIndex = 0;
    int prevIndex = 0;
    /*Scoring*/
    static int stat_totalScore;
    static int stat_currScore;
    /**/
    /*Scene switching*/
    static boolean b_EndLevel;
    /**/
    // Variables for FPS
    public float FPS;
    float deltaTime;
    //long dt;
    // Checker class for intersection between lines
    intersection intersectCheck;
    Typeface myfont;
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
    CharSequence winText;
    int winToastTime;
    Toast winToast; // Guess you could say windows is toast. badumtss
    // Timer for how long before change scene
    SpawnTimer time_before_end;
    // Alert dialog
    public boolean showAlert = true;
    AlertDialog.Builder alert = null;
    private Alert AlertObj; // based on Alert.java
    // Settings meant to be shared across all files
    // Interface, access and modify preference data using getSharedPreferences(String, int). There is a single instance of this class that all clients share. Modifications to the preferences must go through an SharedPreferences.Editor object
    SharedPreferences SharedPrefName;
    SharedPreferences.Editor editName;
    SharedPreferences SharedPrefScore;
    SharedPreferences.Editor editScore;
    SharedPreferences sharedPref_level;

    // Names to be shared
    String Playername;

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

    static boolean soundToggle;

    SoundPool SoundPool;

    AudioAttributes Attributes;

    int soundID;

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
        sharedPref_level = getContext().getSharedPreferences("SelectedLevel", Context.MODE_PRIVATE);
        CURRENT_LEVEL = sharedPref_level.getInt("SelectedLevel", 0);
        LevelManager.m_levellist.add(0, new LevelManager());
        LevelManager.m_levellist.add(1, new LevelManager());
        //LevelManager.m_levellist.elementAt(CURRENT_LEVEL - 1) = new LevelManager();
        // The computer mainframe position, which is also the "final destination"(badumtss) of enemies
        comPosX = Screenwidth * 0.8f; comPosY = Screenheight * 0.2f;
        LevelManager.m_levellist.elementAt(0).Init(9, new Vector2D(Screenwidth * 0.15f, Screenheight * 0.6f), 5, 100.f, new Vector2D(Screenwidth * 0.15f, Screenheight * 0.6f), 1, 150.f); // starting position);
        LevelManager.m_levellist.elementAt(1).Init(4, new Vector2D(0, comPosY), 20, 110.f, new Vector2D(0, comPosY), 5, 160.f);
        //lvl_manager = LevelManager.m_levellist.elementAt(CURRENT_LEVEL - 1);
        lvl_manager = LevelManager.m_levellist.elementAt(CURRENT_LEVEL - 1);
        // getResources() returns you the app res folder, we then decode the image using its R.id
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.matrix);
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.pokeball);
        scalebg = Bitmap.createScaledBitmap(bg, Screenwidth, Screenheight, true);// For bigger/smaller version of bg
        circlePressed = new Spriteanimation(BitmapFactory.decodeResource(getResources(),R.drawable.techcirclespritesheet), 288, 96, 3, 3);
        // for grid circle and line
        gridarray = new Grid[lvl_manager.m_numNode];
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

        // Initialise enemies
        lvl_manager.SetEnemiesDestination(CURRENT_LEVEL, Screenwidth, Screenheight, new Vector2D(comPosX, comPosY));

        enemy_list = new Enemy[lvl_manager.m_numEnemies];
        int rand_i = 0;
        prev_enemyX = lvl_manager.m_enemyStartPos.x;
        prev_enemyY = lvl_manager.m_enemyStartPos.y;
        for (int i = 0; i < enemy_list.length; ++i)
        {
            enemy_list[i] = new Enemy(prev_enemyX, prev_enemyY, NUM_WAYPOINT, false, lvl_manager.m_enemySpeed);
            if (CURRENT_LEVEL == 1)
                prev_enemyX -= Screenwidth * 0.1f;
            else if (CURRENT_LEVEL == 2)
                prev_enemyY -= Screenheight * 0.1f;
            for (int j = 0; j < lvl_manager.m_des.size(); ++j)
                enemy_list[i].waypoints[j] = lvl_manager.m_des.elementAt(j);
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
        linelist = new Fingerline[lvl_manager.m_numNode + 1];
        lvl_manager.m_fingerindex = new int[4];
        spawn_timer = new SpawnTimer(0.0f, 5, 0);
        ally_spawnTimer = new SpawnTimer(0.0f, 4, 0);
        time_before_end = new SpawnTimer(0.0f, 2, 0.0f);
        ally_list = new Ally[lvl_manager.m_numAlly];
        prev_allyX = lvl_manager.m_allyStartPos.x;
        prev_allyY = lvl_manager.m_allyStartPos.y;
        for (int i = 0; i < ally_list.length; ++i)
        {
            ally_list[i] = new Ally(prev_allyX, prev_allyY, NUM_WAYPOINT, false);
            if (CURRENT_LEVEL == 1)
                prev_allyX -= Screenwidth * 0.1f;
            else if (CURRENT_LEVEL == 2)
                prev_allyY -= Screenheight * 0.1f;
            for (int j = 0; j < lvl_manager.m_des.size(); ++j)
                ally_list[i].waypoints[j] = lvl_manager.m_des.elementAt(j);
            rand_i = (int)(Math.random() * 10);
            if (rand_i % 2 == 0 && rand_i < 5)
                ally_list[i].type = Ally.PATTERN.TYPE_1;
            else if (rand_i % 2 != 0 && rand_i < 5)
                ally_list[i].type = Ally.PATTERN.TYPE_2;
            else
                ally_list[i].type = Ally.PATTERN.TYPE_3;

            if (ally_list[i].type == Ally.PATTERN.TYPE_1)
                ally_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green1)), Screenwidth / 10, Screenwidth / 10, true);
            else if (ally_list[i].type == Ally.PATTERN.TYPE_2)
                ally_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green2)), Screenwidth / 10, Screenwidth / 10, true);
            else
                ally_list[i].bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green3)), Screenwidth / 10, Screenwidth / 10, true);
        }

        Bitmap ComputerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.computer);
        mainframe = new MainFrameOBJ(ComputerSprite, comPosX, comPosY);

        wall_list = new Objects[5];
        for (int i = 0; i < wall_list.length; ++i)
        {
            wall_list[i] = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.wall)), Screenwidth / 10, Screenheight / 10, true), 5, 5);
            wall_list[i].position.x = Screenwidth * 0.2f;
            wall_list[i].position.y = Screenheight * 0.5f - (Screenheight / 10) * i;
        }
        if (CURRENT_LEVEL == 1) {
            kill_line = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.laser)), (int) (Screenwidth * 0.6f - Screenwidth * 0.2f), Screenheight / 2, true), 5, 5);
            kill_line.position.Set(Screenwidth * 0.2f + (Screenwidth / 10 * 0.5f), lvl_manager.m_des.elementAt(1).y + 50.f);
        }
            else if (CURRENT_LEVEL == 2) {
            kill_line = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.laservertical)), Screenheight / 2, (int) (Screenwidth * 0.6f - Screenwidth * 0.2f), true), 5, 5);
            kill_line.position.Set(lvl_manager.m_des.elementAt(1).x - 600.f, lvl_manager.m_des.elementAt(1).y); // Position is where the wall are plus the wall's sprite width, so the laser starts from the middle of the block
        }
        //Load font
        myfont = Typeface.createFromAsset(getContext().getAssets(),"fonts/finalf.ttf");

        for (int i = 0; i < linelist.length; ++i)
        {
            linelist[i] = new Fingerline();
        }

        stat_totalScore = 0; stat_currScore = 0;
        numRate = 3;
        numHealth = 100;
        // filter param is to go through bilinear interpolation for getting a high quality image after scaling up
        PauseB1 = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause_512)), (int)(Screenwidth)/10, (int)(Screenheight)/10, true), Screenwidth - 200, 30);
        PauseB2 = new Objects(Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.pause_512)), (int)(Screenwidth)/10, (int)(Screenheight)/10, true), Screenwidth - 200, 30);
        myThread = new Gamethread(getHolder(), this);
        myfont = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
        // Make the GamePanel focusable so it can handle events
        setFocusable(true);
        activityTracker = activity;
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
        // create a shared file, is shared among all callers of getSharedPreferences, MODE_PRIVATE is default operation
        SharedPrefName = getContext().getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        // Allows modification to the SharedPreferences object
        editName = SharedPrefName.edit();
        Playername = "Player1";
        // Get the value stored by SharedPreferences, if nothing, return DEFAULT
        Playername = SharedPrefName.getString("PlayerUSERID", "DEFAULT");
        SharedPrefScore = getContext().getSharedPreferences("UserScore", Context.MODE_PRIVATE);
        editScore = SharedPrefScore.edit();
        numRate = 0;
        numRate = SharedPrefScore.getInt("UserScore", 0);
        b_EndLevel = false;

        //SoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        //Attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        //SoundPool = new SoundPool.Builder().setAudioAttributes(Attributes).setMaxStreams(2).build();

    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder){
        // Create the thread
        if (!myThread.isAlive()){
            myThread = new Gamethread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
        if(soundToggle == true)
            {soundManager.PlayBGM();}
        else
        {}
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

    private void RenderHealthbar(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL); // FILL AND STROKE
        canvas.drawRect(Screenwidth / 20 + 8, Screenheight/20, Screenwidth/20 + numHealth, 2 * Screenheight/25, paint);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(Screenwidth / 20 + 8, Screenheight/20, Screenwidth/20 + numHealth, 2 * Screenheight/25, paint);
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

    // Render allies at their position with no paint
    public void RenderAlly(Canvas canvas)
    {
        for (int i = 0; i < ally_list.length; ++i)
            if (ally_list[i] != null && ally_list[i].getActive())
                canvas.drawBitmap(ally_list[i].getBitmap(), ally_list[i].getPos().x, ally_list[i].getPos().y, null);
    }

    public void RenderComputer(Canvas canvas)
    {
        if (mainframe != null && mainframe.getActive())
            canvas.drawBitmap(mainframe.getBitmap(), mainframe.getPos().x, mainframe.getPos().y, null);
    }

    public void RenderObjects(Canvas canvas)
    {
        if (kill_line != null && kill_line.getActive())
            canvas.drawBitmap(kill_line.getBitmap(), kill_line.getPos().x, kill_line.getPos().y, null);
        for (int i = 0; i < wall_list.length; ++i)
            if (wall_list[i] != null && wall_list[i].getActive())
                canvas.drawBitmap(wall_list[i].getBitmap(), wall_list[i].getPos().x, wall_list[i].getPos().y, null);
    }

    public void RenderComputerHealthbar(Canvas canvas) {

        Paint paint = new Paint();
        paint.setARGB(255, 255, 100, 100);
        paint.setStrokeWidth(10);
        //paint.setStyle(Paint.Style.STROKE); // FILL_AND_STROKE
        //canvas.drawRect(Screenwidth/20 + 5, Screenheight/25 - 5, 4 * Screenwidth/20, 2 * Screenheight/25, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mainframe.getPos().x, mainframe.getPos().y - 5, mainframe.getPos().x + (mainframe.getWidth() * (numHealth / 100)), mainframe.getPos().y + mainframe.getHeight() * 0.5f, paint);


        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        //canvas.drawRect(Screenwidth/20 + 8, Screenheight/25, Screenwidth/20 + numHealth, 2 * Screenheight/25 - 5, paint);
        canvas.drawRect(mainframe.getPos().x, mainframe.getPos().y - 5, (mainframe.getPos().x + mainframe.getWidth()), mainframe.getPos().y + mainframe.getHeight() * 0.5f, paint);

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
        RenderTextOnScreen(canvas, "T-SCORE: " + stat_totalScore, 110, (int)(Screenheight * 0.9f), 80, 0, 0, 255, 255);
        //RenderHealthbar(canvas);
        RenderPause(canvas);
        RenderAlly(canvas);
        RenderEnemy(canvas);
        RenderComputer(canvas);
        RenderObjects(canvas);
        RenderComputerHealthbar(canvas);
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

                   collisionAllies();
                for (int i = 0; i < enemy_list.length; ++i)
                    if (enemy_list[i] != null && enemy_list[i].getActive()) {
                        enemy_list[i].Update(dt);
                    }
                    else if (enemy_list[i] == null)
                        enemy_list[i] = (Enemy)InitialiseEnemy();
                int randomNum = (int)(Math.random() * 10);
                    for (int i = 0; i < ally_list.length; ++i)
                    if (ally_list[i] != null && ally_list[i].getActive())
                        ally_list[i].Update(dt);
                    else if (ally_list[i] == null)
                        ally_list[i] = (Ally) InitialiseAlly();
                spawn_timer.Update(dt, randomNum);
                ally_spawnTimer.Update(dt, -1);
                /* Object Pooling */ // currently this is causing enemies to repeatedly spawn
                // if the player does not kill the last one. If you want the game to end after the last enemy hits mainframe, just add --m_numenemies to collisionenemies()
                if (lvl_manager.m_numEnemies > 0 && !GetEnemyActiveStatus(enemy_list)) {
                    int inactive_index = GetInactiveEnemy(enemy_list);
                    if (inactive_index >= 0) {
                        if (spawn_timer.can_run) {
                            enemy_list[inactive_index].setActive(true);
                            spawn_timer.can_run = false;
                        }
                    }
                }
                /**/
                /* Making them spawn according to seconds*/
                int inactive_index = lvl_manager.m_enemySpawnIndex;
                if (inactive_index >= 0) {
                    if (spawn_timer.can_run) {
                        enemy_list[inactive_index].setActive(true);
                        spawn_timer.can_run = false;
                        --lvl_manager.m_enemySpawnIndex;
                    }
                }
                /**/
                /* Making allies spawn according to seconds*/
                inactive_index = lvl_manager.m_allySpawnIndex;
                if (inactive_index >= 0) {
                    if (ally_spawnTimer.can_run) {
                        ally_list[inactive_index].setActive(true);
                        ally_spawnTimer.can_run = false;
                        --lvl_manager.m_allySpawnIndex;
                    }
                }
                /**/
                /* End game if condition of all enemies defeated is satisfied */
                if (lvl_manager.m_numEnemies == 0) {
                    b_EndLevel = true;
                    winToast.show();
                }

                if (b_EndLevel) {
                    time_before_end.Update(dt, -1);
                    if (time_before_end.can_run) {
                        GameState = 1;
                        time_before_end.can_run = false;
                    }
                }
                /**/
                SensorMove();
                deltaTime = dt;
            }
            break;
            case 1: {
                if (b_EndLevel) {
                    time_before_end.Update(dt, -1);
                    if (time_before_end.can_run) {
                            /*Check if game is over, calculate rating*/
                            if (stat_totalScore > 5)
                                numRate = 1;
                            else if (stat_totalScore > 10)
                                numRate = 2;
                            else if (stat_totalScore > 20)
                                numRate = 3;
                            else
                                numRate = 0;
                            /**/
                            editScore.putInt("UserScore", numRate);
                            editScore.commit();
                            if (showAlert == true) {
                                AlertObj.RunAlert();
                                showAlert = false;
                            }
                            SensorMove();
                            deltaTime = dt;
                    }
                }
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
            case 1:
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
                        startVibrate();
                        numHealth -= 50;
                        enemy_list[i] = null;
                        break;
                    }
                }
            }
            if (numHealth <= 0) {
                //Gameover conditions
                mainframe.setActive(false);
                b_EndLevel = true;
                GameState = 1;
                toast.show();
            }
        }
    }
    public void collisionAllies() {
        for (int i = 0; i < ally_list.length; i++) {
            float distance = ((comPosX - ally_list[i].x) * (comPosX - ally_list[i].x)) + ((comPosY - ally_list[i].y) * (comPosY - ally_list[i].y));
            if (mainframe != null && mainframe.getActive()) {
                Vector2D distance_vec = ally_list[i].position.Minus(mainframe.position);
                float dist = distance_vec.LengthSquared();
                float radiusSquared = 80.f;
                if (dist <= radiusSquared) {
                    numHealth += 50;
                    ally_list[i] = null;
                    break;
                }
            }
//            if (Math.sqrt(distance) <= 5)
//            {
//
//                break;
//            }
        }
        if (numHealth > 100) {
            // limit health to max_health
            numHealth = 100;
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
                case 1:
                    if (pos.x >= kill_line.getPos().x + kill_line.getBitmap().getWidth() * 0.5f)
                        return true;
                    break;
            }
        //}
        return false;
    }

    public void getindex(final int index, int position)
    {
        lvl_manager.m_fingerindex[position] = index; // stores which grid node player puts his finger on
        ++lvl_manager.INDEX; // to record where finger lays next
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
                    lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_DOWN;
                else if(topright == 3)
                    lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_RIGHT;
            }
        }
        else if (topright == 1)
        {
            if (bottomright == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_DOWN;
            else if(topleft == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_LEFT;
        }
        else if (bottomleft == 1)
        {
            if (topleft == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_UP;
            else if (bottomright == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_RIGHT;
        }
        else if (bottomright == 1)
        {
            if (topright == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_UP;
            else if (bottomleft == 3)
                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_LEFT;
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
    // Returns true if there is at least one enemy active or not null
    public boolean GetEnemyActiveStatus(Enemy[] arr)
    {
        for (int i = 0; i < arr.length; ++i)
            if (arr[i] != null && arr[i].getActive())
                return true;
        return false; // none are active so return invalid index
    }
    private Objects InitialiseEnemy() {
        int rand_i = 0;
        Enemy enemy = new Enemy(lvl_manager.m_enemyStartPos.x, lvl_manager.m_enemyStartPos.y, NUM_WAYPOINT, false, lvl_manager.m_enemySpeed);
        for (int j = 0; j < lvl_manager.m_des.size(); ++j)
            enemy.waypoints[j] = lvl_manager.m_des.elementAt(j);
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
    public int GetInactiveAlly(Ally[] arr)
    {
        for (int i = 0; i < arr.length; ++i)
            if (arr[i] != null && !arr[i].getActive())
                return i;
        return -1; // none are inactive so return invalid index
    }
    private Objects InitialiseAlly()
    {
        int rand_i = 0;
        Ally ally = new Ally(lvl_manager.m_allyStartPos.x, lvl_manager.m_allyStartPos.y, NUM_WAYPOINT, false);
        for (int j = 0; j < lvl_manager.m_des.size(); ++j)
            ally.waypoints[j] = lvl_manager.m_des.elementAt(j);
        rand_i = (int) (Math.random() * 10);
        if (rand_i % 2 == 0 && rand_i < 5)
            ally.type = Ally.PATTERN.TYPE_1;
        else if (rand_i % 2 != 0 && rand_i < 5)
            ally.type = Ally.PATTERN.TYPE_2;
        else
            ally.type = Ally.PATTERN.TYPE_3;

        if (ally.type == Ally.PATTERN.TYPE_1)
            ally.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green1)), Screenwidth / 10, Screenwidth / 10, true);
        else if (ally.type == Ally.PATTERN.TYPE_2)
            ally.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green2)), Screenwidth / 10, Screenwidth / 10, true);
        else
            ally.bitmap = Bitmap.createScaledBitmap((BitmapFactory.decodeResource(getResources(), R.drawable.green3)), Screenwidth / 10, Screenwidth / 10, true);
        return ally;
    }

    static public void setSoundToggle(boolean setSound)
    {
        setSound = soundToggle;
    }
    static public boolean getSoundToggle()
    {
        return soundToggle;
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
        text = "Operation failed! Systemx32 has crashed.";
        toastTime = Toast.LENGTH_SHORT;
        toast = Toast.makeText(context, text, toastTime);

        winText = "You Win! Going to highscore page.";
        winToastTime = winToast.LENGTH_SHORT;
        winToast = Toast.makeText(context, winText, winToastTime);
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
                    if (lvl_manager.INDEX < 4)
                    getindex(currIndex, lvl_manager.INDEX); // store finger pos
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
                        if (lvl_manager.INDEX < 4) {
                            getindex(currIndex, lvl_manager.INDEX);
                            getPattern(lvl_manager.m_fingerindex);
                        }
                        invalidate();
                    }
                }
                else if (/*prevIndex < 0 &&*/ !linelist[numLines].isDrawn) // If previous position was at a grid
                {
                    linelist[numLines].setEnd(X, Y); // line end is drawn at wherever finger is, since the finger has gone out of the old node but has not gone to a new node
                }
                break;
            case MotionEvent.ACTION_UP: // This is where most of the reset code are
                // Check for intersection of lines after finger is up so as to calculate score
                /*if (currIndex < 0)
                    break;*/
                int num_destroyed = 0;
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
                                if (lvl_manager.m_fingerpattern != Enemy.PATTERN.TYPE_MAX_TYPE)
                                {
                                    for (int e = 0; e < enemy_list.length; ++e)
                                        if (enemy_list[e] != null && enemy_list[e].getActive() && enemy_list[e].type == lvl_manager.m_fingerpattern) {
                                            if (CURRENT_LEVEL == 1 && CheckCrossedKillLine(enemy_list[e].position, 0, deltaTime))
                                            {
                                                enemy_list[e] = null;
                                                ++num_destroyed;
                                                stat_currScore += lvl_manager.score_multiplier * num_destroyed;
                                                ++lvl_manager.score_multiplier;
                                                --lvl_manager.m_numEnemies;
                                            }
                                            else if (CURRENT_LEVEL == 2 && CheckCrossedKillLine(enemy_list[e].position, 1, deltaTime))
                                            {
                                                enemy_list[e] = null;
                                                ++num_destroyed;
                                                stat_currScore += lvl_manager.score_multiplier * num_destroyed;
                                                ++lvl_manager.score_multiplier;
                                                --lvl_manager.m_numEnemies;
                                            }
                                        }
                                }
                                lvl_manager.score_multiplier = 1;
                                lvl_manager.INDEX = 0;
                                lvl_manager.m_fingerpattern = Enemy.PATTERN.TYPE_MAX_TYPE;
                                lvl_manager.m_fingerindex = new int[4];
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
                stat_totalScore += stat_currScore;
                stat_currScore = 0;
                numLines = 0;
                prevIndex = -1;
                currIndex = -1;
                break;
        }          return true;
    }
}
