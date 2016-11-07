package com.vvorld.speed.framework.helper.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.vvorld.speed.R;
import com.vvorld.speed.common.utils.FontCache;

/**
 * Created by vivekjha on 15/02/16.
 */
public class SpeedSplashTextView extends TextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 100; //Default 100ms delay

    public SpeedSplashTextView(Context context) {
        super(context);
        applyCustomisation(context);
    }

    public SpeedSplashTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomisation(context);
    }

    public SpeedSplashTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomisation(context);
    }

    private void applyCustomisation(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/trajan_pro_regular.ttf", context);
        setTypeface(customFont);
        setTextSize(35);
        setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };

    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

}
