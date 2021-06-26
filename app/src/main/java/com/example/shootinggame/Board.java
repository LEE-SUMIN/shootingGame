package com.example.shootinggame;

import android.widget.ImageView;

import java.util.ArrayList;

public class Board {
    private static Board board;
    private Cannon cannon;
    private ArrayList<Enemy> enemyList;
    public static int bullets;

    private int life;
    private int lifeLimit;
    private int bulletLimit;

    private LifeListener lifeListener;

    private Board() {
        cannon = Cannon.getInstance();
        enemyList = new ArrayList<>();
        bullets = 0;
    }

    public static Board getInstance() {
        if(board == null){
            board = new Board();
        }
        return board;
    }

    public void initListener(LifeListener lifeListener) {
        this.lifeListener = lifeListener;
    }

    public void start(int lifeLimit, int bulletLimit) {
        this.lifeLimit = lifeLimit;
        this.life = lifeLimit;
        this.bulletLimit = bulletLimit;
    }

    public void start() {

    }

    public Cannon getCannon() {
        return cannon;
    }

    public int getLifeLimit() { return lifeLimit; }


    public boolean shootAvailable() {
        if(bullets > bulletLimit) return false;
        return true;
    }


    public Bullet shoot(ImageView view) {
        int angle = cannon.getAngle();
        Bullet bullet = new Bullet(view, angle);
        bullets++;

        return bullet;
    }

    public Enemy addEnemy(ImageView view) {
        Enemy e = new Enemy(view);
        enemyList.add(e);
        return e;
    }

    public void removeLife() {
        life--;
        if(life <= 0) {
            lifeListener.die();
        }
        else {
            lifeListener.lifeDecrease();
        }
    }

    public void removeBullet() {
        bullets--;
    }

    public int getLife() {
        return life;
    }
}
