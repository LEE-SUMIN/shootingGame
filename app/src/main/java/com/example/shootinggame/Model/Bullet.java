package com.example.shootinggame.Model;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Bullet extends Item {
    private int angle; //발사될 당시 cannon의 angle
    private List<AnimatorSet> animatorSets; //Bullet이 움직일 경로 애니메이션 묶음 -> 순차적으로 실행되면서 Z형태로 움직임

    private int counter; //animatorSets에서 현재 실행중인 Animator의 index 값 관리
    private int reflection = 0; //현재 Bullet객체의 반사된 횟수



    /**
     * Bullet객체 생성자
     * @param view : MainActivity에서 전달된 ImageView에 AnimatorSet할당하기 위함.
     * @param angle : 발사될 당시 Cannon의 angle값을 받아와서 저장 -> AnimatorSet 생성 시에 사용됨
     * @param id : board에서 관리되기 위한 id
     */
    Bullet(ImageView view, int angle, int id) {
        super(view, id);
        this.angle = angle;
        this.animatorSets = new ArrayList<>();
        //view의 초기 위치 설정(Cannon 위치와 비슷하게 둔다)
        setX(MyDisplay.display_width / 2f - 30f);
        setY(MyDisplay.display_height * 0.8f - 200f);
        //Animator생성: View가 움직일 경로 결정
        createAnimators();
    }

    /**
     * Bullet의 속도를 랜덤하게 결정(1.0 ~ 2.0초)
     * @return
     */
    @Override
    protected int getRandomVelocity() {
        return (int) (Math.random() * 1000 + 1000);
    }

    /**
     * bullet의 ImageView가 가질 Animator 생성
     */
    @Override
    protected void createAnimators() {
        float height = MyDisplay.display_height * 0.8f - 200f; //bullet이 움직일 수 있는 공간의 높이
        //bullet의 각도가 90도인 경우 -> 일직선으로 올라간다.
        if(angle == 90) {
            float nextY = -50; //bullet이 움직여야할 다음 y좌표 = 화면 최상단
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
            animatorSet.play(translationY);
            animatorSet.setDuration(velocity); //초기에 설정된 랜덤 속도(시간)만큼 애니메이션 재생
            animatorSets.add(animatorSet);
        }

        //bullet의 각도가 90도가 아닌 경우
        else {
            //현재 위치
            float curX = x;
            float curY = y;
            //움직여야 할 다음 위치
            float nextX = angle > 90 ? 0 : MyDisplay.display_width;
            float nextY = (float) (height - (MyDisplay.display_width / 2f) * Math.abs(Math.tan(Math.toRadians(angle))));

            int offset = 0; //반사가 진행될수록 y좌표 값을 증가시키기 위한 변수
            
            while(curY >= 0) { //현재 위치가 화면 최상단을 벗어날 때 까지 진행
                offset++;
                
                AnimatorSet animatorSet = new AnimatorSet();
                ValueAnimator translationX = ValueAnimator.ofFloat(curX, nextX); //현재 x좌표 위치 -> 다음 x좌표 위치까지 이동하는 애니메이션
                ValueAnimator translationY = ValueAnimator.ofFloat(curY, nextY); //현재 y좌표 위치 -> 다음 y좌표 위치까지 이동하는 애니메이션
                animatorSet.playTogether(translationX, translationY); //translationX와 translationY 애니메이션을 동시에 실행 -> 대각선 방향으로 움직이게 함
                animatorSet.setDuration(velocity); //초기에 설정된 랜덤 속도(시간)만큼 애니메이션 재생
                animatorSet.setInterpolator(new LinearInterpolator());

                /**
                 * 애니메이션 frame 마다 호출되며 View의 좌표 & Bullet 객체의 좌표 값(x, y) 갱신
                 */
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


                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }

                    /**
                     * Z형태로 움직일 때 "각 직선 = AnimatorSet 하나"를 뜻함 -> 여러 개의 AnimatorSet이 연속적으로 움직여야 함
                     * 하나의 animatorSet 실행 종료 -> List 상 다음 AnimatorSet 실행
                     */
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(view.getY() < 0) { //bullet이 화면을 벗어남
                            remove();
                        }
                        else{
                            reflection++; //하나의 애니메이션이 종료됐고, 아직 화면 상에 존재함 -> 반사가 한 번 일어난 것
                            //다음 차례 AnimatorSet을 실행한다
                            AnimatorSet set = getNextAnimatorSet();
                            if(set != null)
                                set.start();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) { }

                    @Override
                    public void onAnimationRepeat(Animator animation) { }

                });

                animatorSets.add(animatorSet);
                /*다음 Animator를 위한 설정*/
                angle = 180 - angle; //반사되면서 움직이는 방향 전환
                //움직인 위치를 현재 위치로 바꾸고,
                curX = nextX;
                curY = nextY;
                //다음 위치를 설정해준다.
                nextX = angle > 90 ? 0 : MyDisplay.display_width - 40; //x좌표는 움직이는 각도가 90도 미만일 경우 오른쪽 끝, 90도 초과일 경우 왼쪽 끝
                nextY = (float) (height - offset * MyDisplay.display_width * Math.abs(Math.tan(Math.toRadians(angle)))); //y좌표는 처음을 제외하고 항상 (화면 가로 길이) * (tan(angle)) 만큼 이동한다.
            }

        }
    }

    /**
     * Bullet 움직이기 시작
     * @return
     */
    @Override
    public void start() {
        if(animatorSets.size() == 0) return;
        animatorSets.get(0).start();
    }

    /**
     * 다음 실행되어야 할 AnimatorSet 리턴
     * @return
     */
    private AnimatorSet getNextAnimatorSet() {
        counter++;
        if(counter >= animatorSets.size())
            return null;
        return animatorSets.get(counter);
    }

    /**
     * 유효한 bullet = 중간에 enemy와 충돌해서 없어지지 않았고, 반사가 1회 이상 일어난 bullet인지 확인
     * @return
     */
    @Override
    public boolean isValid() {
        if(reflection > 0 && alive) return true;
        return false;
    }

    /**
     * bullet 제거
     */
    @Override
    public void remove() {
        super.remove();
        for(AnimatorSet as : animatorSets) { //진행 중인 애니메이션이 끝나도 아무런 일이 발생하지 않도록 함
            as.removeAllListeners();
        }
        board.removeBullet(id); //board에서 해당 bullet 제거 요청
    }
}
