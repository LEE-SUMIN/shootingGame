package com.example.shootinggame.Model;

import android.util.Log;
import android.widget.ImageView;

import com.example.shootinggame.ConflictListener;
import com.example.shootinggame.ConflictThread;
import com.example.shootinggame.LifeListener;

import java.util.HashMap;

public class Board {
    //게임 상에 존재하는 board는 하나여야 하므로 static 변수로 관리
    private static Board board;
    
    //Board는 Cannon, Bullets, Enemies를 가지며, 외부에서 각 객체에 접근할 경우 반드시 board를 통하도록 한다.
    private Cannon cannon;
    private HashMap<Integer, Bullet> bulletHashmap; //화면 상에 존재하는 bullet들
    private HashMap<Integer, Enemy> enemyHashMap; //화면 상에 존재하는 enemy들
    private int bulletId = 0; //bullet객체를 생성할 때 마다 하나씩 증가시키며 객체에 id 부여 -> hashMap에 저장
    private int enemyId = 0; //enemy객체를 생성할 떄 마다 하나씩 증가시키며 객체에 id 부여 -> hashMap에 저장

    private int life; //남아있는 생명 개수
    private int lifeLimit; //최초 부여된 생명 개수
    private int bulletLimit; //화면 상에 존재할 수 있는 최대 bullet 개수

    private LifeListener lifeListener; //생명이 하나 감소하거나, 모두 소멸된 경우 MainActivity에 알림
    private ConflictListener conflictListener; //bullet과 enemy가 충돌한 경우 MainActivity에 알림
    private ConflictThread conflictThread;


    private Board() {
        cannon = Cannon.getInstance();
        this.enemyHashMap = new HashMap<>();
        this.bulletHashmap = new HashMap<>();
    }

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
     * Listener 할당
     * @param lifeListener : 생명이 하나 줄어들 때 마다 MainActivity에 알려줌 -> heart ImageView 조정
     * @param conflictListener : 충돌이 감지되면 MainActivity에 알려줌 -> Bulletm, Enemy ImageView 조정
     */
    public void initListener(LifeListener lifeListener, ConflictListener conflictListener) {
        this.lifeListener = lifeListener;
        this.conflictListener = conflictListener;
    }

    /**
     * 게임이 시작되기 위한 각종 초기화 과정을 진행하고, 충돌감지Thread 실행
     * @param lifeLimit : 생명 개수
     * @param bulletLimit : 한 화면에 존재할 수 있는 bullet 개수
     */
    public void start(int lifeLimit, int bulletLimit) {
        clear(); //이미 존재하는 bullet이나 enemy가 있는 경우 제거
        Log.d("테스트", "bullet개수: " + bulletHashmap.size() + " enemy개수: " + enemyHashMap.size());

        this.lifeLimit = lifeLimit;
        this.life = lifeLimit;
        this.bulletLimit = bulletLimit;

        //conflictDetectorThread 시작
        conflictThread = new ConflictThread();
        conflictThread.start();
    }

    /**
     * 외부에서 Cannon에 접근하기 위한 getter함수
     * @return
     */
    public Cannon getCannon() {
        return cannon;
    }

    /**
     * 외부에서 현재 남은 생명 개수를 얻기 위한 getter 함수
     * @return
     */
    public int getLife() {
        return life;
    }

    /**
     * 외부에서 최초 생명 개수를 알기 위한 getter 함수
     * (초기 heart ImageView 개수를 결정할 때 사용)
     * @return
     */
    public int getLifeLimit() { return lifeLimit; }

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
    public Bullet shoot(ImageView view) {
        //bullet 당 id가 생성되고, HashMap으로 관리
        int id = setBulletId();
        //현재 Cannon의 각도를 받아와서 bullet 생성시에 넘겨줌 -> animator 결정
        int angle = cannon.getAngle();
        Bullet bullet = new Bullet(view, angle, id);
        bulletHashmap.put(id, bullet);
        return bullet;
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
     * 한 화면 상에 존재하는 bullet은 최대 5개이기 때문에 id가 무한히 증가할 필요는 없다 -> modular 연산 사용
     * @return
     */
    private int setBulletId() {
        int id = bulletId;
        bulletId = (bulletId + 1) % 10;
        return id;
    }

    /**
     * 화면 상에 존재하는 enemy들을 HashMap으로 관리하기 위해 각각의 Enemy에 id를 부여한다.
     * 한 화면 상에 존재하는 enemy는 이론 상 20개를 넘을 수 없다 -> modular 연산 사용
     * @return
     */
    private int setEnemyId() {
        int id = enemyId;
        enemyId = (enemyId + 1) % 30;
        return id;
    }

    /**
     * 생명이 하나 감소될 때 호출되면서 board가 갖는 life변수를 1 감소시키고,
     * lifeListener의 함수를 호출함으로써 MainActivity에 알림 -> heart ImageView 조정 or FinishActivity로 넘어감
     * @param id
     */
    public void removeLife(int id) {
        Log.d("테스트", "죽은id: " + id);
        Enemy e = enemyHashMap.get(id);
        if(e != null && e.isAlive()) {
            life--;
            if(life <= 0) {
                //존재하는 생명을 모두 소모하여 게임을 종료해야 하는 경우
                lifeListener.die();
            }
            else {
                lifeListener.decreaseLife();
            }
        }
        e.remove();
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
        //i라는 id를 가진 enemy가 화면 상에 존재하면(hashMap에 존재하면), 존재하는 bullet 중에 enemy와 충돌하는 것이 있는지 확인.
        //(1) 존재하는 enemy의 좌표 값 -> ex, ey
        for(int i = 0; i < 30; i++) {
            if(enemyHashMap.containsKey(i)){
                Enemy e = enemyHashMap.get(i);
                if(e != null && e.isAlive()) {
                    float ex = e.getX();
                    float ey = e.getY();
                    //(2) enemy와 충돌하는 bullet이 있는지 확인
                    Bullet b = findBullet(ex, ey);
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
    public Bullet findBullet(float ex, float ey) {
        for(int j = 0; j < 30; j++) {
            if(bulletHashmap.containsKey(j)) {
                Bullet b = bulletHashmap.get(j);
                if(b != null && b.isValid()) {
                    float bx = b.getX();
                    float by = b.getY();
                    if(conflict(ex, ey, bx, by)) {
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
    public boolean conflict(float ex, float ey, float bx, float by) {
        if(bx < ex + 150 && bx + 40 > ex) {
            if (by < ey + 150 && by + 40 > ey) {
                return true;
            }
        }
        return false;
    }

    /**
     * 존재하는 bullet과 enemy를 모두 제거
     * (게임 시작 전에 호출되면서 전체 board를 초기화 할 때 사용)
     */
    public void clear() {
        if(conflictThread != null) {
            conflictThread.interrupt();
        }
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
