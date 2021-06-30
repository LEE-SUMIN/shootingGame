package com.example.shootinggame.Model;

import android.content.Context;
import android.widget.ImageView;

import com.example.shootinggame.ConflictListener;
import com.example.shootinggame.LifeListener;

import java.util.HashMap;

public class Board {
    //게임 상에 존재하는 board는 하나여야 하므로 static 변수로 관리
    private static Board board;
    private boolean running; //게임 실행 상태
    
    //Board는 Cannon, Bullets, Enemies를 가지며, 외부에서 각 객체에 접근할 경우 반드시 board를 통하도록 한다.
    private Cannon cannon;
    private HashMap<Integer, Bullet> bulletHashmap; //화면 상에 존재하는 bullet들
    private HashMap<Integer, Enemy> enemyHashMap; //화면 상에 존재하는 enemy들
    private int bulletId = 0; //bullet객체를 생성할 때 마다 하나씩 증가시키며 객체에 id 부여 -> hashMap에 저장
    private int enemyId = 0; //enemy객체를 생성할 떄 마다 하나씩 증가시키며 객체에 id 부여 -> hashMap에 저장

    /* 게임 세팅 변수 */
    private int life; //남은 생명 개수
    private int bulletLimit; //화면 상에 존재할 수 있는 최대 bullet 개수

    private LifeListener lifeListener; //생명이 하나 감소하거나, 모두 소멸된 경우 MainActivity에 알림
    private ConflictListener conflictListener; //bullet과 enemy가 충돌한 경우 MainActivity에 알림

    private Board() { }

    /**
     * 전체 게임에는 반드시 하나의 Board만 존재해야하므로 Singleton Pattern 적용
     */
    public static Board getInstance() {
        if(board == null){
            board = new Board();
        }
        return board;
    }

    /**
     * board가 가져야 하는 각종 자원 초기화
     * @param context
     */
    public void init(Context context) {
        cannon = Cannon.getInstance();
        this.enemyHashMap = new HashMap<>();
        this.bulletHashmap = new HashMap<>();
        this.lifeListener = (LifeListener) context;
        this.conflictListener = (ConflictListener) context;
    }

    /**
     * 게임 시작 변수 세팅
     * @param bulletLimit : 한 화면에 존재할 수 있는 bullet 개수
     */
    public void start(int life, int bulletLimit) {
        running = true;
        this.life = life;
        this.bulletLimit = bulletLimit;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public int getLife() {
        return life;
    }

    /**
     * 현재 화면에 존재하는 bullet개수가 bulletLimit보다 적은 경우 -> true
     * @return
     */
    public boolean shootAvailable() {
        return bulletHashmap.size() < bulletLimit;
    }

    /**
     * MainActivity에서 shoot 버튼을 클릭하는 경우 호출 -> bullet 생성
     * @param view : MainActivity로부터 ImageView를 받아와서 Bullet 객체 생성 시에 View를 넘겨주고, Animator를 생성함
     * @return
     */
    public Bullet addBullet(ImageView view) {
        //bullet 당 id가 생성되고, HashMap으로 관리
        int id = setBulletId();
        //현재 Cannon의 각도를 받아와서 bullet 생성 시에 넘겨줌 -> bullet 경로 결정
        int angle = cannon.getAngle();
        Bullet b = new Bullet(view, angle, id);
        bulletHashmap.put(id, b);
        return b;
    }

    /**
     * MainActivity의 EnemyThread 내부에서 호출 -> Enemy 생성
     * @param view : MainActivity로부터 ImageView를 받아와서 Enemy 객체 생성 시에 View를 넘겨주고, Animator를 생성함
     * @return
     */
    public Enemy addEnemy(ImageView view) {
        //enemy 당 id가 생성되고, HashMap으로 관리
        int id = setEnemyId();
        Enemy e = new Enemy(view, id);
        enemyHashMap.put(id, e);
        return e;
    }

    /**
     * 화면 상에 존재하는 bullet들을 HashMap으로 관리하기 위해 각각의 Bullet에 id를 부여한다.
     * @return
     */
    private int setBulletId() {
        while(bulletHashmap.containsKey(bulletId)) {
            bulletId = (bulletId + 1) % 10;
        }
        return bulletId;
    }

    /**
     * 화면 상에 존재하는 enemy들을 HashMap으로 관리하기 위해 각각의 Enemy에 id를 부여한다.
     * 한 화면 상에 존재하는 enemy는 이론 상 20개를 넘을 수 없다 -> modular 연산 사용
     * @return
     */
    private int setEnemyId() {
        while(enemyHashMap.containsKey(enemyId)) {
            enemyId = (enemyId + 1) % 30;
        }
        return enemyId;
    }

    /**
     * 게임이 진행 중인 상태인지 확인
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * 생명이 하나 감소될 때 호출되면서 board가 갖는 life변수를 1 감소시키고,
     * lifeListener의 함수를 호출함으로써 MainActivity에 알림 -> heart ImageView 조정 or FinishActivity로 넘어감
     */
    public void removeLife() {
        life--;
        lifeListener.decreaseLife(life);
    }

    /**
     * board가 갖는 bulletHashMap으로부터 해당 id를 가진 bullet을 제거한다.
     * 즉, bullet이 enemy와 충돌하면서 사라지거나, 화면을 벗어날 때 호출된다.
     * @param id : 제거할 bullet의 id
     */
    public void removeBullet(int id) {
        bulletHashmap.remove(id);
    }

    /**
     * board가 갖는 enemyHashMap으로부터 해당 id를 가진 enemy를 제거한다.
     * 즉, enemy가 bullet과 충돌하면서 사라지거나, 화면을 벗어날 때 호출된다.
     * @param id : 제거할 enemy의 id
     */
    public void removeEnemy(int id) {
        enemyHashMap.remove(id);
    }

    /**
     * 충돌감지Thread내에서 호출되며 충돌을 감지한다.
     */
    public void detectConflict() {
        //enemyHashMap에 존재하는 enemy와 충돌하는 bullet이 존재하는지 확인 -> MainActivity에 알림
        //(1) 화면 상에 존재하는 enemy의 좌표 값 -> ex, ey
        for(int i = 0; i < 30; i++) {
            if(enemyHashMap.containsKey(i)){
                Enemy e = enemyHashMap.get(i);
                if(e != null && e.isValid()) {
                    float ex = e.getX();
                    float ey = e.getY();
                    //(2) enemy와 충돌하는 bullet이 있는지 확인
                    Bullet b = findConflictingBullet(ex, ey);
                    if(b != null) {
                        conflictListener.conflict(e, b);
                    }
                }
            }
        }
    }

    /**
     * 주어진 enemy 좌표와 충돌하는 bullet이 있는지 확인
     * @param ex : enemy의 x좌표
     * @param ey : enemy의 y좌표
     * @return
     */
    public Bullet findConflictingBullet(float ex, float ey) {
        for(int j = 0; j < 30; j++) {
            if(bulletHashmap.containsKey(j)) {
                Bullet b = bulletHashmap.get(j);
                if(b != null && b.isValid()) {
                    float bx = b.getX();
                    float by = b.getY();
                    if(isConflict(ex, ey, bx, by)) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 주어진 Enemy 객체의 좌표와 Bullet객체의 좌표가 중첩되어 있는지(충돌했는지) 확인
     * @param ex : Enemy의 x좌표
     * @param ey : Enemy의 y좌표
     * @param bx : Bullet의 x좌표
     * @param by : Bullet의 y좌표
     * @return
     */
    public boolean isConflict(float ex, float ey, float bx, float by) {
        if(bx < ex + 135 && bx + 40 > ex) {
            if (by < ey + 135 && by + 40 > ey) {
                return true;
            }
        }
        return false;
    }

    /**
     * 존재하는 bullet과 enemy를 모두 제거
     * (게임이 종료될 때 호출되면서 전체 board를 초기화 할 때 사용)
     */
    public void clear() {
        running = false;
        for(int i = 0; i < 10; i++) {
            if(bulletHashmap.containsKey(i)) {
                Bullet b = bulletHashmap.get(i);
                if(b != null) {
                    b.remove();
                }
            }
        }
        for(int i = 0; i < 30; i++) {
            if(enemyHashMap.containsKey(i)) {
                Enemy e = enemyHashMap.get(i);
                if(e != null) {
                    e.remove();
                }
            }
        }
        bulletHashmap.clear();
        enemyHashMap.clear();
    }
}
