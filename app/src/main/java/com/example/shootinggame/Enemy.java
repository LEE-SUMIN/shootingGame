package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

public class Enemy {
    static private int id = 0;
    private int x;
    private int velocity;
    ImageView view;
    AnimatorSet animatorSet;

    public Enemy(ImageView view) {
        this.view = view;
        this.x = (int) (Math.random() * (MainActivity.display_width - 200));
        this.velocity = (int) (Math.random() * 3000 + 7000);
        animatorSet = new AnimatorSet();
        createAnimator();
    }

    public int getX() {
        return x;
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    private void createAnimator() {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationY", MainActivity.display_height * 0.9f);
        animatorSet.play(translationX);
        animatorSet.setDuration(velocity);
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
