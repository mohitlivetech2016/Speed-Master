package com.vvorld.speed.framework.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vvorld.speed.R;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.utils.FunctionUtils;
import com.vvorld.speed.common.utils.LocationUtility;
import com.vvorld.speed.common.utils.SpeedAnimations;
import com.vvorld.speed.common.value.Constants;
import com.vvorld.speed.framework.helper.view.SpeedSplashTextView;

/**
 * Created by vaibhav.singhal on 6/9/2016.
 */
public class SplashActivity extends Activity {

    private static final long ANIMATION_DURATION = 700;

    private Handler mHandler;
    private Runnable myRunnable;

    private SpeedSplashTextView mSpeedSplashTextView;
    private ImageView img_splash;
    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */

    protected static final String TAG = "MainActivity";
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSpeedSplashTextView = (SpeedSplashTextView)findViewById(R.id.txt_splash_name);
        img_splash = (ImageView) findViewById(R.id.img_splash);
        mHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
              decideNextScreen();
            }
        };

        //> animation
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                SpeedAnimations.setScalingSplashAnimation(img_splash, ANIMATION_DURATION, new SpeedAnimations.IAnimationListener() {
                    @Override
                    public void onAnimationEnd() {

                        mSpeedSplashTextView.setCharacterDelay(150);
                        mSpeedSplashTextView.animateText(getString(R.string.app_name));

                        if (mHandler != null && myRunnable != null) {
                            mHandler.postDelayed(myRunnable, Constants.SPLASH_TIME_OUT);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mHandler != null && myRunnable != null) {
            mHandler.removeCallbacks(myRunnable);
        }
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        if (mHandler != null && myRunnable != null) {
            mHandler.removeCallbacks(myRunnable);
        }
        super.onBackPressed();
    }

    /**
     * Show instructions screen or main activity;
     */
    private void decideNextScreen(){
        int instructionCount = SpeedApplication.getPrefs().getInstructionScreenCount();
        if(instructionCount < Constants.INSTRUCTION_SCREEN_MAX_COUNT){
            SpeedApplication.getPrefs().setInstructionScreenCount(++instructionCount);
            Intent i = new Intent(SplashActivity.this,
                    InstructionsActivity.class);
            startActivity(i);

        }
        else{
            Intent i = new Intent(SplashActivity.this,
                    MainActivity.class);
            startActivity(i);
        }
        finish();
    }
}
