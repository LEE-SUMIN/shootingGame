package com.example.shootinggame;

import android.widget.ImageView;

import java.util.ArrayList;

public class Board {
    private static Board board;
    private Cannon cannon;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;

    private final int life = 3;
    private final int bulletLimit = 1000;

    private Board() {
        cannon = Cannon.getInstance();
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    public static Board getInstance() {
        if(board == null){
            board = new Board();
        }
        return board;
    }

    public Cannon getCannon() {
        return cannon;
    }


    public boolean shootAvailable() {
        if(bullets.size() > bulletLimit) return false;
        return true;
    }


    public Bullet shoot(ImageView view) {
        int angle = cannon.getAngle();
        Bullet bullet = new Bullet(view, angle);
        bullets.add(bullet);

        return bullet;
    }

    public void addEnemy(ImageView view) {
        Enemy e = new Enemy(view);
        enemies.add(e);
    }
}
