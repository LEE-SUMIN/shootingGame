package com.example.shootinggame;

/**
 * Board에서 생명이 하나 감소될 때 마다 호출하여 MainActivity에 알려주기 위한 Listener
 */
public interface LifeListener {
    public void decreaseLife(int life); //생명이 하나 감소했음을 알려줌
}
