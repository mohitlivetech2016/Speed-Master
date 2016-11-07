package com.vvorld.speed.common.app;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.vvorld.speed.BuildConfig;
import com.vvorld.speed.R;
import com.vvorld.speed.common.prefs.Prefs;

import io.fabric.sdk.android.Fabric;

/**
 * Created by vivekjha on 01/06/16.
 */
public class SpeedApplication extends Application {

    private static SpeedApplication mInstance;
    private boolean isGpsPermissionGranted = false;
    private static Prefs prefs;


    //> for multidexing in API levels prior to lollipop
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        mInstance = this;
        // need to set it to true if permission not required.
        if(!needPermission()){
            isGpsPermissionGranted = true;
        }
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

    }
    public static SpeedApplication getAppInstance() {
        return mInstance;
    }

    /**
     * Get need for permissions
     * @return true, if SDK is greater than lollipop
     */
    public boolean needPermission(){
        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public boolean isGpsPermissionGranted() {
        // additional check if permission not required return true so that application will always work on device where permission not required.
        if(!needPermission()){
            return true;
        }
        //need to handle case if this method return false, may need to send localbraodcast to show permission dialog
        boolean isGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(isGranted){
            return true;
        }
        return false;
    }

    public void setIsGpsPermissionGranted(boolean isGpsPermissionGranted) {
        this.isGpsPermissionGranted = isGpsPermissionGranted;
    }
    public static Prefs getPrefs(){
        if(prefs == null) {
            prefs = new Prefs(getAppInstance());
        }
        return prefs;
    }



}
