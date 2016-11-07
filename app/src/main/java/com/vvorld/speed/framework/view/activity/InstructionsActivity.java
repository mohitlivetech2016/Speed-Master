package com.vvorld.speed.framework.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.vvorld.speed.R;

/**
 * Created by vaibhav.singhal on 8/31/2016.
 */
public class InstructionsActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        //Assigns the ViewPager to TabLayout.
        mTabLayout.setupWithViewPager(mViewPager);
        showInstructionLayout(this);
        startBlinkAnimationOnSpeedMagnitudeTextView();

    }
    @Override
    public void onBackPressed() {
        if(rel_instruction.getVisibility() == View.VISIBLE){
            backButtonPressed(this);
        }
        else {
            super.onBackPressed();
        }
    }
    private void startBlinkAnimationOnSpeedMagnitudeTextView(){
        TextView txtGotIt = (TextView) findViewById(R.id.txt_instruction_gotit);
        txtGotIt.setAnimation(null);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        txtGotIt.startAnimation(myFadeInAnimation);
    }

}
