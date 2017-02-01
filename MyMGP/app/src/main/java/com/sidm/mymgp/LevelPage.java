package com.sidm.mymgp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

// levelpage class is an activity(inherited) and therefore interacts with the player and it uses the interface OnClickListener to make sure OnClick is called when this View is touched
public class LevelPage extends Activity implements OnClickListener {

    // Buttons for start and help, which is also a view, which is also an object, should contain a reference to xml one since UI xml objects are views
    private Button btn_Level1;
    private Button btn_Level2;
    private Button btn_Level3;
    private Button btn_Level4;
    private Button btn_backToMain;

    @Override
    // initialize activity
    protected void onCreate(Bundle savedInstanceState) {
        // call parent class's onCreate, super allows access to overridden methods
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title so can be full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        // Set the UI layout for levelpage to follow
        setContentView(R.layout.levelpage);

        btn_Level1 = (Button)findViewById(R.id.btn_Level1); // btn from xml is found and given to btn_start
        btn_Level1.setOnClickListener(this); // make button wait for click and when i press it, call OnClick
        btn_backToMain = (Button)findViewById(R.id.btn_backToMain);
        btn_backToMain.setOnClickListener(this);
        btn_Level2 = (Button)findViewById(R.id.btn_Level2);
        btn_Level2.setOnClickListener(this);
        btn_Level3 = (Button)findViewById(R.id.btn_Level3);
        btn_Level3.setOnClickListener(this);
        btn_Level4 = (Button)findViewById(R.id.btn_Level4);
        btn_Level4.setOnClickListener(this);
    }

    // *Must* be implemented since this is an interface function
    public void onClick(View v)
    {
        // Sort of the glue or transition between activities
        Intent intent = new Intent();
        if (v == btn_Level1)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if (v == btn_Level2)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if (v == btn_Level3)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if (v == btn_Level4)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if (v == btn_backToMain)
        {
            intent.setClass(this, Mainmenu.class);
        }

        startActivity(intent); // Go to gamepage class
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