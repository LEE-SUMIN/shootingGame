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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        display_width = size.x;
        display_height = size.y;

        infoLayout = (LinearLayout) findViewById(R.id.info);
        start = (Button) findViewById(R.id.start);
        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        skyLayout = (FrameLayout) findViewById(R.id.sky_layout);

        board = Board.getInstance();
        board.initListener(this, this);

        BitmapDrawable spaceshipDrawable = (BitmapDrawable) spaceship.getDrawable();
        spaceshipBitmap = spaceshipDrawable.getBitmap();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //(1) set board
                board.start(3, 5);
                //(2) set life
                lifeViews = new ImageView[board.getLifeLimit()];
                for(int i = 0; i < board.getLifeLimit(); i++){
                    ImageView heart = new ImageView(getApplicationContext());
                    heart.setImageResource(R.drawable.heart);
                    heart.setPadding(15, 0, 15, 0);
                    lifeViews[i] = heart;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(80, 80);
                    infoLayout.addView(heart, params);
                }
                //(3) start
                start.setVisibility(View.INVISIBLE);
                generateEnemy();
            }
        });

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

        btnShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(board.shootAvailable()) {
                    //1. bulletImage 생성
                    ImageView bulletImage = new ImageView(getApplicationContext());
                    bulletImage.setImageResource(R.drawable.bullet);
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(40, 40);
                    skyLayout.addView(bulletImage, param);
                    //2. board에 bullet 생성
                    Bullet bullet = board.shoot(bulletImage);
                    //3. animator 실행
                    AnimatorSet firstSet = bullet.getFirstAnimatorSet();
                    if(firstSet != null) {
                        firstSet.start();
                    }
                }
            }
        });
    }


    private Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) {
        if(degrees == 0) return bitmap;
        Matrix m = new Matrix();
        m.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private void generateEnemy() {
        enemyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    //1. enemy 이미지 생성
                    ImageView enemyImage = new ImageView(getApplicationContext());
                    enemyImage.setImageResource(R.drawable.monster);
                    enemyImage.setPadding(15, 15, 15, 15);
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(150, 150);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            skyLayout.addView(enemyImage, param);
                            //2. board에 enemy 생성
                            Enemy enemy = board.addEnemy(enemyImage);
                            enemyImage.setX(enemy.getX());
                            enemyImage.setY(enemy.getY());
                            enemy.getAnimatorSet().start();
                        }
                    });

                    //3. sleep
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

    @Override
    public void lifeDecrease() {
        int life = board.getLife();
        lifeViews[life].setVisibility(View.GONE);
    }

    @Override
    public void die() {
        if(enemyThread != null) {
            enemyThread.interrupt();
            board.clear();
            Intent intent = new Intent(MainActivity.this, FinishActiivty.class);
            startActivity(intent);
        }
    }

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