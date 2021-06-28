package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Bullet {
    private final ImageView view;
    private final int id;
    private final int velocity;

    private int angle;
    private int counter;
    private int reflection = 0;
    private List<AnimatorSet> animatorSets;
    private Board board;
    private float x;
    private float y;

    Bullet(ImageView view, int angle, int id) {
        this.view = view;
        this.id = id;
        view.setX(MainActivity.display_width / 2f - 30f);
        view.setY(MainActivity.display_height * 0.8f - 200f);
        this.x = view.getX();
        this.y = view.getY();
        this.angle = angle;
        this.velocity = (int) (Math.random() * 1000 + 500);
        animatorSets = new ArrayList<>();
        board = Board.getInstance();

        createAnimators();
    }

    private void createAnimators() {
        float height = MainActivity.display_height * 0.8f - 200f;
        if(angle == 90) {
            float nextY = -50;
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator translationY = ValueAnimator.ofFloat(this.y, nextY);
            translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float yVal = (float) animation.getAnimatedValue();
                    view.setY(yVal);
                    y = yVal;
                }
            });
            animatorSet.playTogether(translationY);
            animatorSet.setDuration(velocity);
            animatorSets.add(animatorSet);
        }
        else {
            float curX = x;
            float curY = y;
            float nextX = angle > 90 ? 0 : MainActivity.display_width;
            float nextY = (float) (height - (MainActivity.display_width / 2f) * Math.abs(Math.tan(Math.toRadians(angle))));
            int offset = 0;
            while(curY >= 0) {
                //y좌표 이동 좌표 조정
                offset++;
                //x좌표 이동 범위 조정(bullet 너비 빼기)
                float limit_x = nextX < MainActivity.display_width / 2f ? nextX : nextX - 40;
                AnimatorSet animatorSet = new AnimatorSet();
                ValueAnimator translationX = ValueAnimator.ofFloat(curX, limit_x);
                ValueAnimator translationY = ValueAnimator.ofFloat(curY, nextY);
                animatorSet.playTogether(translationX, translationY);
                animatorSet.setDuration(velocity);
                translationX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float xVal = (float) animation.getAnimatedValue();
                        view.setX(xVal);
                        x = xVal;
                    }
                });
                translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float yVal = (float) animation.getAnimatedValue();
                        view.setY(yVal);
                        y = yVal;
                    }
                });
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(view.getY() < 0) {
                            remove();
                        }
                        else{
                            reflection++;
                            AnimatorSet set = getNextAnimatorSet();
                            if(set != null)
                                set.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                });

                animatorSets.add(animatorSet);
                //dx, dy 값 조정
                angle = 180 - angle;
                curX = limit_x;
                curY = nextY;
                nextX = angle > 90 ? 0 : MainActivity.display_width;
                nextY = (float) (height - offset * MainActivity.display_width * Math.abs(Math.tan(Math.toRadians(angle))));
            }

        }
    }

    public AnimatorSet getFirstAnimatorSet() {
        if(animatorSets.size() == 0) return null;
        return animatorSets.get(0);
    }

    public AnimatorSet getNextAnimatorSet() {
        counter++;
        if(counter >= animatorSets.size())
            return null;
        return animatorSets.get(counter);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void remove() {
        view.clearAnimation();
        view.setVisibility(View.GONE);
        board.removeBullet(id);
    }
}
