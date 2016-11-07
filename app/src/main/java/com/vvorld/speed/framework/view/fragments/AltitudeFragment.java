package com.vvorld.speed.framework.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvorld.speed.R;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.utils.FunctionUtils;
import com.vvorld.speed.common.utils.LocationUtility;
import com.vvorld.speed.framework.helper.view.SpeedSplashTextView;
import com.vvorld.speed.framework.view.activity.MainActivity;

/**
 * Created by vaibhav.singhal on 6/6/2016.
 *
 * Used to show altitude of flight.
 * Description of override method is provided in BaseFragment as comment.
 */
public class AltitudeFragment  extends BaseFragment  {
    private ImageView mFlightImage;
    private Button mBtnAltitude;
    private SpeedSplashTextView mTxtAltitudeValue;
    private SpeedSplashTextView mTxtAboveText;
    private Resources mResources;
    private LocationUtility mLocationUtility = null;
    private MainActivity mActivity;
    // to know whether on response of gps enable we need to start animation or not
    private boolean isAltitudeButtonPressed = false;
    // to show animation on text view only once
    private boolean isAboveTextAnimationShown = false;
    int mAltitudeShowDelay = 0;

    @Override
    protected int intializeLayoutId() {
        return R.layout.fragment_altitude_layout;
    }

    @Override
    protected void initViews(View mFragmentView) {
        if(getActivity() instanceof  MainActivity) {
            mActivity = (MainActivity) getActivity();
            mResources = mActivity.getResources();
        }
        mLocationUtility = LocationUtility.getInstance(getActivity());
        mFlightImage = (ImageView) mFragmentView.findViewById(R.id.imgFlightImage);
        mBtnAltitude = (Button) mFragmentView.findViewById(R.id.btnAnimate);
        mTxtAltitudeValue = (SpeedSplashTextView) mFragmentView.findViewById(R.id.txtAltitudeValue);
        mTxtAboveText = (SpeedSplashTextView) mFragmentView.findViewById(R.id.txtAbove);
        mBtnAltitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!SpeedApplication.getAppInstance().isGpsPermissionGranted())
                {
                    mActivity.haveGpsPermission(new MainActivity.IGpsPermission() {
                        @Override
                        public void onGpsPermissionResult(boolean isGranted) {
                            if(isGranted)
                                if(mLocationUtility!=null)
                                {
                                    altitudeButtonClick();
                                }
                        }
                    });
                }
                else {
                    altitudeButtonClick();
                }

            }
        });
        mTxtAltitudeValue.setTypeface(FunctionUtils.getTypeFace(getActivity()));
        mTxtAboveText.setTypeface(FunctionUtils.getTypeFace(getActivity()));
        mTxtAboveText.setTextSize(30);
        mTxtAboveText.setCharacterDelay(150);
        mTxtAltitudeValue.setCharacterDelay(150);

    }

    private void altitudeButtonClick()
    {
        // setting variable to false as this method is called when button is pressed that means dialog not visible, to be safe setting it to false.
        FunctionUtils.isGPSEnableDialogVisible = false;
        if (FunctionUtils.isGPSEnabled(getActivity()) && mBtnAltitude.getTag() == null) {
            // show altitude value
            mBtnAltitude.setTag(true);
            mBtnAltitude.setText(mResources.getString(R.string.stop_updates));
            startAnimation();

            /* to handle if gps is off after altitude button was pressed i.e we were receiving updates but then user turn off gps.
                so that we will not show enable gps dialog if text on button is stop updates
            */
        } else if(mBtnAltitude.getText().equals(mActivity.getResources().getString(R.string.show_altitude))){
            isAltitudeButtonPressed = true;
            FunctionUtils.showDialogToEnableGPS(getActivity());
        }
        else if(mBtnAltitude.getText().equals(mActivity.getResources().getString(R.string.stop_updates))){
            mBtnAltitude.setTag(null);
            mBtnAltitude.setText(mResources.getString(R.string.show_altitude));
            mTxtAltitudeValue.setVisibility(View.GONE);
            mTxtAboveText.setVisibility(View.GONE);
            // so that we can stop location updates from handler if button not pressed again within specified time
            mActivity.requestLocationUpdateAndScheduleRunnable();
        }

    }
    private void startAnimation(){
        Animation.AnimationListener animListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mFlightImage.setVisibility(View.VISIBLE);
                mBtnAltitude.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFlightImage.setVisibility(View.INVISIBLE);
                mBtnAltitude.setVisibility(View.VISIBLE);
                mLocationUtility.startLocationUpdates();
                // to hide animated object after animation
                animation.cancel();
                mFlightImage.setAnimation(null);
                isAboveTextAnimationShown = false;
            }
        };
        mFlightImage.requestLayout();
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_bottom_to_top);
        animation.setAnimationListener(animListener);
        animation.setDuration(1000);
        mFlightImage.startAnimation(animation);
    }
    /**
     * Will be called from notifyLocationChange() of MainActivity to update location object in fragment.
     * notifyLocationChange() of MainActivity is called from onLocationChanged() of LocationUtility class.
     * @param location
     */
    @Override
    public void setLocation(Location location){
        mLocation = location;
        if(mBtnAltitude != null && mBtnAltitude.getTag() != null) {
            // receiving updates fast so skip one update after displaying one update.
            if(mAltitudeShowDelay >= 1){
                mAltitudeShowDelay = 0;
                return;
            }
            mAltitudeShowDelay++;
            if (mLocation.hasAltitude()) {
                mTxtAltitudeValue.setText(mLocation.getAltitude() + " " + mActivity.getResources().getString(R.string.meters));
                mTxtAltitudeValue.animateText(mLocation.getAltitude() + " " + mActivity.getResources().getString(R.string.meters));
                mTxtAboveText.setText(getString(R.string.aboveSeaLevel));
                mTxtAboveText.setVisibility(View.VISIBLE);
                mTxtAboveText.setTypeface(FunctionUtils.getTypeFace(getActivity()));
                mTxtAboveText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mTxtAltitudeValue.setVisibility(View.VISIBLE);
                if(!isAboveTextAnimationShown){
                    isAboveTextAnimationShown = true;
                    mTxtAboveText.animateText(getString(R.string.aboveText));
                }
            } else {
                mTxtAltitudeValue.setVisibility(View.GONE);
                mTxtAboveText.setText(getString(R.string.altitudeNotAvailable));
                mTxtAboveText.setTypeface(Typeface.DEFAULT);
                mTxtAboveText.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray_400));
                mTxtAboveText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void notifyGPSEnableResponse(int resultCode) {
        if(isAltitudeButtonPressed && resultCode == Activity.RESULT_OK) {
           altitudeButtonClick();
            isAltitudeButtonPressed = false;
        }
    }
    public Button getAlitudeButtonView(){
        return mBtnAltitude;
    }
}
