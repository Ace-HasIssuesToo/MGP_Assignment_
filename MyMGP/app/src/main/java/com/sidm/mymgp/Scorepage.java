package com.sidm.mymgp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        setContentView(R.layout.scorepage);

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this); // btn is linked to this button id and when i press it, go to this
        TextView scoreText;
        scoreText = (TextView) findViewById(R.id.scoreText);
String Playername;
        int Highscores;
        SharedPrefName = getSharedPreferences("PlayerUSERID", Context.MODE_PRIVATE);
        Playername = SharedPrefName.getString("PlayerUSERID", "DEFAULT");
        SharedPrefScore = getSharedPreferences("UserScore", Context.MODE_PRIVATE);
        Highscores = SharedPrefScore.getInt("UserScore", 0);
        // Printing to score page view
        scoreText.setText(String.format(Playername + Highscores));
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
}
