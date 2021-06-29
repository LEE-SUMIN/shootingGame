package com.example.shootinggame.Model;

import android.graphics.Point;
import android.view.Display;

public class MyDisplay {
    Display display;
    static int display_width;
    static int display_height;

    /**
     * display 크기 상수 관리
     * @param display
     */
    public MyDisplay(Display display) {
        this.display = display;
        Point size = new Point();
        display.getRealSize(size);
        display_width = size.x;
        display_height = size.y;
    }
}
