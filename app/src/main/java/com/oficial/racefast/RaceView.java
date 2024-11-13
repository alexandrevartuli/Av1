package com.oficial.racefast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF; // Importar RectF
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RaceView extends View {
    private Bitmap trackBitmap;
    private Bitmap scaledTrackBitmap;
    private List<Vehicle> vehicles;

    // Coordenadas e dimensões da região crítica
    private float criticalRegionX;
    private float criticalRegionY;
    private float criticalRegionWidth;
    private float criticalRegionHeight;

    private Handler uiHandler;
    private Runnable uiRunnable;

    public RaceView(Context context) {
        super(context);
        init();
    }

    public RaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        trackBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pista);
        vehicles = new ArrayList<>();

        uiHandler = new Handler(Looper.getMainLooper());
        uiRunnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // Redesenhar a tela
                uiHandler.postDelayed(this, 16); // Chamar novamente após 16ms (~60 FPS)
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scaledTrackBitmap = Bitmap.createScaledBitmap(trackBitmap, w, h, true);

        // Definir posição e dimensões da região crítica
        criticalRegionWidth = 200f; // Largura da região crítica
        criticalRegionHeight = 100f; // Altura da região crítica

        // Centralizar horizontalmente e verticalmente
        criticalRegionX = (w - criticalRegionWidth) / 2.0f;
        criticalRegionY = 0.7f*(h - criticalRegionHeight) ;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Desenhar a pista escalonada
        canvas.drawBitmap(scaledTrackBitmap, 0, 0, null);

        // Desenhar a região crítica
        Paint criticalRegionPaint = new Paint();
        criticalRegionPaint.setColor(Color.argb(100, 255, 0, 0)); // Vermelho semi-transparente
        RectF criticalRegion = getCriticalRegionRect();
        canvas.drawRect(criticalRegion, criticalRegionPaint);

        synchronized (vehicles) {
            // Desenhar cada veículo
            for (Vehicle vehicle : vehicles) {
                vehicle.draw(canvas);
            }
        }
    }

    public void startRace() {
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                if (!vehicle.isAlive()) {
                    vehicle.start();
                }
            }
        }

        uiHandler.post(uiRunnable);
    }

    public void pauseRace() {
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                vehicle.stopVehicle();
            }
        }

        uiHandler.removeCallbacks(uiRunnable);
    }

    public void finishRace() {
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                vehicle.stopVehicle();
            }
            vehicles.clear();
        }

        uiHandler.removeCallbacks(uiRunnable);

        invalidate();
    }

    public void addCars(int quantity) {
        vehicles.clear(); // Limpar veículos existentes
        for (int i = 0; i < quantity; i++) {
            // Carregar imagem do carro
            Bitmap carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car1);

            // Definir uma cor única para cada carro
            Paint paint = new Paint();
            int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};
            paint.setColor(colors[i % colors.length]);

            // Posicionamento inicial
            int startX = 100;
            int startY = 100 + (50 * i);

            // Velocidade
            double speed = 3 + (i * 0.5);

            // Criar o carro e adicionar à lista
            Car car = new Car(this, carBitmap, scaledTrackBitmap, startX, startY, speed, paint);
            vehicles.add(car);
        }

        // Adicionar o Safety Car
        Bitmap safetyCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.safety_car);
        Paint safetyCarPaint = new Paint();
        safetyCarPaint.setColor(Color.WHITE);
        SafetyCar safetyCar = new SafetyCar(this, safetyCarBitmap, scaledTrackBitmap, 150, 150, 5.0, safetyCarPaint);
        vehicles.add(safetyCar);

        invalidate(); // Redesenhar para mostrar os veículos adicionados
    }

    public RectF getCriticalRegionRect() {
        return new RectF(criticalRegionX, criticalRegionY,
                criticalRegionX + criticalRegionWidth,
                criticalRegionY + criticalRegionHeight);
    }
}
