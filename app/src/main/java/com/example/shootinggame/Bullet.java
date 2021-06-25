package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Bullet {
    private ImageView view;
    private int angle;
    private int x;
    private int y;
    private int velocity;
    private int counter;
    private int reflection = 0;
    private List<AnimatorSet> animatorSets;

    Bullet(ImageView view, int angle) {
        this.view = view;
        this.angle = angle;
        this.velocity = (int) (Math.random() * 1000 + 500);
        animatorSets = new ArrayList<>();

        createAnimators();
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

    private void createAnimators() {
        if(angle == 90) {
            float dy = -MainActivity.display_height;
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", dy);
            animatorSet.play(translationY);
            animatorSet.setDuration(velocity);
            animatorSets.add(animatorSet);
        }
        else {
            float dx = angle > 90 ? -MainActivity.display_width / 2 : MainActivity.display_width / 2;
            float dy = (float) (-dx * Math.tan(Math.toRadians(angle)));
            int n = (int) (MainActivity.display_height / -dy) + 2;
            while(n > 0) {
                float limit_x = dx < 0 ? dx + 30 : dx - 30;
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", limit_x);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", dy);
                animatorSet.playTogether(translationX, translationY);
                animatorSet.setDuration(velocity);
                animatorSet.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                Log.d("getY()", view.getY() + "");
                                                if(view.getY() < 0) {
                                                    //TODO: bullet array에서 제거
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
                n--;
                //dx, dy 값 조정
                angle = 180 - angle;
                dx = angle > 90 ? -MainActivity.display_width / 2 : MainActivity.display_width / 2;
                dy += (float) (-dx * Math.tan(Math.toRadians(angle)));
                Log.d("dy", dy + "");
            }

        }
    }




    public double getVelocity() {
        return this.velocity;
    }
}
