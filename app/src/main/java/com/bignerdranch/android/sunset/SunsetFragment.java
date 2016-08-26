package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by owen on 2016/8/24.
 */
public class SunsetFragment extends Fragment {

    private static final int ANIMATION_DURATION = 3000;

    private View mSunView;
    private View mSkyView;

    private ObjectAnimator mSunAnim;
    private ObjectAnimator mSkyAnim;

    private AnimatorSet mSunsetAnimSet;
    private AnimatorSet mSunriseAnimSet;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sunset, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        mBlueSkyColor = ContextCompat.getColor(getActivity(), R.color.blue_sky);
        mSunsetSkyColor = ContextCompat.getColor(getActivity(), R.color.sunset_sky);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mSunView.getY() >= mSkyView.getHeight()) ||
                        mSunsetAnimSet != null && mSunsetAnimSet.isRunning()) {
                    /*
                     * 太阳上升的情况只有两种
                     * 1) 太阳刚刚落到底
                     * 2) 太阳正在落下
                     */
                    sunrise();
                } else {
                    // 其他情况，太阳上升
                    sunset();
                }

//            void runrise() {
//                if (太阳正在落下的过程中) {
//
//                } else {
//                    太阳升起和天空变蓝
//                }
//            }
//
//            void sunset() {
//                if (太阳正在升起的过程中) {
//
//                } else {
//                    太阳落下，天空变黑
//                }
//            }
            }
        });
    }

    /**
     * 太阳升起
     */
    private void sunrise() {
        // 如果太阳正在落下
        if (mSunsetAnimSet != null && mSunsetAnimSet.isRunning()) {
            long playTime = ANIMATION_DURATION - mSunAnim.getCurrentPlayTime();

            mSunAnim = getSunAnimator((float) mSunAnim.getAnimatedValue(), mSunView.getTop());
            mSunAnim.setCurrentPlayTime(playTime);

            mSkyAnim = getSkyAnimator((int) mSkyAnim.getAnimatedValue(), mBlueSkyColor);
            mSkyAnim.setCurrentPlayTime(playTime);

            // 停止太阳下落的动画
            mSunsetAnimSet.end();
        } else {
            mSunAnim = getSunAnimator(mSkyView.getHeight(), mSunView.getTop());
            mSkyAnim = getSkyAnimator(mSunsetSkyColor, mBlueSkyColor);
        }
        mSunriseAnimSet = new AnimatorSet();
        mSunriseAnimSet.play(mSunAnim).with(mSkyAnim);
        mSunriseAnimSet.start();
    }

    /**
     * 太阳落下
     */
    private void sunset() {
        // 如果太阳正在升起
        if (mSunriseAnimSet != null && mSunriseAnimSet.isRunning()) {
            long playTime = ANIMATION_DURATION - mSunAnim.getCurrentPlayTime();

            mSunAnim = getSunAnimator((float) mSunAnim.getAnimatedValue(), mSkyView.getHeight());
            mSunAnim.setCurrentPlayTime(playTime);

            mSkyAnim = getSkyAnimator((int) mSkyAnim.getAnimatedValue(), mSunsetSkyColor);
            mSkyAnim.setCurrentPlayTime(playTime);

            mSunriseAnimSet.end();
        } else {
            mSunAnim = getSunAnimator(mSunView.getTop(), mSkyView.getHeight());
            mSkyAnim = getSkyAnimator(mBlueSkyColor, mSunsetSkyColor);
        }

        mSunsetAnimSet = new AnimatorSet();
        mSunsetAnimSet.play(mSunAnim)
                .with(mSkyAnim);

        mSunsetAnimSet.start();
    }

    private ObjectAnimator getSunAnimator(float sunYStart, float sunYEnd) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(ANIMATION_DURATION);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        return heightAnimator;
    }

    private ObjectAnimator getSkyAnimator(int startColor, int endColor) {
        ObjectAnimator skyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", startColor, endColor)
                .setDuration(ANIMATION_DURATION);
        skyAnimator.setEvaluator(new ArgbEvaluator());
        return skyAnimator;
    }
}
