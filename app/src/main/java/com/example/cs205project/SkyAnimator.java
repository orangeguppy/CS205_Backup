package com.example.cs205project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.concurrent.TimeUnit;

public class SkyAnimator implements Runnable {
    Bitmap frames[] = new Bitmap[2];
    SpaceShooter spaceShooter;
    public SkyAnimator(Context context, SpaceShooter spaceShooter) {
        frames[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.sky1);
        frames[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.sky2);
        this.spaceShooter = spaceShooter;
    }

    @Override
    public void run() {
        boolean originalFrame = false;
        while(true) {
            Bitmap current = originalFrame ? frames[0] : frames[1];
            spaceShooter.setBackground(current);
            originalFrame = !originalFrame;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}