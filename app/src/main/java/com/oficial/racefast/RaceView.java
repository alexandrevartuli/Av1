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

import com.oficial.rtlib.RTTaskManager;
import com.oficial.rtlib.TaskInfo;
import com.oficial.rtlib.TaskChain;
import com.oficial.rtlib.Subtask;
import com.oficial.rtlib.RealTimeScheduler;
import com.oficial.rtlib.AmdahlCalculator;

import java.util.ArrayList;
import java.util.List;

public class RaceView extends View {
    private Bitmap trackBitmap;
    private Bitmap scaledTrackBitmap;
    private List<Vehicle> vehicles;

    private float criticalRegionX;
    private float criticalRegionY;
    private float criticalRegionWidth;
    private float criticalRegionHeight;

    private Handler uiHandler;
    private Runnable uiRunnable;

    private RTTaskManager rtManager; // Gerenciador das tarefas
    public static RTTaskManager staticRTManagerReference;

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
                invalidate();
                uiHandler.postDelayed(this, 16);
            }
        };

        rtManager = new RTTaskManager();
        staticRTManagerReference = rtManager; // guarda referência estática
    }

    public static RTTaskManager getRTManager() {
        return staticRTManagerReference;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        scaledTrackBitmap = Bitmap.createScaledBitmap(trackBitmap, w, h, true);
        criticalRegionWidth = 200f;
        criticalRegionHeight = 100f;
        criticalRegionX = (w - criticalRegionWidth) / 2.0f;
        criticalRegionY = 0.7f*(h - criticalRegionHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scaledTrackBitmap, 0, 0, null);

        Paint criticalRegionPaint = new Paint();
        criticalRegionPaint.setColor(Color.argb(100, 255, 0, 0));
        RectF criticalRegion = getCriticalRegionRect();
        canvas.drawRect(criticalRegion, criticalRegionPaint);

        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                vehicle.draw(canvas);
            }
        }
    }

    public void startRace() {
        // Verifica escalonabilidade antes de iniciar


        
        
        boolean schedulable = rtManager.areAllSchedulable();
        if (!schedulable) {
            rtManager.adjustPrioritiesIfNeeded();
            schedulable = rtManager.areAllSchedulable();
        }

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
        //saveCarStates(); // opcional
    }

    public void finishRace() {
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                vehicle.stopVehicle();
            }
            vehicles.clear();
        }

        uiHandler.removeCallbacks(uiRunnable);
        //saveCarStates(); // opcional
        //clearCarStatesFromFirestore(); // opcional

        invalidate();
    }

    public void addCars(int quantity) {
        if (quantity < 19) {
            quantity = 19;
        }
        addCarsNormally(quantity);
    }

    private void addCarsNormally(int quantity) {
        vehicles.clear();
        rtManager = new RTTaskManager();
        staticRTManagerReference = rtManager; // ATUALIZA a referência estática aqui também!

        int baseY = 50;
        int baseX = 50;
        int spacing = 50;

        for (int i = 0; i < quantity; i++) {
            Bitmap carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car1);

            Paint paint = new Paint();
            int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};
            paint.setColor(colors[i % colors.length]);

            int startX = baseX + (i * spacing);
            int startY = baseY; // Y fixo

            double speed = 3 + (i * 0.5);

            Car car = new Car(this, carBitmap, scaledTrackBitmap, startX, startY, speed, paint);
            vehicles.add(car);

            int vehicleId = car.getVehicleId();
            TaskInfo tInfo = new TaskInfo("CarTask" + vehicleId, vehicleId, 0, 15, 100, 80, 2);

            TaskChain chain = new TaskChain();
            chain.addSubtask(new Subtask("SensorRead", 5));
            chain.addSubtask(new Subtask("DBStore", 10));
            chain.addSubtask(new Subtask("Move", 5));

            rtManager.addTask(tInfo, chain);
        }

        addSafetyCarIfNecessary();

        invalidate();
    }

    private void addSafetyCarIfNecessary() {
        boolean safetyCarExists = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle instanceof SafetyCar) {
                safetyCarExists = true;
                break;
            }
        }
        if (!safetyCarExists) {
            addSafetyCar();
        }
    }

    private void addSafetyCar() {
        Bitmap safetyCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.safety_car);
        Paint safetyCarPaint = new Paint();
        safetyCarPaint.setColor(Color.WHITE);
        SafetyCar safetyCar = new SafetyCar(this, safetyCarBitmap, scaledTrackBitmap, 100, 50, 5.0, safetyCarPaint);
        vehicles.add(safetyCar);

        int vehicleId = safetyCar.getVehicleId();
        TaskInfo tInfo = new TaskInfo("SafetyCarTask", vehicleId, 0, 15, 100, 50, 1);
        TaskChain chain = new TaskChain();
        chain.addSubtask(new Subtask("SensorRead", 5));
        chain.addSubtask(new Subtask("Move", 5));

        rtManager.addTask(tInfo, chain);
    }

    public RectF getCriticalRegionRect() {
        return new RectF(criticalRegionX, criticalRegionY,
                criticalRegionX + criticalRegionWidth,
                criticalRegionY + criticalRegionHeight);
    }
}
