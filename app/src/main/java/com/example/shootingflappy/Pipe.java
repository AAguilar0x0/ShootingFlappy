package com.example.shootingflappy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

import static com.example.shootingflappy.GameView.screenRatioX;
import static com.example.shootingflappy.GameView.screenRatioY;

public class Pipe {
    public int x = 0, y = 0, width, height, speed = 20;
    private Bitmap pipe;

    public Pipe(Resources res, String type) {
        pipe = BitmapFactory.decodeResource(res,
                (type == "up" ? R.drawable.pipe2 : R.drawable.pipe1));

        width = pipe.getWidth();
        height = pipe.getHeight();

        width /= 4;
        height /= 5;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        pipe = Bitmap.createScaledBitmap(pipe, width, height, false);
    }

    public void randomY(int screenY) {
        Random random = new Random();
        int start = screenY / 3;
        height = random.nextInt(start - 300) + start;
    }

    public Bitmap getPipe() {
        pipe = Bitmap.createScaledBitmap(pipe, width, height, false);
        return pipe;
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
