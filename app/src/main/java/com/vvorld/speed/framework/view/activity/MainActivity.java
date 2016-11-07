package com.vvorld.speed.framework.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.TabLayout;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.vvorld.speed.R;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.listeners.ILocationUpdates;
import com.vvorld.speed.common.prefs.Prefs;
import com.vvorld.speed.common.utils.FunctionUtils;
import com.vvorld.speed.common.utils.LocationUtility;
import com.vvorld.speed.common.utils.LogUtil;
import com.vvorld.speed.common.value.Constants;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements ILocationUpdates{

    private static final String TAG = MainActivity.class.getName();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private LocationUtility mLocationUtility;

    private Handler mMyHandler;
    private ProgressDialog mSearchingForGPSDialog;
    private TextView mTxtSearchingForGPS;
    private IGpsPermission mIGpsPermission;

    public interface IGpsPermission
    {
        void onGpsPermissionResult(boolean isGranted);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationUtility = LocationUtility.getInstance(this);
        mLocationUtility.setOnLocationUpdateListener(this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTxtSearchingForGPS = (TextView) findViewById(R.id.txtSearchingForGPS);
        mSearchingForGPSDialog = new ProgressDialog(this);
        mSearchingForGPSDialog.setMessage(getResources().getString(R.string.searchingForGPS));
        mSearchingForGPSDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {
                mTxtSearchingForGPS.setVisibility(View.VISIBLE);
            }
        });
        setupViewPager();
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mMyHandler = new Handler();
        //Assigns the ViewPager to TabLayout.
        mTabLayout.setupWithViewPager(mViewPager);
        setTouchListenerOnInstructionsLayout(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gotItButtonClicked(MainActivity.this);
                return true;
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR_DEVICE_HASH")
                .build();
        mAdView.loadAd(adRequest);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public boolean haveGpsPermission(IGpsPermission gpsPermission)
    {
        if (!SpeedApplication.getAppInstance().isGpsPermissionGranted()) {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.Activity_CONSTANTS.GPS_PERM_REQ_CODE);

            mIGpsPermission = gpsPermission;
        }
        else
            return true;
        return false;
    }

    public boolean haveGpsPermission()
    {
        return haveGpsPermission(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mShowSearchingForGPSDialogReceiver,
                new IntentFilter(Constants.ACTION_SHOW_DIALOG_SEARCHING_FOR_GPS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestLocationUpdatesOnClientConnectedReceiver,
                new IntentFilter(Constants.ACTION_REQUEST_LOCATION_UPDATES_ON_GOOGLE_API_CLIENT));
        if(!haveGpsPermission()){
            // do nothing inside if, above method will take care.
        }
        else{
            if(mLocationUtility!=null) {
                if (!mLocationUtility.isGoogleClientConnected()) {
                      mLocationUtility.connectGoogleClient();
                }
                // this method required as when app start again after onstop get location updates for some time, will be called from onConnect as well through local  broadcast
                else {
                    requestLocationUpdateAndScheduleRunnable();
                }
            }
        }

    }
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named ACTION_REQUEST_LOCATION_UPDATES_ON_GOOGLE_API_CLIENT is broadcasted.
    private BroadcastReceiver mRequestLocationUpdatesOnClientConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            requestLocationUpdateAndScheduleRunnable();
        }
    };

    @Override
    protected void onDestroy() {
        if(mLocationUtility!=null) {
            mLocationUtility.googleClientDisconnection();
        }
        if(mSearchingForGPSDialog != null){
            mSearchingForGPSDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(mLocationUtility!=null) {
            mLocationUtility.stopLocationUpdates();
        }
        // Unregister receiver.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestLocationUpdatesOnClientConnectedReceiver);
        // Unregister receiver.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShowSearchingForGPSDialogReceiver);
        if(mMyHandler != null){
            mMyHandler.removeCallbacks(mStopLocationUpDatesIfButtonNotPressedRunnable);
        }
        dismissSearchingForGPSDialog();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_contact_us:
                openGmailIntent(Constants.AppConstants.APP_EMAIL,getString(R.string.contact_email_header), getString(R.string.contact_email_content));
                break;
            case R.id.icon_share:
                ShareCompat.IntentBuilder.from(MainActivity.this)
                        .setType("text/plain")
                        .setSubject(getString(R.string.share_flight_speed_app))
                        .setText(getString(R.string.share_content)
                                + Constants.AppConstants.APP_STORE_LINK + getPackageName())
                        .setChooserTitle(getString(R.string.share_flight_speed_app))
                        .startChooser();

                break;
            case R.id.icon_rate_us:
                FunctionUtils.rateThisApp(this);
                break;
            case R.id.action_info:
               showInstructionLayout(this);
                break;
        }
        return true;
    }

    @Override
    public void notifyLocationChange(Location location) {
        //getSpeed () gives speed in meters/second over ground.
        mTxtSearchingForGPS.setVisibility(View.GONE);
        dismissSearchingForGPSDialog();
        if(location != null) {
            mSpeedFragment.setLocation(location);
            mAltitudeFragment.setLocation(location);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.Activity_CONSTANTS.GPS_PERM_REQ_CODE:
                boolean isLocationPermission = false, showRationale = true;
                if(grantResults!=null) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(!FunctionUtils.isGPSEnabled(this)){
                            FunctionUtils.showDialogToEnableGPS(this);
                        }
                        isLocationPermission = true;
                        mLocationUtility = LocationUtility.getInstance(this);
                        mLocationUtility.setOnLocationUpdateListener(this);
                        SpeedApplication.getAppInstance().setIsGpsPermissionGranted(true);
                    }
                    else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
                            showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                            if (!showRationale) {
                                // user denied flagging NEVER ASK AGAIN
                                // you can either enable some fall back,
                                // disable features of your app
                                // or open another dialog explaining
                                // again the permission and directing to
                                // the app setting
                                LogUtil.d(MainActivity.class.getSimpleName(),"Never ask again as clicked");
                                FunctionUtils.showPreviouslyDeniedDialog(MainActivity.this);
                            }
                            else
                                FunctionUtils.showDialogForGPSPermissionDenied(MainActivity.this);
                        }
                    }
                }
                if(mIGpsPermission!=null)
                    mIGpsPermission.onGpsPermissionResult(isLocationPermission);
                break;
        }
    }



    /**
     * Email Intent to send email along with Subject and text
     */

    private void openGmailIntent(String emailId, String subject, String body)
    {
        if(FunctionUtils.isPackageExist(Constants.AppConstants.GMAIL_PACKAGE,this)) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType(Constants.AppConstants.EMAIL_CONTENT_TYPE);
            sendIntent.setClassName(Constants.AppConstants.GMAIL_PACKAGE, Constants.AppConstants.GMAIL_PACKAGE_COMPOSE_ACTIVITY);
            sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(sendIntent);
        }
        else {
            ShareCompat.IntentBuilder.from(this)
                    .setType(Constants.IntentConstants.EMAIL_INTENT)
                    .addEmailTo(emailId)
                    .setSubject(subject)
                    .setText(body)
                    .setChooserTitle(getString(R.string.email_chooser_title))
                    .startChooser();
        }
    }


    /**
     * Will be called in response of GPS enable dialog, startResolutionForResult called from FunctionUtils.checkLocationSettings() .
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case Constants.REQUEST_CHECK_SETTINGS:
                FunctionUtils.isGPSEnableDialogVisible = false;
                if(resultCode == RESULT_OK) {
                    notifyFragmentsGPSEnableRequestResult(resultCode);
                }
                else if(resultCode == RESULT_CANCELED){
                    FunctionUtils.showDialogIfUserDenyToTurnGPS(this);
                }
                break;
        }
    }

    /**
     * Notify fragment of GPS enable dialog of user selected choice
     * @param resultCode
     */
    private void notifyFragmentsGPSEnableRequestResult(int resultCode){
        if(mAltitudeFragment.isFragmentVisible()){
            mAltitudeFragment.notifyGPSEnableResponse(resultCode);
        }
        else {
            mSpeedFragment.notifyGPSEnableResponse(resultCode);
        }
    }


    /**
     * Request Location updates onStart if either of show speed or show altitude
     * button pressed before onStop called as we are stopping location updates onStop.
     */
    public void requestLocationUpdateAndScheduleRunnable(){
        //no need to as location updates already on.
        if(LocationUtility.isLocationUpdatesOn){
            return;
        }
        mLocationUtility.startLocationUpdates();
        //cancel any previous same runnable scheduled.
        mMyHandler.removeCallbacks(mStopLocationUpDatesIfButtonNotPressedRunnable);
        /*start location update as soon as app start but if either of
         show speed or show altitude not pressed within 3 min stop location updates.
         */
        mMyHandler.postDelayed(mStopLocationUpDatesIfButtonNotPressedRunnable, Constants.TIME_DELAY_THREE_MIN);

    }
    private Runnable mStopLocationUpDatesIfButtonNotPressedRunnable = new Runnable() {
        public void run() {
            if ((mSpeedFragment != null && mSpeedFragment.getShowSpeedButtonView() != null
                    && mSpeedFragment.getShowSpeedButtonView().getTag() == null) &&
                    (mAltitudeFragment != null && mAltitudeFragment.getAlitudeButtonView() != null
                            && mAltitudeFragment.getAlitudeButtonView().getTag() == null)) {
                mLocationUtility.stopLocationUpdates();
            }
        }
    };
    private Runnable mChangeDialogTextRunnable = new Runnable() {
        public void run() {
            if (mSearchingForGPSDialog != null && mSearchingForGPSDialog.isShowing()) {
                mSearchingForGPSDialog.setMessage(getResources().getString(R.string.takingLonger));
            }
        }
    };
    private BroadcastReceiver mShowSearchingForGPSDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // show dialog only if gps is available.
            if(FunctionUtils.isGPSEnabled(context)) {
                showSearchingGPSDialog();
            }
        }
    };
    public void showSearchingGPSDialog(){
        if(mSearchingForGPSDialog != null && mSpeedFragment.getShowSpeedButtonView() != null && mAltitudeFragment.getAlitudeButtonView()!= null ) {
            if (mSpeedFragment.getShowSpeedButtonView().getTag() != null || mAltitudeFragment.getAlitudeButtonView().getTag() != null) {
                if (!mSearchingForGPSDialog.isShowing()) {
                    mSearchingForGPSDialog.setMessage(getResources().getString(R.string.searchingForGPS));
                    mSearchingForGPSDialog.show();
                    mMyHandler.postDelayed(mChangeDialogTextRunnable, Constants.TIME_DELAY_TEN_SECONDS);
                }
            }
        }
    }
    private void dismissSearchingForGPSDialog(){
        if(mSearchingForGPSDialog != null && mSearchingForGPSDialog.isShowing()){
            removeCallBack();
            mSearchingForGPSDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(rel_instruction.getVisibility() == View.VISIBLE){
            backButtonPressed(this);
        }
        else {
            removeCallBack();
            super.onBackPressed();
        }
    }
    private void removeCallBack(){
        if(mMyHandler != null){
            mMyHandler.removeCallbacks(mChangeDialogTextRunnable);
        }
    }
}
