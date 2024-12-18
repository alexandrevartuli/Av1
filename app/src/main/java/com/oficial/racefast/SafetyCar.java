package com.oficial.racefast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class SafetyCar extends Car {

    public SafetyCar(RaceView raceView, Bitmap bitmap, Bitmap trackBitmap, int startX, int startY, double speed, Paint paint) {
        super(raceView, bitmap, trackBitmap, startX, startY, speed, paint);
        this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void move() {
        try {
            double originalSpeed = speed;
            speed = 6.0; // velocidade aumentada

            // Chama os métodos herdados do Car, que não lançam InterruptedException
            updateSensors();
            decideDirection();

            if (isInCriticalRegion()) {
                // Safety Car não usa semáforo, passa direto
                updatePosition();
            } else {
                updatePosition();
            }

            checkCollisions();

            speed = originalSpeed;
            // Remover o catch (InterruptedException e), já que não há esse tipo de exceção lançada
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
