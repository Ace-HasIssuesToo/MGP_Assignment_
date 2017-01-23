package com.sidm.mymgp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

// Mainmenu class is an activity(inherited) and therefore interacts with the player and it uses the interface OnClickListener to make sure OnClick is called when this View is touched
public class Mainmenu extends Activity implements OnClickListener {

    // Buttons for start and help, which is also a view, which is also an object, should contain a reference to xml one since UI xml objects are views
    private Button btn_start;
    private Button btn_help;

    @Override
    // initialize activity
    protected void onCreate(Bundle savedInstanceState) {
        // call parent class's onCreate, super allows access to overridden methods
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title so can be full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        // Set the UI layout for mainmenu to follow
        setContentView(R.layout.mainmenu);

        btn_start = (Button)findViewById(R.id.btn_start); // btn from xml is found and given to btn_start
        btn_start.setOnClickListener(this); // make button wait for click and when i press it, call OnClick
        btn_help = (Button)findViewById(R.id.btn_help);
        btn_help.setOnClickListener(this);
    }

    // *Must* be implemented since this is an interface function
    public void onClick(View v)
    {
        // Sort of the glue or transition between activities
        Intent intent = new Intent();
        if (v == btn_start)
        {
            intent.setClass(this, Gamepage.class);
        }
        else if (v == btn_help)
        {
            //intent.setClass(this, Helppage.class);
            intent.setClass(this, Splashpage.class);
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
