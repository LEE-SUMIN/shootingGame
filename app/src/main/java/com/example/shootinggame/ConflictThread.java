package com.example.shootinggame;

import com.example.shootinggame.Model.Board;

public class ConflictThread extends Thread {
    private boolean interrupt;
    private Board board;

    ConflictThread() {
        this.interrupt = false;
        this.board = Board.getInstance();
    }

    @Override
    public void run() {
        while(!interrupt) {
            board.detectConflict();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        interrupt = true;
    }
}
