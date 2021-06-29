package com.example.shootinggame.Model;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.widget.ImageView;

public class Enemy extends Item {
    AnimatorSet animatorSet; //Enemy가 움직일 경로 애니메이션


    /**
     * Enemy 객체 생성자
     * @param view : MainActivity에서 전달된 ImageView에 AnimatorSet을 할당하기 위함.
     * @param id : board에서 관리되기 위한 id
     */
    public Enemy(ImageView view, int id) {
        super(view, id);
        setX((float) (Math.random() * (MyDisplay.display_width - 200)));
        setY(0);

        this.velocity = getRandomVelocity();
        this.animatorSet = new AnimatorSet();
        createAnimators();
    }

    @Override
    protected int getRandomVelocity() {
        return (int) (Math.random() * 3000 + 7000);
    }

    /**
     * enemy의 ImageView가 움직일 경로 Animator 생성
     */
    @Override
    protected void createAnimators() {
        float height = MyDisplay.display_height * 0.8f - 200f; //enemy가 움직일 수 있는 공간의 높이
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
                    board.removeLife();
                    remove();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
    }

    @Override
    public void start() {
        animatorSet.start();
    }

    /**
     * Enemy와 Bullet이 충돌한 경우 또는 Enemy가 화면 밖으로 벗어난 경우 호출되며,
     * 현재 Enemy 객체가 가지는 ImageView, Animator를 제거하고 board 상에서 현재 객체를 제거함.
     */
    @Override
    public void remove() {
        super.remove();
        animatorSet.removeAllListeners();
        board.removeEnemy(id);
    }
}
