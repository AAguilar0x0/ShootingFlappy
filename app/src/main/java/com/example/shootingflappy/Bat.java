package com.example.shootingflappy;


import android.graphics.Rect;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.example.shootingflappy.GameView.screenRatioX;
import static com.example.shootingflappy.GameView.screenRatioY;

public class Bat {
    public boolean wasShot = true;
    public int x = 0, y, width, height, speed = 20;
    private int batCounter = 1;
    private Bitmap bat1, bat2;

    public Bat(Resources res) {
        bat1 = BitmapFactory.decodeResource(res, R.drawable.bat1);
        bat2 = BitmapFactory.decodeResource(res, R.drawable.bat2);

        width = bat1.getWidth();
        height = bat1.getHeight();

        width /= 7;
        height /= 7;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bat1 = Bitmap.createScaledBitmap(bat1, width, height, false);
        bat2 = Bitmap.createScaledBitmap(bat2, width, height, false);

        y = -height;
    }

    public Bitmap getBat() {
        if (batCounter == 1) {
            batCounter++;
            return bat1;
        }

        batCounter = 1;

        return bat2;
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
