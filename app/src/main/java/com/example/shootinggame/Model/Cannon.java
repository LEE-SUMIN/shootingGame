package com.example.shootinggame.Model;

public class Cannon {
    private static Cannon cannon;
    private int angle;

    /**
     * 전체 게임 상에서 Cannon은 하나만 존재해야 하므로 Singleton Pattern 적용
     */
    private Cannon() {
        this.angle = 90; //처음 시작 각도는 90도로 세팅되어 있다.
    }
    public static Cannon getInstance() {
        if(cannon == null){
            cannon = new Cannon();
        }
        return cannon;
    }

    /**
     * 외부에서 angle 값을 얻기 위한 getter 함수
     * -> Board에서 Bullet을 생성할 때 호출됨
     * @return
     */
    public int getAngle() {
        return angle;
    }

    /**
     * 외부에서 angle 값을 수정하기 위한 setter 함수
     * -> MainActivity에서 SeekBar를 조정할 때 호출됨
     * @param angle
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }
}
