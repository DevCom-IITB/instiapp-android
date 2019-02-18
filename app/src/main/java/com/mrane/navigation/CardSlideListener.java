package com.mrane.navigation;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.mrane.navigation.SlidingUpPanelLayout.PanelSlideListener;

import app.insti.R;
import app.insti.fragment.MapFragment;

public class CardSlideListener implements PanelSlideListener,
        ValueAnimator.AnimatorUpdateListener {
    private static final long TIME_ANIMATION_SHOW = 250;
    private MapFragment mainActivity;
    private SlidingUpPanelLayout slidingLayout;
    private EndDetectScrollView scrollView;
    private ValueAnimator animator;

    public CardSlideListener(MapFragment mainActivity) {
        this.mainActivity = mainActivity;
        slidingLayout = mainActivity.getSlidingLayout();
        scrollView = (EndDetectScrollView) mainActivity.getActivity()
                .findViewById(R.id.new_expanded_place_card_scroll);

        animator = new ValueAnimator();
        animator.addUpdateListener(this);
        Interpolator i = new Interpolator() {
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        };
        animator.setInterpolator(i);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
//		setActionBarTranslation(slidingLayout.getCurrentParalaxOffset());
//		if(slideOffset >= slidingLayout.getAnchorPoint()){
//			mainActivity.getSupportActionBar().hide();
//		}
//		else{
//			mainActivity.getSupportActionBar().show();
//		}
    }

    @Override
    public void onPanelCollapsed(View panel) {
    }

    @Override
    public void onPanelExpanded(View panel) {
        scrollView.requestDisallowInterceptTouchEvent(false);
        scrollView.setScrollingEnabled(true);

    }

    @Override
    public void onPanelAnchored(View panel) {
        scrollView.requestDisallowInterceptTouchEvent(true);
        scrollView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelHidden(View panel) {
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = 20;
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup) mainActivity.getActivity().findViewById(
                android.R.id.content).getParent());
        int children = content.getChildCount();
        for (int i = 0; i < children; i++) {
            View child = content.getChildAt(i);
            if (child.getId() != android.R.id.content) {
                if (y <= -actionBarHeight) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    child.setTranslationY(y);
                }
            }
        }
    }

    public void dismissCard() {
        animator.cancel();
        int initialPanelHeight = slidingLayout.getPanelHeight();
        int finalPanelHeight = 0;
        animator.setIntValues(initialPanelHeight, finalPanelHeight);
        animator.setDuration(TIME_ANIMATION_SHOW);
        animator.start();
    }

    public void showCard() {
        animator.cancel();
        int initialPanelHeight = slidingLayout.getPanelHeight();
        int finalPanelHeight = mainActivity.getResources()
                .getDimensionPixelSize(R.dimen.hidden_card_height);
        animator.setIntValues(initialPanelHeight, finalPanelHeight);
        animator.setDuration(TIME_ANIMATION_SHOW);
        animator.start();
    }

    public boolean isPanelOpen() {
        return slidingLayout.getPanelHeight() > 10;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        int panelHeight = (Integer) animator.getAnimatedValue();
        slidingLayout.setPanelHeight(panelHeight);
    }

}
