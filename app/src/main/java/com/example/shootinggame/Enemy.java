package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Enemy {
    private int id;
    private int x;
    private float y;
    private int velocity;
    private boolean alive;
    ImageView view;
    AnimatorSet animatorSet;

    public Enemy(ImageView view, int id) {
        this.id = id;
        this.view = view;
        this.x = (int) (Math.random() * (MainActivity.display_width - 200));
        this.y = 0;
        this.alive = true;
        this.velocity = (int) (Math.random() * 3000 + 7000);
        animatorSet = new AnimatorSet();
        createAnimator();
    }

    private void createAnimator() {
        ValueAnimator translationY = ValueAnimator.ofFloat(y, MainActivity.display_height * 0.8f - 200f);
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
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(alive) {
                    remove();
                    Board.getInstance().removeLife();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    public int getX() {
        return x;
    }

    public float getY() { return y; }

    public boolean isAlive() {
        return alive;
    }

    public void remove() {
        alive = false;
        view.clearAnimation();
        view.setVisibility(View.GONE);
        Board.getInstance().removeEnemy(id);
    }
}
