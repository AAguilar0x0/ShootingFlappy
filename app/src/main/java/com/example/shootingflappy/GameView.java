package com.example.shootingflappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    private Thread thread;
    private Background background1, background2;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0, pipesDistance = 400, shootSFX, scoreChangeSFX;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Bat[] bats;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private MCBird mcBird;
    private GameActivity activity;
    private Pipe[] pipes;


    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        shootSFX = soundPool.load(activity, R.raw.shoot, 1);
        scoreChangeSFX = soundPool.load(activity, R.raw.scorechange, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1080f / screenX;  // screen ratio assuming the screen width is 1080 MAX
        screenRatioY = 1920f / screenY;  // screen ratio assuming the screen height is 1920 MAX


        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        mcBird = new MCBird(this, screenY, getResources());
        mcBird.y -= 90 * screenRatioY;

        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        bats = new Bat[1];

        for (int i = 0; i < 1; i++) {
            Bat bat = new Bat(getResources());
            bats[i] = bat;

        }

        random = new Random();

        pipes = new Pipe[6];

        for (int i = 0; i < pipes.length; i++) {
            if (i < pipes.length / 2) {
                pipes[i] = new Pipe(getResources(), "up");
                pipes[i].x = screenX + i * ((pipes[i].width + 200 * screenX / 200) / (pipes.length / 2));
                pipes[i].randomY(screenY);
            } else {
                pipes[i] = new Pipe(getResources(), "down");
                pipes[i].x = pipes[i - ((pipes.length / 2))].x;
                pipes[i].y = pipes[i - (pipes.length / 2)].height +
                        pipesDistance;
                pipes[i].height = screenY - pipes[i].y;
            }
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        if (mcBird.isGoingUp)
            mcBird.y -= 20 * screenRatioY;
        else
            mcBird.y += 20 * screenRatioY;

        if (mcBird.y < 0)
            mcBird.y = 0;

        if (mcBird.y >= screenY - mcBird.height) {
            isGameOver = true;
            return;
        }

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;

            for (Bat bat : bats) {
                if (Rect.intersects(bat.getCollisionShape(), bullet.getCollisionShape())) {
                    score++;
                    soundPool.play(scoreChangeSFX, 1, 1, 0, 0, 1);
                    bat.x = -500;
                    bullet.x = screenX + 500;
                    bat.wasShot = true;
                }
            }
        }

        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (int i = 0; i < pipes.length; i++) {
            if (Rect.intersects(pipes[i].getCollisionShape(), mcBird.getCollisionShape())) {
                isGameOver = true;
                return;
            } else if (mcBird.x + mcBird.width > pipes[i].x + pipes[i].width / 2 &&
                    mcBird.x + mcBird.width <= pipes[i].x + pipes[i].width / 2 + pipes[i].speed &&
                    i < pipes.length / 2) {
                score++;
                soundPool.play(scoreChangeSFX, 1, 1, 0, 0, 1);
            }

            pipes[i].x -= pipes[i].speed;
            if (pipes[i].x + pipes[i].width < 0) {
                pipes[i].x = screenX;
                if (i < (pipes.length / 2)) {
                    pipes[i].randomY(screenY);
                } else {
                    pipes[i].y = pipes[i - (pipes.length / 2)].height +
                            pipesDistance;
                    pipes[i].height = screenY - pipes[i].y;
                }
            }
        }

        for (Bat bat : bats) {
            bat.x -= bat.speed;
            if (bat.x + bat.width < 0) {
                int bound = (int) (30 * screenRatioX);
                bat.speed = random.nextInt(bound);

                if (bat.speed < 10 * screenRatioX)
                    bat.speed = (int) (10 * screenRatioX);

                bat.x = screenX;
                bat.y = random.nextInt(screenY - bat.height);

                bat.wasShot = false;
            }

            if (Rect.intersects(bat.getCollisionShape(), mcBird.getCollisionShape())) {
                isGameOver = true;
                return;
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Pipe pipe : pipes)
                canvas.drawBitmap(pipe.getPipe(), pipe.x, pipe.y, paint);

            for (Bat bat : bats)
                canvas.drawBitmap(bat.getBat(), bat.x, bat.y, paint);

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(mcBird.getMCBird(), mcBird.x, mcBird.y, paint);

                LinearLayout layout = new LinearLayout(activity);

                TextView textView = new TextView(activity);
                textView.setVisibility(View.VISIBLE);
                textView.setText("Game Over");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setBackgroundResource(R.drawable.tv_config);
                layout.addView(textView);

                layout.setGravity(Gravity.CENTER);
                layout.measure(canvas.getWidth(), canvas.getHeight());
                layout.layout(0, 0, canvas.getWidth(), canvas.getHeight());
                layout.draw(canvas);

                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting();
                return;
            }

            canvas.drawBitmap(mcBird.getMCBird(), mcBird.x, mcBird.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void waitBeforeExiting() {
        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void saveIfHighScore() {
        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2) {
                    mcBird.isGoingUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mcBird.isGoingUp = false;
                if (event.getX() > screenX / 2)
                    mcBird.toShoot++;
                break;
        }

        return true;
    }

    public void newBullet() {
        soundPool.play(shootSFX, 1, 1, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = mcBird.x + mcBird.width;
        bullet.y = mcBird.y + (mcBird.height / 2);
        bullets.add(bullet);
    }
}
