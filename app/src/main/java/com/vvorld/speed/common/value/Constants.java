package com.vvorld.speed.common.value;

/**
 * Created by vivekjha on 01/06/16.
 */
public class Constants {

    public static class AppConstants
    {
        public static final String APP_EMAIL = "flightspeedapp@gmail.com";
        public static final String APP_STORE_LINK = "http://play.google.com/store/apps/details?id=";
        public static final String APP_MARKET_LINK = "market://details?id=";
        public static final String GMAIL_PACKAGE = "com.google.android.gm";
        public static final String GMAIL_PACKAGE_COMPOSE_ACTIVITY = "com.google.android.gm.ComposeActivityGmail";
        public static final String EMAIL_CONTENT_TYPE = "plain/text";
    }

    public static class Activity_CONSTANTS
    {
        public static final int GPS_PERM_REQ_CODE = 123;
    }
    public static class FRAGMENT_CONSTANTS{
        public static final String FRAGMENT_SPEED = "Speed";
        public static final String FRAGMENT_ALTITUDE = "Altitude";
    }
    // end scale value kph & mps meter i.e 0 to constant defined
    public static final float END_SCALE_VALUE_KPH_METER = 1000;
    public static final float END_SCALE_VALUE_MPS_METER = 300;
    public static int SPLASH_TIME_OUT = 2500;
    public  static final int REQUEST_CHECK_SETTINGS = 0x1;
    public  static final int SPEED_FRAGMENT_POSITION = 0;
    public  static final int ALTITUDE_FRAGMENT_POSITION = 1;
    public static final int RATE_US_DIALOG_COUNT = 2;
    public static final int INSTRUCTION_SCREEN_MAX_COUNT = 2;

    public static class IntentConstants
    {
        public static final String EMAIL_INTENT = "message/rfc822";
    }
    public static final String ACTION_REQUEST_LOCATION_UPDATES_ON_GOOGLE_API_CLIENT = "requestLocationUpdatesOnClientCOnnect";
    public static final String ACTION_SHOW_DIALOG_SEARCHING_FOR_GPS = "showDialogSearchingForGPS";
    public static final String[] perms = {"android.permission.ACCESS_FINE_LOCATION"};
    //in milliseconds: 1 sec = 1000 milliseconds
    public static final int TIME_DELAY_THREE_MIN = 3* 1000 * 60;
    public static final int TIME_DELAY_TEN_SECONDS = 10 * 1000;
    public static final int TEXT_BLINK_INTERVAL = 500;
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED = 1;
}
