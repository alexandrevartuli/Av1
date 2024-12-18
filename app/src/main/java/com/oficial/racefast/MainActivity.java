package com.oficial.racefast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Importar o RTTaskManager e outras classes do rtlib se quiser fazer testes aqui
import com.oficial.rtlib.AmdahlCalculator;

public class MainActivity extends AppCompatActivity {
    private ImageView ivTrack;
    private EditText contagemCarros;
    private Button buttonStart, buttonPause, buttonFinish;
    private RaceView raceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Certifique-se de que o layout está correto

        contagemCarros = findViewById(R.id.ContagemCarros);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);
        buttonFinish = findViewById(R.id.buttonFinish);
        raceView = findViewById(R.id.raceView);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtdText = contagemCarros.getText().toString();
                if (!qtdText.isEmpty()) {
                    int qtdCarros = Integer.parseInt(qtdText);
                    raceView.addCars(qtdCarros);
                    raceView.startRace();
                } else {
                    Toast.makeText(MainActivity.this, "Insira a quantidade de veículos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raceView.pauseRace();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raceView.finishRace();
            }
        });

        // Exemplo de uso da Lei de Amdahl, não é obrigatório:
        testAmdahl();
    }

    private void testAmdahl() {
        double f = 0.9;
        for (int N = 1; N <= 8; N++) {
            double sp = AmdahlCalculator.speedup(f, N);
            System.out.println("N="+N+" Speedup="+sp);
        }
    }
}
