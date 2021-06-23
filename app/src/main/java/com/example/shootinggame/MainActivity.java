package com.example.shootinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    SeekBar seekBar;
    ImageView spaceship;

    Cannon cannon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spaceship = (ImageView) findViewById(R.id.spaceship);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        cannon = new Cannon();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int angle = progress - 90;
                Bitmap spaceship_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
                Matrix rotateMatrix = new Matrix();
                cannon.setAngle(angle);
                rotateMatrix.postRotate(angle);
                Bitmap rotated_spaceship = Bitmap.createBitmap(spaceship_bitmap, 0, 0, spaceship_bitmap.getWidth(), spaceship_bitmap.getHeight(), rotateMatrix, false);
                spaceship.setImageBitmap(rotated_spaceship);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}