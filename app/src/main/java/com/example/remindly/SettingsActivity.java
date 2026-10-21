package com.example.remindly;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Ajustes");

        EditText etMensaje = findViewById(R.id.etMensaje);
        Button btnGuardarMensaje = findViewById(R.id.btnGuardarMensaje);

        SharedPreferences prefs = getSharedPreferences(
                AlarmReceiver.PREFS_AJUSTES, MODE_PRIVATE);
        etMensaje.setText(prefs.getString(AlarmReceiver.KEY_MENSAJE, ""));

        btnGuardarMensaje.setOnClickListener(v -> {
            prefs.edit()
                    .putString(AlarmReceiver.KEY_MENSAJE,
                            etMensaje.getText().toString().trim())
                    .apply();
            Toast.makeText(this, "Mensaje guardado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
