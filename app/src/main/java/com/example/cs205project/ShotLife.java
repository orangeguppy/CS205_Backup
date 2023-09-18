package com.example.cs205project;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ShotLife {
    Bitmap shot;
    Context context;
    int shx, shy;
    String shotType;

    public ShotLife(Context context, int shx, int shy, int drawableId) {
        this.context = context;
        this.shotType = shotType;
        this.shx = shx;
        this.shy = shy;
        shot = BitmapFactory.decodeResource(context.getResources(), drawableId);
    }
    public Bitmap getShot(){
        return shot;
    }
    public int getShotWidth() {
        return shot.getWidth();
    }
    public int getShotHeight() {
        return shot.getHeight();
    }
}

