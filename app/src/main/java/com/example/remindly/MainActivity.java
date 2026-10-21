package com.example.remindly;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvEventos;
    private TextView tvVacio;
    private EventoAdapter adapter;
    private ArrayList<Evento> eventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.crearCanales(this);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        rvEventos = findViewById(R.id.rvEventos);
        tvVacio = findViewById(R.id.tvVacio);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnAjustes = findViewById(R.id.btnAjustes);

        rvEventos.setLayoutManager(new LinearLayoutManager(this));

        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, FormActivity.class)));

        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventos = EventoStorage.cargar(this);
        adapter = new EventoAdapter(this, eventos, false);
        adapter.setOnEventoListener(new EventoAdapter.OnEventoListener() {
            @Override
            public void onEditar(Evento e) {
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                intent.putExtra("id", e.id);
                startActivity(intent);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEliminar(Evento e) {
                eventos.remove(e);
                EventoStorage.guardar(MainActivity.this, eventos);
                EventoStorage.eliminarImagen(MainActivity.this, e.imagen);
                NotificationHelper.cancelar(MainActivity.this, e.id);
                adapter.notifyDataSetChanged();
                actualizarVacio();
                Toast.makeText(MainActivity.this, "Recordatorio eliminado",
                        Toast.LENGTH_SHORT).show();
            }
        });
        rvEventos.setAdapter(adapter);
        actualizarVacio();
    }

    private void actualizarVacio() {
        if (eventos.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
            rvEventos.setVisibility(View.GONE);
        } else {
            tvVacio.setVisibility(View.GONE);
            rvEventos.setVisibility(View.VISIBLE);
        }
    }
}
