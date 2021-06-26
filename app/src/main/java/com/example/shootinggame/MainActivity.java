package com.example.shootinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements LifeListener {
    Display display;
    static int display_width;
    static int display_height;
    int density;

    LinearLayout infoLayout;
    Button start;
    SeekBar seekBar;
    Button btnShoot;
    ImageView spaceship;
    FrameLayout skyLayout;

    Bitmap spaceshipBitmap;
    Board board;
    ImageView[] lifeViews;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        display_width = size.x;
        display_height = size.y;
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        density = outMetrics.densityDpi;

        infoLayout = (LinearLayout) findViewById(R.id.info);
        start = (Button) findViewById(R.id.start);
        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        skyLayout = (FrameLayout) findViewById(R.id.sky_layout);

        board = Board.getInstance();
        board.initListener(this);

        BitmapDrawable spaceshipDrawable = (BitmapDrawable) spaceship.getDrawable();
        spaceshipBitmap = spaceshipDrawable.getBitmap();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.start(1, 5);
                lifeViews = new ImageView[board.getLifeLimit()];
                for(int i = 0; i < board.getLifeLimit(); i++){
                    ImageView heart = new ImageView(getApplicationContext());
                    heart.setImageResource(R.drawable.heart);
                    heart.setPadding(15, 0, 15, 0);
                    lifeViews[i] = heart;
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(80, 80);
                    infoLayout.addView(heart, params);
                }
                start.setVisibility(View.INVISIBLE);
                generateEnemy();
            }
        });

        //TODO: handler
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                
            }
        };

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
                    bulletImage.setPadding(15, 15, 15, 15);
                    FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(60, 60);
                    param.gravity=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
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
        //1. enemy 이미지 생성
        ImageView enemyImage = new ImageView(getApplicationContext());
        enemyImage.setImageResource(R.drawable.monster);
        enemyImage.setPadding(15, 15, 15, 15);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(150, 150);
        param.gravity=Gravity.TOP;
        skyLayout.addView(enemyImage, param);
        //2. board에 bullet 생성
        Enemy enemy = board.addEnemy(enemyImage);
        enemyImage.setX(enemy.getX());
        enemy.getAnimatorSet().start();
    }

    @Override
    public void lifeDecrease() {
        int life = board.getLife();
        lifeViews[life].setVisibility(View.GONE);
    }

    @Override
    public void die() {
        start.setVisibility(View.VISIBLE);
    }


}