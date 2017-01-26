package com.sidm.mymgp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by 153942B on 1/12/2017.
 */

public class Scorepage extends Activity implements View.OnClickListener {
    // Buttons for start and help, which is also a view, which is also an object
    private Button btn_back;

    SharedPreferences SharedPrefName;
    SharedPreferences SharedPrefScore;
    // Define Screen width and Screen height as integer
    int Screenwidth, Screenheight;
    // Image for star rating
    private Bitmap rating;
    // Amount of rating
    int numRate;
    // Canvas
    private Bitmap canvasMap;
    Canvas canvas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        setContentView(R.layout.scorepage);

        // Set information to get screen size, DisplayMetrics describe general information about display, size, density, and font scaling.
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        Screenwidth = metrics.widthPixels;
        Screenheight = metrics.heightPixels;

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this); // btn is linked to this button id and when i press it, go to this
        TextView scoreText;
        scoreText = (TextView) findViewById(R.id.scoreText);
        String Playername;
        SharedPrefName = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        Playername = SharedPrefName.getString("PlayerUSERID", "DEFAULT");
        SharedPrefScore = getSharedPreferences("UserScore", Context.MODE_PRIVATE);
        numRate = SharedPrefScore.getInt("UserScore", 0);
        // Printing to score page view
        scoreText.setText(String.format(Playername + " " + numRate));
        rating = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.star), Screenwidth / 10, Screenwidth / 10, true);
        canvasMap = Bitmap.createBitmap(Screenwidth, Screenheight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvasMap);
    }

    public void onClick(View v)
    {
        Intent intent = new Intent();
        if (v == btn_back)
        {
            intent.setClass(this, Mainmenu.class);
        }

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RenderRating(canvas);
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
}
