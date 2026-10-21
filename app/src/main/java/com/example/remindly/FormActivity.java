package com.example.remindly;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class FormActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion;
    private Button btnFecha, btnHora, btnGuardar;
    private CheckBox chkHora;
    private RadioGroup rgPeriodicidad, rgPlazo;
    private ImageView ivImagen;
    private int dia = -1, mes = -1, anio = -1;
    private int hora = -1, minuto = 0;
    private Evento editando;
    private Uri imagenSeleccionada;

    private final ActivityResultLauncher<String> selectorImagen =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagenSeleccionada = uri;
                    ivImagen.setImageURI(uri);
                    ivImagen.setVisibility(ImageView.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnFecha = findViewById(R.id.btnFecha);
        btnHora = findViewById(R.id.btnHora);
        btnGuardar = findViewById(R.id.btnGuardar);
        chkHora = findViewById(R.id.chkHora);
        rgPeriodicidad = findViewById(R.id.rgPeriodicidad);
        rgPlazo = findViewById(R.id.rgPlazo);
        ivImagen = findViewById(R.id.ivImagen);
        Button btnImagen = findViewById(R.id.btnImagen);

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            for (Evento e : EventoStorage.cargar(this)) {
                if (e.id == id) {
                    editando = e;
                    break;
                }
            }
        }

        if (editando != null) {
            setTitle("Editar recordatorio");
            etNombre.setText(editando.nombre);
            etDescripcion.setText(editando.descripcion);
            dia = editando.dia;
            mes = editando.mes;
            anio = editando.anio;
            btnFecha.setText(editando.fechaTexto().split(" ")[0]);
            if (editando.hora >= 0) {
                chkHora.setChecked(true);
                hora = editando.hora;
                minuto = editando.minuto;
                btnHora.setEnabled(true);
                btnHora.setText(String.format("%02d:%02d", hora, minuto));
            }
            if (editando.imagen != null) {
                Bitmap bmp = EventoStorage.cargarImagen(this, editando.imagen);
                if (bmp != null) {
                    ivImagen.setImageBitmap(bmp);
                    ivImagen.setVisibility(ImageView.VISIBLE);
                }
            }
            rgPeriodicidad.check(editando.anual ? R.id.rbAnual : R.id.rbUnico);
            rgPlazo.check(plazoARadio(editando.plazoDias));
        } else {
            setTitle("Nuevo recordatorio");
        }

        btnImagen.setOnClickListener(v -> selectorImagen.launch("image/*"));

        btnFecha.setOnClickListener(v -> {
            Calendar hoy = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                anio = y;
                mes = m + 1;
                dia = d;
                btnFecha.setText(String.format("%02d/%02d/%04d", dia, mes, anio));
            }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH),
                    hoy.get(Calendar.DAY_OF_MONTH)).show();
        });

        chkHora.setOnCheckedChangeListener((buttonView, isChecked) ->
                btnHora.setEnabled(isChecked));

        btnHora.setOnClickListener(v ->
                new TimePickerDialog(this, (view, h, m) -> {
                    hora = h;
                    minuto = m;
                    btnHora.setText(String.format("%02d:%02d", hora, minuto));
                }, 9, 0, true).show());

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private int plazoARadio(int plazo) {
        switch (plazo) {
            case 7: return R.id.rbPlazo7;
            case 3: return R.id.rbPlazo3;
            case 0: return R.id.rbPlazo0;
            default: return R.id.rbPlazo1;
        }
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el título del recordatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dia <= 0) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }
        String descripcion = etDescripcion.getText().toString().trim();

        Evento ev;
        if (editando != null) {
            ev = editando;
        } else {
            ev = new Evento();
            ev.id = EventoStorage.siguienteId(this);
        }
        ev.nombre = nombre;
        ev.descripcion = descripcion;
        ev.dia = dia;
        ev.mes = mes;
        ev.anio = anio;
        ev.hora = chkHora.isChecked() ? hora : -1;
        ev.minuto = minuto;
        ev.anual = rgPeriodicidad.getCheckedRadioButtonId() == R.id.rbAnual;
        ev.plazoDias = plazoSeleccionado();

        // Guardar la imagen seleccionada en Internal Storage
        if (imagenSeleccionada != null) {
            String imagenAnterior = ev.imagen;
            String nombreNuevo = EventoStorage.guardarImagen(this, imagenSeleccionada, ev.id);
            if (nombreNuevo != null) {
                ev.imagen = nombreNuevo;
                EventoStorage.eliminarImagen(this, imagenAnterior);
            }
        }

        ArrayList<Evento> lista = EventoStorage.cargar(this);
        if (editando != null) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).id == ev.id) {
                    lista.set(i, ev);
                    break;
                }
            }
        } else {
            lista.add(ev);
        }
        EventoStorage.guardar(this, lista);

        NotificationHelper.cancelar(this, ev.id);
        NotificationHelper.programar(this, ev);

        Toast.makeText(this, "Recordatorio guardado", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int plazoSeleccionado() {
        int id = rgPlazo.getCheckedRadioButtonId();
        if (id == R.id.rbPlazo7) return 7;
        if (id == R.id.rbPlazo3) return 3;
        if (id == R.id.rbPlazo0) return 0;
        return 1;
    }
}
