package com.example.shootinggame;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.shootinggame.Model.Board;
import com.example.shootinggame.Model.Bullet;
import com.example.shootinggame.Model.Enemy;
import com.example.shootinggame.Model.MyDisplay;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements LifeListener, ConflictListener {
    LinearLayout infoLayout;
    TextView score;
    Button start;
    SeekBar seekBar;
    Button btnShoot;
    ImageView spaceship;
    FrameLayout skyLayout;

    Board board;
    ImageView[] lifeViews;

    Thread enemyThread;
    Thread conflictThread;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Display 크기 값
        MyDisplay display = new MyDisplay(getWindowManager().getDefaultDisplay());
        
        //각종 View 초기화
        infoLayout = (LinearLayout) findViewById(R.id.info);
        start = (Button) findViewById(R.id.start);
        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        skyLayout = (FrameLayout) findViewById(R.id.sky_layout);

        //board 생성
        board = Board.getInstance();
        board.init(this);
        
        //start 버튼 클릭 -> 게임 시작
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lifeLimit = 3;
                int bulletLimit = 5;
                //(1) set board
                board.start(lifeLimit, bulletLimit);
                //(2) 주어진 생명 개수 만큼 heart ImageView 추가
                setLifeViews(board.getLife());
                //(3) enemy 생성 & 충돌 감지 시작
                start.setVisibility(View.INVISIBLE);
                btnShoot.setActivated(true);
                seekBar.setActivated(true);
                startEnemyThread();
                startConflictThread();
            }
        });
        
        //seekbar 조정 -> cannon 각도 조정 + cannon ImageView 회전
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                board.getCannon().setAngle(180 - progress);
                spaceship.setRotation(progress - 90);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        
        //shoot 버튼 클릭 -> bullet 생성
        btnShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //화면 상에 존재하는 bullet이 일정 개수를 넘지 않았을 경우,
                if(board.shootAvailable()) {
                    //(1) bulletImage 생성
                    ImageView bulletImage = new ImageView(getApplicationContext());
                    bulletImage.setImageResource(R.drawable.bullet);
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(40, 40);
                    skyLayout.addView(bulletImage, param);
                    //(2) board에 bullet 생성
                    Bullet b = board.addBullet(bulletImage);
                    b.start();
                }
            }
        });
    }

    /**
     * 화면 좌측 상단에 생명 개수만큼 heart ImageView 생성
     * @param lifeLimit
     */
    private void setLifeViews(int lifeLimit) {
        lifeViews = new ImageView[board.getLife()];
        for(int i = 0; i < board.getLife(); i++){
            ImageView heart = new ImageView(getApplicationContext());
            heart.setImageResource(R.drawable.heart);
            heart.setPadding(15, 0, 15, 0);
            lifeViews[i] = heart;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(80, 80);
            infoLayout.addView(heart, params);
        }
    }

    /**
     * Enemy 생성 thread 실행
     */
    private void startEnemyThread() {
        enemyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //게임이 실행 중인 경우에만 실행되도록 함
                while(board.isRunning()) {
                    //(1) enemy 이미지 생성
                    ImageView enemyImage = new ImageView(getApplicationContext());
                    enemyImage.setImageResource(R.drawable.monster);
                    enemyImage.setPadding(15, 15, 15, 15);
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(150, 150);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            skyLayout.addView(enemyImage, param);
                            //(2) board에 enemy 생성
                            Enemy enemy = board.addEnemy(enemyImage);
                            enemy.start();
                        }
                    });

                    //(3) sleep : enemy 생성 시간도 랜덤하게 주기 위해 sleep 시간 랜덤으로 설정(0.5 ~ 3.5초)
                    try {
                        int t = (int) (Math.random() * 3000 + 500);
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        enemyThread.start();
    }

    /**
     * 충돌 감지 thread 실행
     */
    private void startConflictThread() {
        conflictThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //게임이 실행 중인 경우에만 실행되도록 함
                while(board.isRunning()) {
                    board.detectConflict();
                }
            }
        });
        conflictThread.start();
    }

    /**
     * LifeListener 인터페이스 구현 함수 -> Board에서 생명 감소할 때 호출됨
     */
    @Override
    public void decreaseLife() {
        //줄어든 생명 개수에 맞게 heart ImageView 조정
        int life = board.getLife();
        lifeViews[life].setVisibility(View.GONE);
    }

    /**
     * LifeListener 인터페이스 구현 함수 -> Board에서 주어진 생명을 모두 소모한 경우 호출됨
     */
    @Override
    public void gameOver() {
        //board 상에 남아있는 enemy, bullet 객체 제거
        board.clear();
        skyLayout.removeAllViews();
        btnShoot.setActivated(false);
        seekBar.setActivated(false);
        //FinishActivity로 전환
        Intent intent = new Intent(MainActivity.this, FinishActiivty.class);
        startActivity(intent);
    }

    /**
     * ConflictListener 인터페이스 구현 함수 -> Board에서 bullet과 enemy의 충돌을 감지한 경우 호출됨
     * @param e : 제거할 enemy 객체
     * @param b : 제거할 bullet 객체
     */
    @Override
    public void conflict(Enemy e, Bullet b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                e.remove();
                b.remove();
            }
        });
    }
}