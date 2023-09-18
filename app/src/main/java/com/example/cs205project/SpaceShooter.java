package com.example.cs205project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class SpaceShooter extends View {

    public final static Object healthMutex = new Object();
    Context context;
    Bitmap background, lifeImage, heartImage;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 3;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    OurSpaceship ourSpaceship;
    EnemySpaceship enemySpaceship;
    Random random;
    ArrayList<ShotLife> enemyShots, ourShots;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyShotAction = false;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };


    public SpaceShooter(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        enemyShots = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        ourSpaceship = new OurSpaceship(context);
        enemySpaceship = new EnemySpaceship(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.sky1);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        heartImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        // Begin the animation thread
        SkyAnimator skyAnimator = new SkyAnimator(context, this);
        Thread t = new Thread(skyAnimator);
        t.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw background, Points and life on Canvas
        canvas.drawBitmap(background, null, new Rect(0,0,screenWidth + 200,screenHeight), null);
        canvas.drawText("GPA: " + points, 0, TEXT_SIZE, scorePaint);
        for(int i = life; i >= 1; i--){
            canvas.drawBitmap(heartImage, screenWidth - heartImage.getWidth() * i, 0, null);

        }
        // When life becomes 0, stop game and launch GameOver Activity with points
        if (life == 0) {
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        // Move enemySpaceship
        enemySpaceship.ex += enemySpaceship.enemyVelocity;
        // If enemySpaceship collides with right wall, reverse enemyVelocity
        if (enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() >= screenWidth) {
            enemySpaceship.enemyVelocity *= -1;
        }
        // If enemySpaceship collides with left wall, again reverse enemyVelocity
        if (enemySpaceship.ex <= 0) {
            enemySpaceship.enemyVelocity *= -1;
        }
        // Till enemyShotAction is false, enemy should fire shots from random travelled distance
        if (enemyShotAction == false) {
            int healthOrShot = random.nextInt(10);
            ShotLife enemyShot = (healthOrShot < 7) ? new Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey) :
                    new Life(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey);
            enemyShots.add(enemyShot);
            // We're making enemyShotAction to true so that enemy can take a short at a time
            enemyShotAction = true;
        }
        // Draw the enemy Spaceship
        canvas.drawBitmap(enemySpaceship.getEnemySpaceship(), enemySpaceship.ex, enemySpaceship.ey, null);
        // Draw our spaceship between the left and right edge of the screen
        if (ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()) {
            ourSpaceship.ox = screenWidth - ourSpaceship.getOurSpaceshipWidth();
        } else if (ourSpaceship.ox < 0) {
            ourSpaceship.ox = 0;
        }
        // Draw our Spaceship
        canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox, ourSpaceship.oy, null);
        // Draw the enemy shot downwards our spaceship and if it's being hit, decrement life, remove
        // the shot object from enemyShots ArrayList and show an explosion.
        // Else if, it goes away through the bottom edge of the screen also remove
        // the shot object from enemyShots.
        // When there is no enemyShots no the screen, change enemyShotAction to false, so that enemy
        // can shot.

        final Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);

        for (int i = 0; i < enemyShots.size(); i++) {
            enemyShots.get(i).shy += 20;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if (enemyShots.get(i) instanceof Shot) {
                if ((enemyShots.get(i).shx >= ourSpaceship.ox)
                        && enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                        && enemyShots.get(i).shy >= ourSpaceship.oy
                        && enemyShots.get(i).shy <= screenHeight) {

                    // ============================ VIBRATION ==============================
                    final VibrationEffect vibrationEffect;

                    // this is the only type of the vibration which requires system version Oreo (API 26)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                        // this effect creates the vibration of default amplitude for 1000ms(1 sec)
                        vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);

                        // it is safe to cancel other vibrations currently taking place
                        vibrator.cancel();
                        vibrator.vibrate(vibrationEffect);
                    }
                    // ====================================================================
                    Thread updateLife = new Thread() {
                        public void run() {
                            synchronized (healthMutex) {
                                life--;
                            }
                        }
                    };
                    updateLife.start();
                    try {
                        updateLife.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    enemyShots.remove(i);
                    explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                    explosions.add(explosion);
                } else if (enemyShots.get(i).shy >= screenHeight) {
                    enemyShots.remove(i);
                }
                if (enemyShots.size() < 1) {
                    enemyShotAction = false;
                }
            } else if (enemyShots.get(i) instanceof Life) {
                if ((enemyShots.get(i).shx >= ourSpaceship.ox)
                        && enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                        && enemyShots.get(i).shy >= ourSpaceship.oy
                        && enemyShots.get(i).shy <= screenHeight) {

                    Thread updateLife = new Thread() {
                        public void run() {
                            synchronized (healthMutex) {
                                life++;
                            }
                        }
                    };
                    updateLife.start();
                    try {
                        updateLife.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    enemyShots.remove(i);
                    explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                    explosions.add(explosion);
                } else if (enemyShots.get(i).shy >= screenHeight) {
                    enemyShots.remove(i);
                }
                if (enemyShots.size() < 1) {
                    enemyShotAction = false;
                }
            }
        }
        // Draw our spaceship shots towards the enemy. If there is a collision between our shot and enemy
        // spaceship, increment points, remove the shot from ourShots and create a new Explosion object.
        // Else if, our shot goes away through the top edge of the screen also remove
        // the shot object from enemyShots ArrayList.
        for (int i = 0; i < ourShots.size(); i++) {
            ourShots.get(i).shy -= 20;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if ((ourShots.get(i).shx >= enemySpaceship.ex)
                    && ourShots.get(i).shx <= enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth()
                    && ourShots.get(i).shy <= enemySpaceship.getEnemySpaceshipWidth()
                    && ourShots.get(i).shy >= enemySpaceship.ey) {
                points++;
                ourShots.remove(i);
                explosion = new Explosion(context, enemySpaceship.ex, enemySpaceship.ey);
                explosions.add(explosion);
            } else if (ourShots.get(i).shy <= 0) {
                ourShots.remove(i);
            }
        }
        // Do the explosion
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 8) {
                explosions.remove(i);
            }
        }
        // If not paused, we’ll call the postDelayed() method on handler object which will cause the
        // run method inside Runnable to be executed after 30 milliseconds, that is the value inside
        // UPDATE_MILLIS.
        if (!paused)
            handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        // When event.getAction() is MotionEvent.ACTION_UP, if ourShots arraylist size < 1,
        // create a new Shot.
        // This way we restrict ourselves of making just one shot at a time, on the screen.
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (ourShots.size() < 1) {
                ShotLife ourShot = new Shot(context, ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth() / 2, ourSpaceship.oy);
                ourShots.add(ourShot);
            }
        }
        // When event.getAction() is MotionEvent.ACTION_DOWN, control ourSpaceship
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX;
        }
        // When event.getAction() is MotionEvent.ACTION_MOVE, control ourSpaceship
        // along with the touch.
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX;
        }
        // Returning true in an onTouchEvent() tells Android system that you already handled
        // the touch event and no further handling is required.
        return true;
    }

    public void setBackground(Bitmap bitmap) {
        this.background =  bitmap;
    }
}

