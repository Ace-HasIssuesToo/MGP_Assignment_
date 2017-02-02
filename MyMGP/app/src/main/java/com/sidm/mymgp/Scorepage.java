package com.sidm.mymgp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 153942B on 1/12/2017.
 */

public class Scorepage extends Activity implements View.OnClickListener {
    // Buttons for start and help, which is also a view, which is also an object
    private Button btn_back;
    private Button btn_fbLogin;
    boolean loggedin = false;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    // ShareDialog
    ProfilePictureView profile_pic;
    String user_name;
    List<String> PERMISSIONS = Arrays.asList("publish_actions");

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

//    public Scorepage(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
    /*Drawing of score*/
    Paint star_paint;
    myview draw_view;
    /**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // hide top bar
        //FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(new myview(this));
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

        /*star rating*/
        star_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        draw_view = new myview(this);
        /**/

        /* facebook */
        //btn_fbLogin = (LoginButton) findViewById(R.id.fb_login_button);
        //btn_fbLogin.setOnClickListener(this);
        //profile_pic = (ProfilePictureView)findViewById(R.id.picture);
        //callbackManager = CallbackManager.Factory.create();
            /*Access tokens*/
//        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                if (currentAccessToken == null){
//                    // User logged out
//                    profile_pic.setProfileId("");
//                }
//                else
//                {
//                    profile_pic.setProfileId(Profile.getCurrentProfile().getId());
//                    user_name = Profile.getCurrentProfile().getName();
//                }
//            }
//        };
//        accessTokenTracker.startTracking();
//            /**/
//        loginManager = LoginManager.getInstance();
//        loginManager.logInWithPublishPermissions(this, PERMISSIONS);
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                profile_pic.setProfileId(Profile.getCurrentProfile().getId());
//                //shareScore();
//            }
//
//            @Override
//            public void onCancel() {
//                System.out.println("Login attempt canceled.");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                System.out.println("Login attempt failed.");
//            }
//        });
        /**/
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

    // To share info on facebook
    public void shareScore() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder().setBitmap(image).setCaption(user_name + " has just played Webpon with a highscore of " + numRate + '!').build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareApi.share(content, null);
    }
    // Use callback manager to manage login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    class myview extends View {

        public myview(Context context) {
            super(context);

            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            RenderRating(canvas);
            //canvas.drawBitmap();
        }
    }
}
