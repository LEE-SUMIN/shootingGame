package com.example.shootinggame;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LifeListener, ConflictListener {
    Display display;
    static int display_width;
    static int display_height;

    LinearLayout infoLayout;
    Button start;
    SeekBar seekBar;
    Button btnShoot;
    ImageView spaceship;
    FrameLayout skyLayout;

    Bitmap spaceshipBitmap;
    Board board;
    ImageView[] lifeViews;

    Thread enemyThread;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Display 크기 값
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        display_width = size.x;
        display_height = size.y;
        
        //각종 View 초기화
        infoLayout = (LinearLayout) findViewById(R.id.info);
        start = (Button) findViewById(R.id.start);
        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        skyLayout = (FrameLayout) findViewById(R.id.sky_layout);

        //board 생성
        board = Board.getInstance();
        board.initListener(this, this);

        //cannon ImageView의 Bitmap 생성(회전하기 위함)
        spaceshipBitmap = getBitmap(spaceship);
        
        //start 버튼 클릭 -> 게임 시작
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //(1) set board
                board.start(3, 5);
                //(2) set life : 주어진 생명 개수 만큼 heart ImageView 추가
                lifeViews = new ImageView[board.getLifeLimit()];
                for(int i = 0; i < board.getLifeLimit(); i++){
                    ImageView heart = new ImageView(getApplicationContext());
                    heart.setImageResource(R.drawable.heart);
                    heart.setPadding(15, 0, 15, 0);
                    lifeViews[i] = heart;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(80, 80);
                    infoLayout.addView(heart, params);
                }
                //(3) start -> enemy 생성Thread 시작
                start.setVisibility(View.INVISIBLE);
                generateEnemy();
            }
        });
        
        //seekbar 조정 -> cannon 각도 조정 + cannon ImageView 회전
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rotate_angle = progress - 90;
                board.getCannon().setAngle(180 - progress);
                Bitmap rotated_spaceship = getRotatedBitmap(spaceshipBitmap, rotate_angle);
                spaceship.setImageBitmap(rotated_spaceship);
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
                    Bullet bullet = board.shoot(bulletImage);
                    //(3) bullet animator 실행
                    AnimatorSet firstSet = bullet.getFirstAnimatorSet();
                    if(firstSet != null) {
                        firstSet.start();
                    }
                }
            }
        });
    }

    /**
     * Cannon의 ImageView를 Bitmap으로 변환 (회전하기 위함)
     * @param view
     * @return
     */
    private Bitmap getBitmap(ImageView view) {
        BitmapDrawable spaceshipDrawable = (BitmapDrawable) view.getDrawable();
        return spaceshipDrawable.getBitmap();
    }

    /**
     * Bitmap을 주어진 각도 만큼 회전
     * @param bitmap : Cannon의 Bitmap 
     * @param degrees : 회전 시킬 각도
     * @return
     */
    private Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) {
        if(degrees == 0) return bitmap;
        Matrix m = new Matrix();
        m.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    /**
     * Enemy 생성 thread 실행
     */
    private void generateEnemy() {
        enemyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
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
                            enemyImage.setX(enemy.getX());
                            enemyImage.setY(enemy.getY());
                            enemy.getAnimatorSet().start();
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
     * LifeListener 인터페이스 구현 함수 -> Board에서 생명 감소할 때 호출됨
     */
    @Override
    public void decreaseLife() {
        //생명 개수를 나타내는 heart ImageView 조정
        int life = board.getLife();
        lifeViews[life].setVisibility(View.GONE);
    }

    /**
     * LifeListener 인터페이스 구현 함수 -> Board에서 주어진 생명을 모두 소모한 경우 호출됨
     */
    @Override
    public void die() {
        if(enemyThread != null) {
            //enemy 생성 Thread 중지
            enemyThread.interrupt();
            //board 상에 남아있는 enemy, bullet 객체 제거
            board.clear();
            //FinishActivity로 전환
            Intent intent = new Intent(MainActivity.this, FinishActiivty.class);
            startActivity(intent);
        }
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
                Log.d("테스트", "eid: " + e.getId() + " bid: " + b.getId());
                e.remove();
                b.remove();
            }
        });
    }
}