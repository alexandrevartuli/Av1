package com.oficial.racefast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log; // Importação necessária para logs

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Car extends Vehicle {
    private Bitmap carBitmap;
    private int fuelTank;
    private int laps;
    private int distance;
    private int penalty;
    private Map<Integer, Integer> sensor;
    private Bitmap trackBitmap;
    private Map<Integer, Boolean> sensorReadings;
    private static Semaphore semaphore = new Semaphore(1); // Semáforo para a região crítica
    private boolean inCriticalRegion = false; // Variável de estado

    public Car(RaceView raceView, Bitmap bitmap, Bitmap trackBitmap, int startX, int startY, double speed, Paint paint) {
        super(raceView, startX, startY, speed, paint);
        this.carBitmap = bitmap;
        this.trackBitmap = trackBitmap;
        this.sensor = new HashMap<>();
        this.fuelTank = 100; // Valor inicial do tanque de combustível
        this.laps = 0;
        this.distance = 0;
        this.penalty = 0;
    }

    @Override
    public void move() {
        try {
            // Atualizar sensores
            updateSensors();

            // Decidir a direção com base nas leituras dos sensores
            decideDirection();

            // Verificar se está na região crítica
            if (isInCriticalRegion()) {
                if (!inCriticalRegion) {
                    // Está entrando na região crítica
                    semaphore.acquire();
                    inCriticalRegion = true;
                    // Log para depuração
                    Log.d("Car", "Carro " + getId() + " entrou na região crítica.");
                }
                // Mover dentro da região crítica
                updatePosition();
            } else {
                if (inCriticalRegion) {
                    // Está saindo da região crítica
                    inCriticalRegion = false;
                    semaphore.release();
                    // Log para depuração
                    Log.d("Car", "Carro " + getId() + " saiu da região crítica.");
                }
                // Mover normalmente
                updatePosition();
            }

            // Verificar colisões
            checkCollisions();
        } catch (InterruptedException e) {
            // Tratamento de interrupção da thread
            running = false;
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Tratamento de exceções genéricas
            e.printStackTrace();
        }
    }

    private void updatePosition() {
        x += (int) (directionX * speed);
        y += (int) (directionY * speed);
        distance += 1;
    }

    private void updateSensors() {
        int detectionDistance = 50;

        int[] sensorAngles = {-60, -30, 0, 30, 60};

        sensorReadings = new HashMap<>();

        for (int angleOffset : sensorAngles) {
            double sensorAngle = Math.toRadians(getCurrentAngle() + angleOffset);
            int sensorX = x + (int) (Math.cos(sensorAngle) * detectionDistance);
            int sensorY = y + (int) (Math.sin(sensorAngle) * detectionDistance);

            boolean onTrack = isOnTrack(sensorX, sensorY);
            sensorReadings.put(angleOffset, onTrack);
        }
    }

    private void decideDirection() {
        if (sensorReadings.get(0)) {
            // Sensor central detecta pista - manter direção
            return;
        } else if (sensorReadings.get(-30)) {
            // Ajustar direção para a esquerda
            adjustDirection(-5);
        } else if (sensorReadings.get(30)) {
            // Ajustar direção para a direita
            adjustDirection(5);
        } else {
            // Nenhum sensor detecta pista - inverter direção
            adjustDirection(180);
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

    private boolean isOnTrack(int x, int y) {
        if (x >= 0 && x < trackBitmap.getWidth() && y >= 0 && y < trackBitmap.getHeight()) {
            int pixelColor = trackBitmap.getPixel(x, y);
            int tolerance = 50;
            int red = Color.red(pixelColor);
            int green = Color.green(pixelColor);
            int blue = Color.blue(pixelColor);
            return red > 255 - tolerance && green > 255 - tolerance && blue > 255 - tolerance;
        }
        return false;
    }

    private boolean isInCriticalRegion() {
        RectF criticalRegion = raceView.getCriticalRegionRect();
        Rect carRect = getRect();

        // Converter carRect para RectF
        RectF carRectF = new RectF(carRect);

        // Verifica se o carro está dentro da região crítica
        return RectF.intersects(carRectF, criticalRegion);
    }

    private void checkCollisions() {
        // Colisão com bordas da pista
        if (x < 0 || x + getWidth() > raceView.getWidth()) {
            directionX *= -1;
            penalty++;
        }
        if (y < 0 || y + getHeight() > raceView.getHeight()) {
            directionY *= -1;
            penalty++;
        }
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

    // Outros métodos conforme necessário
}
