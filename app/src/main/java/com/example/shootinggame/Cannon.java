package com.example.shootinggame;

public class Cannon {
    private static Cannon cannon;
    private int angle;

    private Cannon() {
        this.angle = 90;
    }
    public static Cannon getInstance() {
        if(cannon == null){
            cannon = new Cannon();
        }
        return cannon;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
