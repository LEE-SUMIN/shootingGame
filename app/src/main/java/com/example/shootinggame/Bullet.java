package com.example.shootinggame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Interpolator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Bullet {
    private final ImageView view; //MainActivity로 전달 받는 Bullet의 ImageView -> Animator를 할당하기 위한 것
    private final int id; //Board에서 각 Bullet들을 관리하기 위한 id
    private final int velocity; //각 Bullet 마다 랜덤한 속도를 가짐 -> 초기 생성시 자동 결정
    private int angle; //발사될 당시 cannon의 angle
    private float x; //Bullet의 현재 x좌표
    private float y; //Bullet의 현재 y좌표
    private List<AnimatorSet> animatorSets; //Bullet이 움직일 경로 애니메이션 묶음 -> 순차적으로 실행되면서 Z형태로 움직임

    private int counter; //animatorSets에서 현재 실행중인 Animator의 index 값 관리
    private int reflection = 0; //현재 Bullet객체의 반사된 횟수

    private Board board;


    /**
     * Bullet객체 생성자
     * @param view : MainActivity에서 전달된 ImageView에 AnimatorSet할당하기 위함.
     * @param angle : 발사될 당시 Cannon의 angle값을 받아와서 저장 -> AnimatorSet 생성 시에 사용됨
     * @param id : board에서 관리되기 위한 id
     */
    Bullet(ImageView view, int angle, int id) {
        this.view = view;
        this.id = id;
        //view의 초기 위치 설정(Cannon 위치와 비슷하게 둔다)
        view.setX(MainActivity.display_width / 2f - 30f);
        view.setY(MainActivity.display_height * 0.8f - 200f);
        this.x = view.getX();
        this.y = view.getY();
        
        this.angle = angle;
        //Bullet의 속도는 Bullet 마다 랜덤으로 결정되며, 한 번 결정되면 일정하게 움직인다.
        this.velocity = (int) (Math.random() * 1000 + 500);
        //지그재그로 반사되면서 움직이기 때문에 AnimatorSet들을 담을 수 있는 ArrayList로 관리
        animatorSets = new ArrayList<>();
        //bullet을 포함하고 있는 board 객체
        board = Board.getInstance();
        //Animator생성
        createAnimators();
    }

    /**
     * bullet의 ImageView가 가질 Animator 생성
     */
    private void createAnimators() {
        float height = MainActivity.display_height * 0.8f - 200f; //bullet이 움직일 수 있는 공간의 높이
        //bullet의 각도가 90도인 경우 -> 일직선으로 올라간다.
        if(angle == 90) {
            float nextY = -50; //bullet이 움직여야할 다음 y좌표
            AnimatorSet animatorSet = new AnimatorSet();
            ValueAnimator translationY = ValueAnimator.ofFloat(this.y, nextY); //현재 y위치에서 다음 y위치까지 움직이는 애니메이션
            translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) { //애니메이션의 frame 마다 호출 -> View의 y좌표 & Bullet 객체의 y좌표 값 갱신
                    float yVal = (float) animation.getAnimatedValue();
                    view.setY(yVal);
                    y = yVal;
                }
            });
            animatorSet.playTogether(translationY);
            animatorSet.setDuration(velocity); //초기에 설정된 랜덤 속도(시간)만큼 애니메이션 재생
            animatorSets.add(animatorSet);
        }

        //bullet의 각도가 90도가 아닌 경우
        else {
            //현재 위치
            float curX = x;
            float curY = y;
            //움직여야 할 다음 위치
            float nextX = angle > 90 ? 0 : MainActivity.display_width;
            float nextY = (float) (height - (MainActivity.display_width / 2f) * Math.abs(Math.tan(Math.toRadians(angle))));
            //반사가 진행될수록 움직여야 할 다음 위치의 y좌표 값을 증가시키기 위한 변수
            int offset = 0;
            
            while(curY >= 0) { //현재 위치가 화면 최상단을 벗어날 때 까지 진행
                offset++;
                //x좌표 이동 범위 조정(bullet 너비 만큼 빼기)
                float limit_x = nextX < MainActivity.display_width / 2f ? nextX : nextX - 40;
                
                AnimatorSet animatorSet = new AnimatorSet();
                ValueAnimator translationX = ValueAnimator.ofFloat(curX, limit_x); //현재 x좌표 위치 -> 다음 x좌표 위치까지 이동하는 애니메이션
                ValueAnimator translationY = ValueAnimator.ofFloat(curY, nextY); //현재 y좌표 위치 -> 다음 y좌표 위치까지 이동하는 애니메이션
                animatorSet.playTogether(translationX, translationY); //translationX와 translationY 애니메이션을 동시에 실행 -> 대각선 방향으로 움직이게 함
                animatorSet.setDuration(velocity); //초기에 설정된 랜덤 속도(시간)만큼 애니메이션 재생
                
                //애니메이션 frame 마다 호출되며 View의 좌표 & Bullet 객체의 좌표 값 갱신
                translationX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float xVal = (float) animation.getAnimatedValue();
                        view.setX(xVal);
                        x = xVal;
                    }
                });
                translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float yVal = (float) animation.getAnimatedValue();
                        view.setY(yVal);
                        y = yVal;
                    }
                });
                
                //지그재그로 움직이려면 "각 직선 = AnimatorSet 하나"를 뜻함 -> 여러 개의 AnimatorSet이 연속적으로 움직여야 지그재그로 움직임
                //하나의 animatorSet 실행 종료 -> List 상 다음 AnimatorSet 실행
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(view.getY() < 0) { //bullet이 화면 상을 벗어남 -> bullet 제거
                            remove();
                        }
                        else{
                            reflection++; //하나의 애니메이션이 종료됐고, 아직 y좌표가 0보다 작다면(화면 상에 존재한다면) 반사가 한 번 일어난 것 -> reflection 변수 증가
                            AnimatorSet set = getNextAnimatorSet(); //다음 차례 AnimatorSet을 가져와서
                            if(set != null)                         //null이 아니라면
                                set.start();                        //실행한다
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) { }

                    @Override
                    public void onAnimationRepeat(Animator animation) { }

                });

                animatorSets.add(animatorSet);
                //다음 Animator를 위한 설정
                angle = 180 - angle; //반사되면서 움직이는 방향 전환됨
                //움직인 위치를 현재 위치로 바꾸고,
                curX = limit_x;
                curY = nextY;
                //다시 다음 위치를 설정해준다.
                nextX = angle > 90 ? 0 : MainActivity.display_width; //x좌표는 움직이는 각도가 90도 미만일 경우 오른쪽 끝, 90도 초과일 경우 왼쪽 끝
                nextY = (float) (height - offset * MainActivity.display_width * Math.abs(Math.tan(Math.toRadians(angle)))); //y좌표는 처음을 제외하고 항상 (화면 가로 길이) * (tan(angle)) 만큼 이동한다.
            }

        }
    }

    /**
     * AnimatorSets가 가지는 첫 번쨰 AnimatorSet 리턴
     * @return
     */
    public AnimatorSet getFirstAnimatorSet() {
        if(animatorSets.size() == 0) return null;
        return animatorSets.get(0);
    }

    /**
     * 다음 실행되어야 할 AnimatorSet 리턴
     * @return
     */
    public AnimatorSet getNextAnimatorSet() {
        counter++;
        if(counter >= animatorSets.size())
            return null;
        return animatorSets.get(counter);
    }

    /**
     * 현재 Bullet 객체 위치의 x좌표 리턴
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     * 현재 Bullet 객체 위치의 y좌표 리턴
     * @return
     */
    public float getY() {
        return y;
    }

    /**
     * Bullet이 Enemy와 충돌한 경우 또는 Bullet이 화면을 벗어난 경우 호출되며,
     * 현재 Bullet 객체가 가지는 ImageView, Animator를 제거하고 board 상에서 제거함.
     */
    public void remove() {
        view.clearAnimation();
        view.setVisibility(View.GONE);
        board.removeBullet(id);
    }
}
