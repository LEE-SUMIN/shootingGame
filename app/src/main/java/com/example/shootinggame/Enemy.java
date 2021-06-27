package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

public class Enemy {
    static private int id = 0;
    private int x;
    private float y;
    private int startOffset;
    private int velocity;
    ImageView view;
    AnimatorSet animatorSet;

    public Enemy(ImageView view) {
        this.view = view;
        this.x = (int) (Math.random() * (MainActivity.display_width - 200));
        this.y = -200;
        this.startOffset = (int) (Math.random() * 3000 + 1000);
        this.velocity = (int) (Math.random() * 3000 + 7000);
        animatorSet = new AnimatorSet();
        createAnimator();
    }

    public int getX() {
        return x;
    }
    public float getY() { return y; }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    private void createAnimator() {
        ValueAnimator translationY = ValueAnimator.ofFloat(y, MainActivity.display_height * 0.8f);
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float valY = (float) animation.getAnimatedValue();
                view.setY(valY);
                y = valY;
            }
        });
        animatorSet.play(translationY);
        animatorSet.setDuration(velocity);
        animatorSet.setStartDelay(startOffset);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Board.getInstance().removeLife();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
