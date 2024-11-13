package com.oficial.racefast;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView ivTrack;
    private EditText contagemCarros;
    private Button buttonStart, buttonPause, buttonFinish;
    private RaceView raceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Certifique-se de que o layout está correto

        // Inicializar os componentes
        contagemCarros = findViewById(R.id.ContagemCarros);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);
        buttonFinish = findViewById(R.id.buttonFinish);
        raceView = findViewById(R.id.raceView);

        // Configurar os listeners dos botões
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
    }
}
