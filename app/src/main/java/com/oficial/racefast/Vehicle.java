package com.oficial.racefast;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public abstract class Vehicle extends Thread {
    protected int x;
    protected int y;
    protected double speed;
    protected double directionX;
    protected double directionY;
    protected Paint paint;
    protected RaceView raceView;
    protected boolean running = true; // Controla se a thread deve continuar rodando

    public Vehicle(RaceView raceView, int startX, int startY, double speed, Paint paint) {
        this.raceView = raceView;
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.paint = paint;
        this.directionX = 1; // Direção inicial
        this.directionY = 0;
    }

    public abstract void move();

    public abstract void draw(Canvas canvas);

    @Override
    public void run() {
        while (running) {
            try {
                move();

                // Controla a taxa de atualização (16ms para ~60 FPS)
                Thread.sleep(16);
            } catch (InterruptedException e) {
                // Interrupção da thread
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Tratamento genérico de exceções
                e.printStackTrace();
            }
        }
    }

    public void stopVehicle() {
        running = false;
        this.interrupt();
    }

    public Rect getRect() {
        return new Rect(x, y, x + getWidth(), y + getHeight());
    }

    public int getWidth() {
        // Deve ser implementado nas subclasses
        return 0;
    }

    public int getHeight() {
        // Deve ser implementado nas subclasses
        return 0;
    }
}