package com.sidm.mymgp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

// Options class is an activity(inherited) and therefore interacts with the player and it uses the interface OnClickListener to make sure OnClick is called when this View is touched
public class Options extends Activity implements OnClickListener {

    private RadioGroup radioGroup_soundEffects;
    private RadioButton radioButton_SoundEffectsOn, radioButton_SoundEffectsOff;

    // Buttons for start and help, which is also a view, which is also an object, should contain a reference to xml one since UI xml objects are views
     Button btn_backToMainagain;

    @Override
    // initialize activity
    protected void onCreate(Bundle savedInstanceState) {
        // call parent class's onCreate, super allows access to overridden methods
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title so can be full-screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        // Set the UI layout for Options to follow
        setContentView(R.layout.options);

        radioGroup_soundEffects = (RadioGroup) findViewById(R.id.settings_sound);
        radioButton_SoundEffectsOn = (RadioButton)findViewById(R.id.soundeffecton);
        radioButton_SoundEffectsOff = (RadioButton)findViewById(R.id.soundeffectoff);

        btn_backToMainagain = (Button)findViewById(R.id.btn_backToMainAgain);
        btn_backToMainagain.setOnClickListener(this);
        radioGroup_soundEffects.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        RadioButton selectedButton = (RadioButton)findViewById(checkedId);
                        if(selectedButton.getId() == R.id.soundeffectoff)
                        {
                            Toast.makeText(getApplicationContext(), "Sound Effects Off", Toast.LENGTH_SHORT).show();

                            if(Gamepanelsurfaceview.getSoundToggle() == true)
                            {
                                Gamepanelsurfaceview.setSoundToggle(false);
                            }
                        }
                    }
                }
        );
    }

    public void onSoundEffectsOnButtonClick(View view)
    {
        Toast.makeText(getApplicationContext(), "Sound Effects On", Toast.LENGTH_SHORT).show();
        if(Gamepanelsurfaceview.getSoundToggle() == false)
        {
            Gamepanelsurfaceview.setSoundToggle(true);
        }
    }

    // *Must* be implemented since this is an interface function
    public void onClick(View v)
    {
        // Sort of the glue or transition between activities
        Intent intent = new Intent();
       if (v == btn_backToMainagain)
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
