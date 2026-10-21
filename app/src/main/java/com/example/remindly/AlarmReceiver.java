package com.example.remindly;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String PREFS_AJUSTES = "ajustes";
    public static final String KEY_MENSAJE = "mensaje_recordatorio";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.crearCanales(context);

        int id = intent.getIntExtra("id", 0);
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        String fecha = intent.getStringExtra("fecha");
        boolean anual = intent.getBooleanExtra("anual", false);

        // Mensaje personalizado guardado en SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_AJUSTES, Context.MODE_PRIVATE);
        String mensaje = prefs.getString(KEY_MENSAJE, "Tienes un recordatorio pendiente");

        Intent abrir = new Intent(context, MainActivity.class);
        abrir.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contenido = PendingIntent.getActivity(
                context, id, abrir, PendingIntent.FLAG_IMMUTABLE);

        String canal = anual ? NotificationHelper.CANAL_ANUAL : NotificationHelper.CANAL_UNICO;

        String texto = mensaje + " — " + descripcion + " (Fecha: " + fecha + ")";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canal)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Recordatorio: " + nombre)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setPriority(anual ? NotificationCompat.PRIORITY_HIGH
                        : NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contenido)
                .setAutoCancel(true);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
