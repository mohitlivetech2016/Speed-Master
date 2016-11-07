package com.vvorld.speed.common.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.vvorld.speed.R;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.value.Constants;
import com.vvorld.speed.framework.view.activity.MainActivity;

/**
 * Created by Vaib on 02-06-2016.
 */
public class FunctionUtils {
    /**
     * GPS is enabled or not
     * @param mContext
     * @return
     */
    // variable was added as on start speed fragment if location is disabled multiple dialogs were displayed.
    public static boolean isGPSEnableDialogVisible = false;
    public static boolean isGPSEnabled (Context mContext){
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    /**
     * Show dialog to enable GPS.
     * @param context
     */
    public static  void showDialogToEnableGPS(final Activity context)
    {
        if(isGPSEnableDialogVisible == true){
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**
         * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
         * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
         * if a device has the needed location settings.
         */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient
        isGPSEnableDialogVisible = true;
        checkLocationSettings(context, builder.build());

    }
    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     *
     *  locationSettingsRequest : Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */

    private static void checkLocationSettings(final Activity context, LocationSettingsRequest locationSettingsRequest) {

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(LocationUtility.getInstance(context).getGoogleApiClientInstance(), locationSettingsRequest);
        /**
         * The callback invoked when
         * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
         * LocationSettingsRequest)} is called. Examines the
         * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
         * location settings are adequate. If they are not, begins the process of presenting a location
         * settings dialog to the user.
         */
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        isGPSEnableDialogVisible = false;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            isGPSEnableDialogVisible = true;
                            status.startResolutionForResult(context, Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        isGPSEnableDialogVisible = false;
                        break;
                }
                // to pass Result object back to caller.
            }
        });
    }

    /**
     * Will convert speed in KPH from MPS
     * @param speedInMPS
     * @return
     */
    public static int convertMPSToKPH(float speedInMPS){
        float speedInKPH =  speedInMPS * 3.6f;
        return (int)speedInKPH;
    }
    public static Typeface getTypeFace(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/digital_7.ttf");
    }

    /**
     * To show dialog to user.
     * @param activity
     */

    public static void showDialogForGPSPermissionDenied(final MainActivity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.permissionDenied));
        dialogBuilder.setMessage(resources.getString(R.string.gpsPermissionDenied));

        dialogBuilder.setPositiveButton(resources.getString(R.string.amSure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton(resources.getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                   activity.haveGpsPermission();
                }
            });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public static void showPreviouslyDeniedDialog(final MainActivity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.permissionDenied));
        dialogBuilder.setMessage(resources.getString(R.string.never_ask_again_denied));

        dialogBuilder.setPositiveButton(resources.getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                startInstalledAppDetailsActivity(activity);
            }
        });
        dialogBuilder.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public static void showDialogIfUserDenyToTurnGPS(final Activity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.permissionDenied));
        dialogBuilder.setMessage(resources.getString(R.string.gpsTurnOnDenied));

        dialogBuilder.setPositiveButton(resources.getString(R.string.amSure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton(resources.getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                FunctionUtils.showDialogToEnableGPS(activity);
            }
        });


        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Show rate us dialog on UI
     * @param activity activity context to bind dialog
     */
    public static void showRateUsDialog(final Activity activity) {
        Resources resources = activity.getResources();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setTitle(resources.getString(R.string.rate_app));
        dialogBuilder.setMessage(resources.getString(R.string.rate_app_content));
        dialogBuilder.setPositiveButton(resources.getString(R.string.rate_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SpeedApplication.getPrefs().setNeverShowAgainClicked(true);
                rateThisApp(activity);
            }
        });
        dialogBuilder.setNegativeButton(resources.getString(R.string.no_thanks), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SpeedApplication.getPrefs().setNeverShowAgainClicked(true);
                dialog.cancel();
            }
        });

        dialogBuilder.setNeutralButton(resources.getString(R.string.remind_later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }



    /**
     * Check if the given package name exists on system
     * @param targetPackage package to get result for
     * @param activity activity / context
     * @return true if exist false otherwise
     */
    public static boolean isPackageExist(String targetPackage, Activity activity){
        PackageManager pm= activity.getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * Rate this app in google play , this intent will open google play to rate the app
     * and also maintains backstack
     */
    public static void rateThisApp(Activity activity)
    {
        Uri uri = Uri.parse(Constants.AppConstants.APP_MARKET_LINK + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Constants.AppConstants.APP_STORE_LINK + activity.getPackageName())));
        }
    }

    /**
     * Open app details screen in settings , if persmission gets denied
     * @param context activity context
     */
    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    public static void killActivity(Activity activity)
    {
        activity.finish();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
