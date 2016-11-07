package com.vvorld.speed.common.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vaibhav on 30/05/14.
 */
public class Prefs {
    /**
     * The Constant sharedPrefsName.
     */
    private static final String sharedPrefsName = "AppPrefs";

    private static final String IS_GPS_PERMISSION = "isGpsPermission";

    private static final String IS_APP_INSTALLED_FIRST_TIME = "isAppInstalledFirstTime";
    private static final String IS_NEVER_SHOW_AGAIN = "isNeverShowAgain";
    private static final String COUNT_BEFORE_RATE_US = "countBeforeRateUs";
    private static final String INSTRUCTION_SCREEN_COUNT = "instructionScreenCount";


    /**
     * The m_context.
     */
    private Context context;

    /**
     * Instantiates a new prefs.
     *
     * @param context the context
     */
    public Prefs(Context context) {
        this.context = context;
    }

    /**
     * Gets the.
     *+
     * @return the shared preferences
     */
    private SharedPreferences get() {
        return context.getSharedPreferences(sharedPrefsName,
                Context.MODE_PRIVATE);
    }

    public boolean isGpsPermission() {
        return get().getBoolean(IS_GPS_PERMISSION, false);
    }

    public void setIsGpsPermission(boolean isGpsPermission) {
        get().edit().putBoolean(IS_GPS_PERMISSION, isGpsPermission).commit();
    }
    public boolean isAppInstalledFirstTime() {
        return get().getBoolean(IS_APP_INSTALLED_FIRST_TIME, true);
    }

    public void setIsAppInstalledFirstTime(boolean isAppInstalledFirstTime) {
        get().edit().putBoolean(IS_APP_INSTALLED_FIRST_TIME, isAppInstalledFirstTime).commit();
    }

    public boolean isNeverShowAgainClicked() {
        return get().getBoolean(IS_NEVER_SHOW_AGAIN, false);
    }

    public void setNeverShowAgainClicked(boolean isNeverShowAgain) {
        get().edit().putBoolean(IS_NEVER_SHOW_AGAIN, isNeverShowAgain).commit();
    }

    public int getCountBeforeRateUs() {
        return get().getInt(COUNT_BEFORE_RATE_US, 0);
    }

    public void setCountBeforeRateUs(int countBeforeRateUs) {
        get().edit().putInt(COUNT_BEFORE_RATE_US, countBeforeRateUs).commit();
    }

    public int getInstructionScreenCount() {
        return get().getInt(INSTRUCTION_SCREEN_COUNT, 0);
    }

    public void setInstructionScreenCount(int instructionScreenCount) {
        get().edit().putInt(INSTRUCTION_SCREEN_COUNT, instructionScreenCount).commit();
    }

}
