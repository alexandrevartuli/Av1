package com.oficial.racefast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.oficial.rtlib.RTTaskManager;
import com.oficial.rtlib.TaskInfo;

public class Car extends Vehicle {
    protected static Semaphore semaphore = new Semaphore(1);
    protected boolean inCriticalRegion = false;

    private Bitmap carBitmap;
    private int fuelTank;
    private int laps;
    private int distance;
    private int penalty;
    private Map<Integer, Integer> sensor;
    private Bitmap trackBitmap;
    private Map<Integer, Boolean> sensorReadings;

    public Car(RaceView raceView, Bitmap bitmap, Bitmap trackBitmap, int startX, int startY, double speed, Paint paint) {
        super(raceView, startX, startY, speed, paint);
        this.carBitmap = bitmap;
        this.trackBitmap = trackBitmap;
        this.sensor = new HashMap<>();
        this.fuelTank = 100;
        this.laps = 0;
        this.distance = 0;
        this.penalty = 0;
    }

    @Override
    public void move() {
        long startTime = System.currentTimeMillis(); // tempo início do ciclo

        try {
            updateSensors();
            decideDirection();

            // Simulação das subtarefas A->B->C para total C=20ms:
            // A: SensorRead 5ms
            Thread.sleep(5);
            // B: DBStore 10ms
            Thread.sleep(10);
            // C: Move 5ms
            Thread.sleep(5);

            if (isInCriticalRegion()) {
                if (!inCriticalRegion) {
                    semaphore.acquire();
                    inCriticalRegion = true;
                    Log.d("Car", "Carro " + getVehicleId() + " entrou na região crítica.");
                }
                updatePosition();
            } else {
                if (inCriticalRegion) {
                    inCriticalRegion = false;
                    semaphore.release();
                    Log.d("Car", "Carro " + getVehicleId() + " saiu da região crítica.");
                }
                updatePosition();
            }

            checkCollisions();

        } catch (InterruptedException e) {
            running = false;
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis(); // tempo fim do ciclo
        long responseTime = endTime - startTime;

        // Obtém TaskInfo deste veículo
        TaskInfo ti = RaceView.getRTManager().getTaskInfoByVehicleId(getVehicleId());
        if (ti == null) {
            Log.d("Car", "Nenhum TaskInfo encontrado para o veículo " + getVehicleId());
        } else {
            // Aqui faz a verificação de deadline
            if (responseTime > ti.D) {
                Log.d("Car", "Carro " + getVehicleId() + " PERDEU o deadline! R_i=" + responseTime + "ms, D_i=" + ti.D + "ms");
            } else {
                Log.d("Car", "Carro " + getVehicleId() + " CUMPRIU o deadline. R_i=" + responseTime + "ms, D_i=" + ti.D + "ms");
            }
        }
    }

    protected void updatePosition() {
        x += (int) (directionX * speed);
        y += (int) (directionY * speed);
        distance += 1;
    }

    protected void updateSensors() {
        int detectionDistance = 50;
        int[] sensorAngles = {-60, -30, 0, 30, 60};
        sensorReadings = new HashMap<>();

        for (int angleOffset : sensorAngles) {
            double sensorAngle = Math.toRadians(getCurrentAngle() + angleOffset);
            int sensorX = x + (int)(Math.cos(sensorAngle)*detectionDistance);
            int sensorY = y + (int)(Math.sin(sensorAngle)*detectionDistance);
            boolean onTrack = isOnTrack(sensorX, sensorY);
            sensorReadings.put(angleOffset, onTrack);
        }
    }

    protected void decideDirection() {
        if (sensorReadings.get(0)) {
            return;
        } else if (sensorReadings.get(-30)) {
            adjustDirection(-5);
        } else if (sensorReadings.get(30)) {
            adjustDirection(5);
        } else {
            adjustDirection(180);
            penalty++;
        }
    }

    protected void checkCollisions() {
        if (x < 0 || x + getWidth() > raceView.getWidth()) {
            directionX *= -1;
            penalty++;
        }
        if (y < 0 || y + getHeight() > raceView.getHeight()) {
            directionY *= -1;
            penalty++;
        }
    }

    private double getCurrentAngle() {
        return Math.toDegrees(Math.atan2(directionY, directionX));
    }

    private void adjustDirection(int angleOffset) {
        double currentAngle = getCurrentAngle();
        double newAngle = currentAngle + angleOffset;
        directionX = Math.cos(Math.toRadians(newAngle));
        directionY = Math.sin(Math.toRadians(newAngle));
    }

    protected boolean isOnTrack(int x, int y) {
        if (x>=0 && x<trackBitmap.getWidth() && y>=0 && y<trackBitmap.getHeight()) {
            int pixelColor = trackBitmap.getPixel(x, y);
            int tolerance = 50;
            int red = Color.red(pixelColor);
            int green = Color.green(pixelColor);
            int blue = Color.blue(pixelColor);
            return red>255-tolerance && green>255-tolerance && blue>255-tolerance;
        }
        return false;
    }

    protected boolean isInCriticalRegion() {
        RectF criticalRegion = raceView.getCriticalRegionRect();
        Rect carRect = getRect();
        RectF carRectF = new RectF(carRect);
        return RectF.intersects(carRectF, criticalRegion);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(carBitmap, x, y, paint);
    }

    @Override
    public int getWidth() {
        return carBitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return carBitmap.getHeight();
    }
}
