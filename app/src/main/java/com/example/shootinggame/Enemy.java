package com.example.shootinggame;

import android.animation.AnimatorSet;
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
        this.velocity = (int) (Math.random() * 3000 + 3000);
        animatorSet = new AnimatorSet();

    }

    public int getX() {
        return x;
    }

    private void createAnimator() {

    }
}
