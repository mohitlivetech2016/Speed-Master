package com.vvorld.speed.common.utils;

import android.animation.Animator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by vivekjha on 05/07/16.
 */
public class SpeedAnimations {

    public interface IAnimationListener
    {
        void onAnimationEnd();
    }

    public static void setScalingSplashAnimation(View v, long duration, final IAnimationListener iAnimationListener)
    {
        v.setScaleX(0.1f);
        v.setScaleY(0.1f);

        v.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(iAnimationListener!=null)
                            iAnimationListener.onAnimationEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .setInterpolator(new OvershootInterpolator());
    }

    /**
     * To animate a view from one point to another.
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param animationListener
     * @param speed
     * @return
     */
    public static Animation animateFromOnePointToAnother(float fromX, float fromY, float toX, float toY, Animation.AnimationListener animationListener, int speed){


        Animation fromAtoB = new TranslateAnimation(
                Animation.ABSOLUTE, //from xType
                fromX,
                Animation.ABSOLUTE, //to xType
                toX,
                Animation.ABSOLUTE, //from yType
                fromY,
                Animation.ABSOLUTE, //to yType
                toY
        );
        // in milli seconds
        fromAtoB.setDuration(speed);
        fromAtoB.setInterpolator(new AnticipateOvershootInterpolator(1.0f));


        if(animationListener != null)
            fromAtoB.setAnimationListener(animationListener);
        return fromAtoB;
    }
}
