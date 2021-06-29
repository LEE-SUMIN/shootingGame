package com.example.shootinggame.Model;

import android.view.View;
import android.widget.ImageView;

public abstract class Item {
    protected final ImageView view;
    protected final int id;
    protected int velocity;
    protected float x;
    protected float y;
    protected boolean alive;
    protected Board board;

    public Item(ImageView view, int id) {
        this.board = Board.getInstance();
        this.view = view;
        this.id = id;
        this.velocity = getRandomVelocity();
        this.alive = true;
    }

    protected void setX(float x) {
        view.setX(x);
        this.x = x;
    }

    protected void setY(float y) {
        view.setY(y);
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /**
     * 충돌이 일어나지 않았는지, 화면을 벗어나지 않았는지 확인
     * @return
     */
    public boolean isValid() {
        return alive;
    }

    public void remove() {
        alive = false;
        view.setVisibility(View.GONE);
    }

    protected abstract int getRandomVelocity();
    protected abstract void createAnimators();
    public abstract void start();
}
