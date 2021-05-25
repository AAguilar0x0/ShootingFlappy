package com.example.shootingflappy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import static com.example.shootingflappy.GameView.screenRatioX;
import static com.example.shootingflappy.GameView.screenRatioY;

public class MCBird {
    public boolean isGoingUp = false;
    public int x = 0, y, width, height, toShoot = 0;
    private int birdCounter = 1;
    private Bitmap bird1, bird2;
    private GameView gameView;

    public MCBird(GameView gameView, int screenY, Resources res) {
        this.gameView = gameView;

        bird1 = BitmapFactory.decodeResource(res, R.drawable.mcbird1);
        bird2 = BitmapFactory.decodeResource(res, R.drawable.mcbird2);

        width = bird1.getWidth();
        height = bird1.getHeight();

        width /= 20;
        height /= 20;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false);
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false);

        y = screenY/2;
    }

    public Bitmap getMCBird() {
        if (toShoot != 0) {
            toShoot--;
            gameView.newBullet();
        }

        Matrix matrix = new Matrix();

        if (isGoingUp) {
            matrix.postRotate(-25);

            if (birdCounter == 1) {
                birdCounter++;
                return Bitmap.createBitmap(bird1, 0, 0, bird1.getWidth(), bird1.getHeight(), matrix, true);
            }

            birdCounter--;

            return Bitmap.createBitmap(bird2, 0, 0, bird2.getWidth(), bird2.getHeight(), matrix, true);
        }

        matrix.postRotate(45);

        if (birdCounter == 1) {
            birdCounter++;
            return Bitmap.createBitmap(bird1, 0, 0, bird1.getWidth(), bird1.getHeight(), matrix, true);
        }

        birdCounter--;

        return Bitmap.createBitmap(bird2, 0, 0, bird2.getWidth(), bird2.getHeight(), matrix, true);

//        if (birdCounter == 1) {
//            birdCounter++;
//            return bird1;
//        }
//
//        birdCounter--;
//
//        return bird2;
    }

    public Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }
}
