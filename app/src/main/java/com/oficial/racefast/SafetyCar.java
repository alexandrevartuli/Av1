package com.oficial.racefast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class SafetyCar extends Car {

    public SafetyCar(RaceView raceView, Bitmap bitmap, Bitmap trackBitmap, int startX, int startY, double speed, Paint paint) {
        // Chama o construtor da classe Car
        super(raceView, bitmap, trackBitmap, startX, startY, speed, paint);
        // Define a prioridade máxima para a thread do Safety Car
        this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void move() {
        try {
            // Se desejar alterar a velocidade do Safety Car
            double originalSpeed = speed;
            speed = 6.0; // Exemplo: velocidade reduzida

            // Chama o método move() da classe Car, que pode lançar InterruptedException
            super.move();

            // Restaura a velocidade original
            speed = originalSpeed;
        } catch (Exception e) {
            // Tratamento de outras exceções
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        // Desenha o Safety Car na tela
        super.draw(canvas);
    }

    // Se necessário, você pode sobrescrever outros métodos ou adicionar novos métodos aqui
}
