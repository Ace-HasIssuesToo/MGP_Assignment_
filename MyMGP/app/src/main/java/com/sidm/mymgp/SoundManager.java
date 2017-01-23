package com.sidm.mymgp;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by User on 15/12/2016.
 */

public class SoundManager {
    private MediaPlayer BGM;
    public SoundManager(Context context)
    {
        BGM = MediaPlayer.create(context, R.raw.background_music);
    }
    public void SetBGMVolume(float leftBGMVol, float rightBGMVol)
    {
        BGM.setVolume(leftBGMVol, rightBGMVol);
    }
    public void PauseBGM()
    {
        BGM.pause();
    }
    public void ResumeBGM()
    {
        BGM.start();
    }
    public void PlayBGM()
    {
        BGM.start();
    }
    public void StopBGM()
    {
        BGM.stop();
    }
}
