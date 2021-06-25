package com.example.shootinggame;

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
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Display display;
    static int display_width;
    static int display_height;
    int density;

    SeekBar seekBar;
    Button btnShoot;
    ImageView spaceship;
    FrameLayout skyLayout;

    Bitmap spaceshipBitmap;
    Board board;

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


        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnShoot = (Button) findViewById(R.id.btnShoot);
        skyLayout = (FrameLayout) findViewById(R.id.sky_layout);

        board = Board.getInstance();
        BitmapDrawable spaceshipDrawable = (BitmapDrawable) spaceship.getDrawable();
        spaceshipBitmap = spaceshipDrawable.getBitmap();

        generateEnemy();

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
        ImageView enemyImage = new ImageView(getApplicationContext());
        enemyImage.setImageResource(R.drawable.monster);
        enemyImage.setPadding(15, 15, 15, 15);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(100, 100);
        param.gravity=Gravity.TOP;
        skyLayout.addView(enemyImage, param);

        board.addEnemy(enemyImage);
    }
}