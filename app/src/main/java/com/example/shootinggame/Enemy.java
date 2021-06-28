package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class Enemy {
    private final ImageView view; //MainActivity로 부터 전달 받는 Enemy의 ImageView -> Animator를 할당하기 위한 것
    private final int id; //Board에서 각 Enemy를 관리하기 위한 id
    private int velocity; //각 Enemy 마다 랜덤한 속도를 가짐 -> 초기 생성시 자동 결정
    private float x; //Enemy의 현재 x좌표
    private float y; //Enemy의 현재 y좌표
    AnimatorSet animatorSet; //Enemy가 움직일 경로 애니메이션

    private boolean alive; //Enemy의 현재 생존 여부(애니메이션 도중 bullet과 충돌하여 사라진 후에도 계속 진행되는 경우를 막기 위한 변수)


    /**
     * Enemy 객체 생성자
     * @param view : MainActivity에서 전달된 ImageView에 AnimatorSet을 할당하기 위함.
     * @param id : board에서 관리되기 위한 id
     */
    public Enemy(ImageView view, int id) {
        //(1) View, id 할당
        this.view = view;
        this.id = id;
        //(2) view의 초기 위치/속도 설정
        this.x = (float) (Math.random() * (MainActivity.display_width - 200)); //x좌표는 랜덤
        this.y = 0; //y좌표는 화면 최상단에서부터 출발
        this.velocity = (int) (Math.random() * 3000 + 7000); //Enemy의 속도는 Enemy 마다 랜덤으로 결정되며, 한 번 결정되면 일정하게 움직인다. (7~10초)
        this.alive = true;
        //(3) 애니메이션 생성
        this.animatorSet = new AnimatorSet();
        createAnimator();
    }

    /**
     * enemy의 ImageView가 움직일 경로 Animator 생성
     */
    private void createAnimator() {
        float height = MainActivity.display_height * 0.8f - 200f; //enemy가 움직일 수 있는 공간의 높이
        ValueAnimator translationY = ValueAnimator.ofFloat(y, height); //현재 y좌표 위치에서 화면 최하단까지 움직이는 애니메이션
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) { //애니메이션의 frame 마다 호출 -> View의 y좌표 & Enemy 객체의 y좌표 값 갱신
                float valY = (float) animation.getAnimatedValue();
                view.setY(valY);
                y = valY;
            }
        });
        animatorSet.play(translationY);
        animatorSet.setDuration(velocity); //초기에 설정된 랜덤 속도(시간)만큼 애니메이션 재생

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            /**
             * 애니메이션이 종료될 때(=화면 최하단까지 왔을 때) 호출
             * (애니메이션이 종료되는 시점에 해당 Enemy가 살아있음 = User가 놓쳤다는 의미 -> 생명 1개 감소)
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                if(alive) {
                    remove(); //애니메이션이 종료된 Enemy객체는 더이상 필요 없으므로 제거
                    Board.getInstance().removeLife();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * MainActivity에서 Bullet의 AnimatorSet을 얻어 실행하기 위한 함수
     * @return
     */
    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    /**
     * 현재 Enemy 객체 위치의 x좌표 리턴
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * 현재 Enemy 객체 위치의 y좌표 리턴
     * @return
     */
    public float getY() { return y; }

    /**
     * 현재 Enemy 객체의 생존여부 확인
     * @return
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Enemy와 Bullet이 충돌한 경우 또는 Enemy가 화면 밖으로 벗어난 경우 호출되며,
     * 현재 Enemy 객체가 가지는 ImageView, Animator를 제거하고 board 상에서 현재 객체를 제거함.
     */
    public void remove() {
        alive = false;
        view.clearAnimation();
        view.setVisibility(View.GONE);
        Board.getInstance().removeEnemy(id);
    }
}
