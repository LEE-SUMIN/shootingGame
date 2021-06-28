package com.example.shootinggame;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private static Board board;
    private static int bulletId = 0;
    private static int enemyId = 0;

    private Cannon cannon;
    private HashMap<Integer, Bullet> bulletHashmap;
    private HashMap<Integer, Enemy> enemyHashMap;

    private int life;
    private int lifeLimit;
    private int bulletLimit;

    private LifeListener lifeListener;
    private ConflictListener conflictListener;

    private Board() {
        cannon = Cannon.getInstance();
        this.enemyHashMap = new HashMap<>();
        this.bulletHashmap = new HashMap<>();
    }

    public static Board getInstance() {
        if(board == null){
            board = new Board();
        }
        return board;
    }

    public void initListener(LifeListener lifeListener, ConflictListener conflictListener) {
        this.lifeListener = lifeListener;
        this.conflictListener = conflictListener;
    }

    public void start(int lifeLimit, int bulletLimit) {
        clear();
        this.lifeLimit = lifeLimit;
        this.life = lifeLimit;
        this.bulletLimit = bulletLimit;
        //conflictDetectorThread 시작
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    detectConflict();
                }
            }
        }).start();
    }

    public Cannon getCannon() {
        return cannon;
    }

    public int getLife() {
        return life;
    }

    public int getLifeLimit() { return lifeLimit; }

    public boolean shootAvailable() {
        return bulletHashmap.size() < bulletLimit;
    }

    public Bullet shoot(ImageView view) {
        int angle = cannon.getAngle();
        int id = setBulletId();
        Bullet bullet = new Bullet(view, angle, id);
        bulletHashmap.put(id, bullet);
        return bullet;
    }


    public Enemy addEnemy(ImageView view) {
        int id = setEnemyId();
        Enemy e = new Enemy(view, id);
        enemyHashMap.put(id, e);
        return e;
    }

    private int setBulletId() {
        int id = bulletId;
        bulletId = (bulletId + 1) % 10;
        return id;
    }

    private int setEnemyId() {
        int id = enemyId;
        enemyId = (enemyId + 1) % 10;
        return id;
    }


    public void removeLife() {
        life--;
        if(life <= 0) {
            lifeListener.die();
        }
        else {
            lifeListener.decreaseLife();
        }
    }

    public void removeBullet(int id) {
        bulletHashmap.remove(id);
    }

    public void removeEnemy(int id) {
        enemyHashMap.remove(id);
    }

    public void detectConflict() {
        for(int i = 0; i < 10; i++) {
            if(enemyHashMap.containsKey(i)){
                Enemy e = enemyHashMap.get(i);
                if(e != null && e.isALive()) {
                    float ex = e.getX();
                    float ey = e.getY();
                    for(int j = 0; j < 10; j++) {
                        if(bulletHashmap.containsKey(j)){
                            Bullet b = bulletHashmap.get(j);
                            if(b != null) {
                                float bx = b.getX();
                                float by = b.getY();
                                if(bx < ex + 150 && bx + 40 > ex) {
                                    if(by < ey + 150 && by + 40 > ey) {
                                        conflictListener.conflict(e, b);
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }

    public void clear() {
        for(int i = 0; i < 10; i++) {
            if(bulletHashmap.containsKey(i)) {
                Bullet b = bulletHashmap.get(i);
                if(b != null) {
                    b.remove();
                }
            }
        }
        for(int i = 0; i < 10; i++) {
            if(enemyHashMap.containsKey(i)) {
                Enemy e = enemyHashMap.get(i);
                if(e != null) {
                    e.remove();
                }
            }
        }
    }

}
