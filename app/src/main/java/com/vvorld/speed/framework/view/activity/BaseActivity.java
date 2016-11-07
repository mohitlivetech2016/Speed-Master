package com.vvorld.speed.framework.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vvorld.speed.R;
import com.vvorld.speed.common.value.Constants;
import com.vvorld.speed.framework.view.adapter.ViewPagerAdapter;
import com.vvorld.speed.framework.view.fragments.AltitudeFragment;
import com.vvorld.speed.framework.view.fragments.SpeedFragment;

/**
 * Created by vaibhav.singhal on 7/4/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private View mInflatedView;
    private CoordinatorLayout mCoordinatorLayout;
    protected SpeedFragment mSpeedFragment;
    protected AltitudeFragment mAltitudeFragment;
    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;
    protected TextView txt_instruction_gotit;
    protected RelativeLayout rel_instruction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        txt_instruction_gotit = (TextView) findViewById(R.id.txt_instruction_gotit);
        rel_instruction = (RelativeLayout) findViewById(R.id.rel_instruction);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_container_coordinatorLayout);
        setupToolbar();
        FrameLayout contentLayout = (FrameLayout) findViewById(R.id.content_detail);
        mInflatedView = getLayoutInflater().inflate(getLayoutId(), null);
        contentLayout.addView(mInflatedView);
    }
    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    protected abstract int getLayoutId();
    public CoordinatorLayout getCoordinatorLayout(){
        return mCoordinatorLayout;
    }
    /**
     * Defines the number of tabs by setting appropriate fragment and tab name.
     */
    protected void setupViewPager() {
        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSpeedFragment = new SpeedFragment();
        mAltitudeFragment = new AltitudeFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mSpeedFragment, Constants.FRAGMENT_CONSTANTS.FRAGMENT_SPEED);
        adapter.addFragment(mAltitudeFragment, Constants.FRAGMENT_CONSTANTS.FRAGMENT_ALTITUDE);
        mViewPager.setAdapter(adapter);
        // initially speed fragment will be visible so setting its visibility to true;
        mSpeedFragment.setIsFragmentVisible(true);
        mAltitudeFragment.setIsFragmentVisible(false);
        // to solve issue as isVisible on fragment returning true for both fragment as view pager keep in cache
        //http://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //This method will be invoked when a new page becomes selected.
                if (position == Constants.SPEED_FRAGMENT_POSITION) {
                    mSpeedFragment.setIsFragmentVisible(true);
                    mAltitudeFragment.setIsFragmentVisible(false);
                } else if (position == Constants.ALTITUDE_FRAGMENT_POSITION) {
                    mSpeedFragment.setIsFragmentVisible(false);
                    mAltitudeFragment.setIsFragmentVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    protected void showInstructionLayout(final Activity activity)
    {
        txt_instruction_gotit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotItButtonClicked(activity);
            }
        });
        rel_instruction.setVisibility(View.VISIBLE);
        rel_instruction.setClickable(true);
    }
    protected void backButtonPressed(Activity activity){
           gotItButtonClicked(activity);
    }
    protected void gotItButtonClicked(Activity  activity){
        if(activity instanceof InstructionsActivity){
            txt_instruction_gotit.setAnimation(null);
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            activity.finish();
            return;
        }
        rel_instruction.setVisibility(View.GONE);
    }
    protected void setTouchListenerOnInstructionsLayout(View.OnTouchListener touchListener){
        rel_instruction.setOnTouchListener(touchListener);

    }


}
