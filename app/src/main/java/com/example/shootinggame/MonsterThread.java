package com.example.shootinggame;

public class MonsterThread extends Thread {
    @Override
    public void run() {
        while(true) {
            try {
                int time = (int) Math.random() * 1000;
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
