package com.example.remindly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.VH> {

    public interface OnEventoListener {
        void onEditar(Evento evento);
        void onEliminar(Evento evento);
    }

    private final Context context;
    private final ArrayList<Evento> eventos;
    private final boolean modoCalendario;
    private OnEventoListener listener;

    public EventoAdapter(Context context, ArrayList<Evento> eventos, boolean modoCalendario) {
        this.context = context;
        this.eventos = eventos;
        this.modoCalendario = modoCalendario;
    }

    public void setOnEventoListener(OnEventoListener listener) {
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        View tarjeta;
        TextView tvNombre, tvFecha, tvRestante, tvDescripcion;
        ImageButton btnEliminar;
        ImageView ivImagen;

        VH(View v) {
            super(v);
            tarjeta = v.findViewById(R.id.tarjeta);
            tvNombre = v.findViewById(R.id.tvNombre);
            tvDescripcion = v.findViewById(R.id.tvDescripcion);
            tvFecha = v.findViewById(R.id.tvFecha);
            tvRestante = v.findViewById(R.id.tvRestante);
            btnEliminar = v.findViewById(R.id.btnEliminar);
            ivImagen = v.findViewById(R.id.ivImagen);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new VH(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Evento e = eventos.get(position);
        h.tvNombre.setText(e.nombre);
        h.tvDescripcion.setText(e.descripcion);
        h.tvFecha.setText(e.fechaTexto() + (e.anual ? "  (anual)" : ""));
        h.tvRestante.setText(e.tiempoRestante());

        if (e.imagen != null) {
            Bitmap bmp = EventoStorage.cargarImagen(context, e.imagen);
            if (bmp != null) {
                h.ivImagen.setImageBitmap(bmp);
            } else {
                h.ivImagen.setImageResource(R.drawable.baseline_notifications_24);
            }
        } else {
            h.ivImagen.setImageResource(R.drawable.baseline_notifications_24);
        }

        if (modoCalendario) {
            int color = ContextCompat.getColor(context,
                    e.anual ? R.color.anual : R.color.unico);
            h.tarjeta.setBackgroundColor(color);
            h.tvNombre.setTextColor(ContextCompat.getColor(context, R.color.white));
            h.tvDescripcion.setTextColor(ContextCompat.getColor(context, R.color.white));
            h.tvFecha.setTextColor(ContextCompat.getColor(context, R.color.white));
            h.tvRestante.setTextColor(ContextCompat.getColor(context, R.color.white));
            h.btnEliminar.setVisibility(View.GONE);
            h.tarjeta.setOnClickListener(null);
        } else {
            h.btnEliminar.setOnClickListener(v -> {
                if (listener != null) listener.onEliminar(e);
            });
            h.tarjeta.setOnClickListener(v -> {
                if (listener != null) listener.onEditar(e);
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }
}
