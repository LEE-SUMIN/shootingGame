package com.example.shootinggame;

import android.animation.AnimatorSet;
import android.media.Image;
import android.widget.ImageView;

public class Enemy {
    static int id = 0;
    int x;
    private int velocity;
    ImageView view;
    AnimatorSet animatorSet;

    public Enemy(ImageView view) {
        this.view = view;
        this.x = (int) Math.random() * MainActivity.display_width;
        this.velocity = (int) (Math.random() * 3000 + 3000);
        animatorSet = new AnimatorSet();

    }

    private void createAnimator() {

    }
}
