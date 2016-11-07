package com.vvorld.speed.framework.view.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Location;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vvorld.speed.R;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.utils.FunctionUtils;
import com.vvorld.speed.common.utils.LocationUtility;
import com.vvorld.speed.common.utils.LogUtil;
import com.vvorld.speed.framework.view.activity.InstructionsActivity;
import com.vvorld.speed.framework.view.activity.MainActivity;
import com.vvorld.speedview.GaugeView;

/**
 * Created by vaibhav.singhal on 6/6/2016.
 *
 * Will show speed of flight.
 */
public class SpeedFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SpeedFragment.class.getName();
    private GaugeView mGaugeViewKilometerPerHour, mGaugeViewMeterPerSecond;
    private RadioButton mRadioBtnMeterPerSecond;
    private RadioButton mRadioBtnKilometerPerHour;
    private TextView mTxtUnit,mTxtSpeedMagnitude;
    private LocationUtility mLocationUtility = null;
    // if getTag return null, don't show speed otherwise show
    private Button mBtnShowSpeed;
    private Resources mResources;
    private MainActivity mActivity;
    // so that we know requestEnable GPS is called from init view methos or on button press: show speed
    private boolean mIsShowUpdatesOnShowSpeedBtnPress = false;


    /**
     * will return layout id for this fragment.
     * @return
     */
    @Override
    protected int intializeLayoutId() {
        return R.layout.fragment_speed_layout;
    }
    /*
        This method is used to get reference of views
     */
    @Override
    protected void initViews(View mFragmentView) {
        if(getActivity() instanceof MainActivity){
            mActivity = (MainActivity)getActivity();
            mResources = mActivity.getResources();
        }

        mLocationUtility = LocationUtility.getInstance(getActivity());
        mGaugeViewKilometerPerHour = (GaugeView) mFragmentView.findViewById(R.id.gauge_viewKPH);
        mGaugeViewMeterPerSecond = (GaugeView) mFragmentView.findViewById(R.id.gauge_viewMPS);
        mRadioBtnMeterPerSecond = (RadioButton) mFragmentView.findViewById(R.id.radioButtonMPS);
        mRadioBtnKilometerPerHour = (RadioButton)mFragmentView.findViewById(R.id.radioButtonKPH);
        mBtnShowSpeed = (Button)mFragmentView.findViewById(R.id.btn_getSpeed);
        // as launching speed fragment from instructions screen as well.
        if(getActivity() instanceof InstructionsActivity){
            mBtnShowSpeed.setClickable(false);
            mRadioBtnMeterPerSecond.setClickable(false);
            mRadioBtnKilometerPerHour.setClickable(false);
        }
        //set on click listener on radio button
        mRadioBtnKilometerPerHour.setOnClickListener(this);
        mRadioBtnMeterPerSecond.setOnClickListener(this);
        mBtnShowSpeed.setOnClickListener(this);
        //Unit of speed meter : Kph or Mps
        mTxtUnit = (TextView) mFragmentView.findViewById(R.id.txt_unit);
        // to show digital text like in calculator
        mTxtUnit.setTypeface(FunctionUtils.getTypeFace(getActivity()));
        mTxtSpeedMagnitude = (TextView) mFragmentView.findViewById(R.id.txt_speed);
        mTxtSpeedMagnitude.setTypeface(FunctionUtils.getTypeFace(getActivity()));
        mTxtSpeedMagnitude.setVisibility(View.GONE);
        //set initial value on meter to zero
        setValueInMeterToZeroOn();


    }

    @Override
    public void onStart() {
        super.onStart();
        // so that don't show enable gps dialog on instructions activity.
        if(SpeedApplication.getAppInstance().isGpsPermissionGranted() && mActivity != null) {
            requestToEnableGPS(true);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.radioButtonKPH || id == R.id.radioButtonMPS){
            toggleView(id);
        }
        else if(id == R.id.btn_getSpeed){
            mIsShowUpdatesOnShowSpeedBtnPress = true;
            // if gps is off & get tag equal to null, as that gps dialog will not be shown when user click on stop update & gps is off.
           checkPermissionAndShowSpeed();
        }
    }

    /**
     * Will show KPH or MPS meter on the basis of option selected.
     * @param viewId
     */
    private void toggleView(int viewId){
        if(viewId == R.id.radioButtonKPH){
            mGaugeViewKilometerPerHour.setVisibility(View.VISIBLE);
            mGaugeViewMeterPerSecond.setVisibility(View.GONE);
            mTxtUnit.setText(getActivity().getResources().getString(R.string.kph));
        }
        else{
            mGaugeViewKilometerPerHour.setVisibility(View.GONE);
            mGaugeViewMeterPerSecond.setVisibility(View.VISIBLE);
            mTxtUnit.setText(getActivity().getResources().getString(R.string.mps));
        }
        /*was facing issue if in magnitude textview speed in kph is shown & we select mph for some time value of speed in khp was shown
         so need to update magnitude in text view as soon as user switch between mps or kph
         */
        if(mBtnShowSpeed.getTag()!= null && mLocation != null && mLocation.hasSpeed()){
            if(viewId == R.id.radioButtonKPH){
                int speedInKPH = FunctionUtils.convertMPSToKPH(mLocation.getSpeed());
                mTxtSpeedMagnitude.setText(String.format("%d", speedInKPH));
            }
            else{
                int speedInMPS = (int) mLocation.getSpeed();
                mTxtSpeedMagnitude.setText(String.format("%d", speedInMPS));
            }
        }
    }

    /**
     * Will be called from MainActivity from notifyLocationChange().
     * notifyLocationChange() of MainActivity is called from onLocationChanged() of LocationUtility class.
     * @param location
     */
    @Override
    public void setLocation(Location location){
       mLocation = location;
        if(mLocation != null && mBtnShowSpeed != null && mBtnShowSpeed.getTag() != null){
            // if has speed, call showSpeed() other wise set zero in magnitude & start animation
            if(mLocation.hasSpeed()) {
                showSpeed();
            }
            else{
                if(mTxtSpeedMagnitude != null) {
                    mTxtSpeedMagnitude.setText(String.format("%d", 0));
                    if (mTxtSpeedMagnitude.getVisibility() == View.GONE) {
                        startBlinkAnimationOnSpeedMagnitudeTextView();
                    }
                }
            }
        }
    }

    @Override
    public void notifyGPSEnableResponse(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                LogUtil.i(TAG, "User agreed to make required location settings changes.");
                //this method
                if(mIsShowUpdatesOnShowSpeedBtnPress) {
                    checkPermissionAndShowSpeed();
                }
                break;
            case Activity.RESULT_CANCELED:
                LogUtil.i(TAG, "User chose not to make required location settings changes.");
                break;
        }

    }

    private void showSpeed(){
        int speedInMPS = (int) mLocation.getSpeed();
        mGaugeViewMeterPerSecond.setTargetValue(speedInMPS);
        int speedInKPH = FunctionUtils.convertMPSToKPH(mLocation.getSpeed());
        mGaugeViewKilometerPerHour.setTargetValue(speedInKPH);

        LogUtil.d(SpeedFragment.class.getSimpleName()," Speed In MPS :" + speedInMPS + " SpeedInKPH : " + speedInKPH);
        //Set magnitude of speed in textview
        if(mBtnShowSpeed.getTag()!=null) {
            // no need to set animation every time showSpeed() is called set it once.
            if(mTxtSpeedMagnitude.getVisibility() == View.GONE) {
               startBlinkAnimationOnSpeedMagnitudeTextView();
            }

            if (mTxtUnit.getText().toString().equalsIgnoreCase(getString(R.string.kph)))
                mTxtSpeedMagnitude.setText(String.format("%d", speedInKPH));
            else if (mTxtUnit.getText().toString().equalsIgnoreCase(getString(R.string.mps)))
                mTxtSpeedMagnitude.setText(String.format("%d", speedInMPS));
        }
        else {
            mTxtSpeedMagnitude.clearAnimation();
            mTxtSpeedMagnitude.setVisibility(View.GONE);
        }
    }
    private void startBlinkAnimationOnSpeedMagnitudeTextView(){
        mTxtSpeedMagnitude.setVisibility(View.VISIBLE);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        mTxtSpeedMagnitude.startAnimation(myFadeInAnimation);
    }

    private void setValueInMeterToZeroOn(){
        mGaugeViewMeterPerSecond.setTargetValue(0);
        mGaugeViewKilometerPerHour.setTargetValue(0);
        //> Clear blink animation from speed magnitude textview
        mTxtSpeedMagnitude.clearAnimation();
        if(mTxtSpeedMagnitude.getAnimation()!=null)
            mTxtSpeedMagnitude.setAnimation(null);
        mTxtSpeedMagnitude.setVisibility(View.GONE);
    }
    private void requestToEnableGPS(boolean isCalledFromInitViews){
        // if gps not enabled showing dialog to enable it. This method automatically check state of gps.
        FunctionUtils.showDialogToEnableGPS(getActivity());
    }

    /**
     * Check permission first before showing speed / using location
     */
    private void checkPermissionAndShowSpeed()
    {
        if(!SpeedApplication.getAppInstance().isGpsPermissionGranted())
        {
            mActivity.haveGpsPermission(new MainActivity.IGpsPermission() {
                @Override
                public void onGpsPermissionResult(boolean isGranted) {
                    if(isGranted)
                    {
                        if(mLocationUtility!=null)
                        {
                            //mActivity.startLocationServices();
                            setShowSpeedBtnTag();
                        }
                    }
                }
            });
        }
        else
            setShowSpeedBtnTag();

    }

    /**
     * TO change state of show speed button.
     * Will be called on press of show speed button.
     */
    private void setShowSpeedBtnTag(){

        //don't change state of show speed button if this method is called in response of enable GPs dialog fron init views.
        if(!mIsShowUpdatesOnShowSpeedBtnPress){
            return;
        }
        // setting variable to false as this method is called when button is pressed that means dialog not visible, to be safe setting it to false.
        FunctionUtils.isGPSEnableDialogVisible = false;
        // second condition to handle case when gps is off & we press stop updates then don't show gps enable dialog
        if(!FunctionUtils.isGPSEnabled(mActivity) && mBtnShowSpeed.getTag() == null){
            FunctionUtils.showDialogToEnableGPS(mActivity);
            return;
        }

        if(mBtnShowSpeed.getTag() == null){
            mLocationUtility.startLocationUpdates();
            mBtnShowSpeed.setTag(true);
            mBtnShowSpeed.setText(mResources.getString(R.string.stop_updates));
        }
        else{
            mBtnShowSpeed.setTag(null);
            mBtnShowSpeed.setText( mResources.getString(R.string.show_speed));
            setValueInMeterToZeroOn();
            mIsShowUpdatesOnShowSpeedBtnPress = false;
            // so that we can stop location updates from handler if button not pressed again within specified time
            /*
            A timer will start from below method that will check after fixed time if either of show altitude or show speed not
            pressed will sstop location updates.
             */
            mActivity.requestLocationUpdateAndScheduleRunnable();
            showRateUsDialog();

        }
    }
    public Button getShowSpeedButtonView(){
        return mBtnShowSpeed;
    }


}