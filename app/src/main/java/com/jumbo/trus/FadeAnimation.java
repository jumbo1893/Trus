package com.jumbo.trus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

public class FadeAnimation {

    private final View view;

    public FadeAnimation(View view) {
        this.view = view;
    }

    public void fadeInAnimation() {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(800);

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(0);
            }
        });
        fadeIn.start();
    }

    public void fadeOutAnimation() {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);

        fadeOut.setDuration(500);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // We wanna set the view to GONE, after it's fade out. so it actually disappear from the layout & don't take up space.
                view.setVisibility(View.GONE);
                view.setAlpha(0);
            }
        });

        fadeOut.start();
    }

    public void fadeInAnimation(int delay) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(800);
        fadeIn.setStartDelay(delay);

        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(0);
            }
        });
        fadeIn.start();
    }
}
