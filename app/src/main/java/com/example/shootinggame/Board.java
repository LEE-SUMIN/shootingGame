package com.example.shootinggame;

import android.widget.ImageView;

import java.util.ArrayList;

public class Board {
    private static Board board;
    private Cannon cannon;
    private ArrayList<Enemy> enemies;
    public static int bullets;

    private int life = 3;
    private final int bulletLimit = 5;

    private Board() {
        cannon = Cannon.getInstance();
        enemies = new ArrayList<>();
        bullets = 0;
    }

    public static Board getInstance() {
        if(board == null){
            board = new Board();
        }
        return board;
    }

    public void start() {

    }

    public Cannon getCannon() {
        return cannon;
    }


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
        enemies.add(e);
        return e;
    }

    public void removeLife() {
        life--;
        if(life <= 0) {

        }
    }
}
