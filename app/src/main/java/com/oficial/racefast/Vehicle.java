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
    protected boolean running = true;

    private static int idCounter = 0;
    private int id; // identificador exclusivo do veículo

    public Vehicle(RaceView raceView, int startX, int startY, double speed, Paint paint) {
        this.raceView = raceView;
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.paint = paint;
        this.directionX = 1;
        this.directionY = 0;
        this.id = idCounter++; // incrementa a cada novo veículo criado
    }

    public abstract void move();
    public abstract void draw(Canvas canvas);

    @Override
    public void run() {
        while (running) {
            try {
                move();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
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
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    // Renomear o método para evitar conflito com Thread.getId()
    public int getVehicleId() {
        return id;
    }
}
