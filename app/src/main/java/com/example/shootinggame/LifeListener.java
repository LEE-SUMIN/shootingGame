package com.example.shootinggame;

/**
 * Board에서 생명이 하나 감소될 때 마다 호출하여 MainActivity에 알려주기 위한 Listener
 */
public interface LifeListener {
    public void decreaseLife(); //생명이 하나 감소했음을 알려줌
    public void gameOver(); //초기 설정된 생명을 모두 소모했을 경우 알려줌 -> 게임 종료 요청
}
