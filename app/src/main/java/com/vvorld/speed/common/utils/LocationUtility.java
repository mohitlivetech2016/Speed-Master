package com.vvorld.speed.common.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.Status;
import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.listeners.ILocationUpdates;
import com.vvorld.speed.common.value.Constants;


public class LocationUtility implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private static LocationRequest mLocationRequest;

    /**
     * Provides the entry point to Google Play services.
     */
    private static GoogleApiClient mGoogleApiClient;
    private static Context mContext;
    private Location mCurrentLocation;
    private Location mLastLocation =null;
    private static LocationUtility mLocationUtility = null;
    // value will be set to true from startLocation updates & false from stopLocationUpdates
    public static boolean isLocationUpdatesOn = false;
    private static LocationUtility mInstance;

    //> make object of interface ILocationUpdates
    private ILocationUpdates mILocationUpdatesListener=null;

    private void LocationUtility(){
        mInstance = this;
    }
    public synchronized static LocationUtility getInstance(Context context){
        if(mLocationUtility == null){
            mLocationUtility = new LocationUtility();
        }
        mContext = context;
        if(mLocationRequest == null){
            createLocationRequest();
        }
        mLocationUtility.getGoogleApiClientInstance();
        /*
            tough we are calling this method from getGoogleApiClientInstance
            but can be case when client instance not null but its not connected.
         */
        if(!isGoogleClientConnected()) {
            connectGoogleClient();
        }
        return mLocationUtility;
    }

    public void setOnLocationUpdateListener(ILocationUpdates iLocationUpdates)
    {
        this.mILocationUpdatesListener = iLocationUpdates;
    }
    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private static  void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(INTERVAL);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {

        //> get last location separately
        if (SpeedApplication.getAppInstance().isGpsPermissionGranted()) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);

                //start location updates from main activity by sending local broad cast
                Intent intent = new Intent(Constants.ACTION_REQUEST_LOCATION_UPDATES_ON_GOOGLE_API_CLIENT);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    public void startLocationUpdates() {
        if(mLocationRequest == null){
            createLocationRequest();
        }
        //TODO: check for permission if not given fire local broadcast & return
        if(LocationUtility.isLocationUpdatesOn == false) {
            LocationUtility.isLocationUpdatesOn = true;
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        Intent intent = new Intent(Constants.ACTION_SHOW_DIALOG_SEARCHING_FOR_GPS);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

    }
    public static void connectGoogleClient()
    {
        // Connect it if its not connected and already not trying to connect
        if(!isGoogleClientConnected() || !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * should only be called from onDestroy of main activity.
     */
    public void googleClientDisconnection()
    {
        if(isGoogleClientConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        LogUtil.e("LocationUtils", "Lat : " + mCurrentLocation.getLatitude() + " Lon : " + mCurrentLocation.getLongitude());
        if(mILocationUpdatesListener!=null) {
            mILocationUpdatesListener.notifyLocationChange(mCurrentLocation);
        }
        else{
            try {
                throw new Exception("Why the hell location update listener is null.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Location getLocation(){
        return mCurrentLocation;
    }

    public double getLatitude(){
        if(mCurrentLocation==null)
            return 0;
        else
            return mCurrentLocation.getLatitude();
    }

    public double getLongitude(){
        if(mCurrentLocation==null)
            return 0;
        else
            return mCurrentLocation.getLongitude();
    }


    public void stopLocationUpdates() {
        if(isGoogleClientConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        LocationUtility.isLocationUpdatesOn = false;
    }


    public float distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results){

        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

        return results[0];
    }

    public float distanceBetween(Location currentLocation,Location endLocation){

      return currentLocation.distanceTo(endLocation);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    // return true when google play services are available
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int status = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {

            return false;
        }
    }

    public static boolean isGoogleClientConnected(){
        if(mGoogleApiClient != null){
            if(mGoogleApiClient.isConnected()) {
                return true;
            }
            return false;
        }
        else{
            return false;
        }
    }
    public Location getLastLocation() {
        return mLastLocation;
    }


    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }
    /**
    * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
    * LocationServices API.
    */
    public synchronized GoogleApiClient getGoogleApiClientInstance(){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return mGoogleApiClient;
    }

}
