package com.vvorld.speed.framework.view.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvorld.speed.common.app.SpeedApplication;
import com.vvorld.speed.common.utils.FunctionUtils;
import com.vvorld.speed.common.value.Constants;

/**
 * Created by vaibhav.singhal on 6/6/2016.
 */
public abstract class BaseFragment extends Fragment  {
    // Root view for this fragment
    private View mFragmentView;
    protected Location mLocation;
    // will keep track of fragment visibility;
    private boolean isFragmentVisible;

    private Activity mActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(intializeLayoutId(), null);
        initViews(mFragmentView);
        mActivity = getActivity();
        return mFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * @return returns layout id of the fragment.
     */
    protected abstract int intializeLayoutId();

    /**
     * To initialise views & other parameters.
     * @param mFragmentView
     */
    protected abstract void initViews(View mFragmentView);

    /**
     * Will be called from notifyLocationChange() of MainActivity to update location object in fragment.
     * notifyLocationChange() of MainActivity is called from onLocationChanged() of LocationUtility class.
     * @param location
     */
    protected abstract void setLocation(Location location);

    /**
     * Will notify user response of GPS enable request to fragments
     * Will be call from onActivityResult of MainActivity.
     * @param resultCode
     */
    protected abstract void notifyGPSEnableResponse(int resultCode);


    public boolean isFragmentVisible(){
        return isFragmentVisible;
    }
    /**
     * To set visible state of fragment.
     * Value is set from setupViewPager() of MainActivity.
     * @param isVisible
     */
    public void setIsFragmentVisible(boolean isVisible) {
        isFragmentVisible = isVisible;
    }

    /**
     * Show rate us dialog checking flag of never show again, internet conenctivity and count
     */
    protected void showRateUsDialog()
    {
        int countBeforeRateUs = SpeedApplication.getPrefs().getCountBeforeRateUs();
        if(!SpeedApplication.getPrefs().isNeverShowAgainClicked()
                && FunctionUtils.isNetworkAvailable(mActivity)
                && countBeforeRateUs > Constants.RATE_US_DIALOG_COUNT)
            FunctionUtils.showRateUsDialog(mActivity);
        SpeedApplication.getPrefs().setCountBeforeRateUs(++countBeforeRateUs);
    }

}