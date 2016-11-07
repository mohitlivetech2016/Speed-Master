package com.vvorld.speed.common.listeners;

import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by vaibhav.singhal on 7/8/2016.
 * Used to pass Result object back to caller.
 */
public interface ILocationSettingsListener {
    void onResult(LocationSettingsResult locationSettingsResult);
}
