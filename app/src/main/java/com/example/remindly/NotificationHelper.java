package com.example.remindly;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

    public static final String CANAL_ANUAL = "canal_anual";
    public static final String CANAL_UNICO = "canal_unico";

    public static void crearCanales(Context context) {
        NotificationManager nm = context.getSystemService(NotificationManager.class);

        NotificationChannel canalAnual = new NotificationChannel(
                CANAL_ANUAL, "Eventos anuales", NotificationManager.IMPORTANCE_HIGH);
        canalAnual.setDescription("Notificaciones de eventos con periodicidad anual");

        NotificationChannel canalUnico = new NotificationChannel(
                CANAL_UNICO, "Eventos únicos", NotificationManager.IMPORTANCE_DEFAULT);
        canalUnico.setDescription("Notificaciones de eventos de una sola vez");

        nm.createNotificationChannel(canalAnual);
        nm.createNotificationChannel(canalUnico);
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void programar(Context context, Evento ev) {
        long ocurrencia = ev.proximaOcurrenciaMillis();
        long disparo = ocurrencia - ev.plazoDias * 24L * 60L * 60L * 1000L;

        if (disparo <= System.currentTimeMillis()) {
            disparo = System.currentTimeMillis() + 5000;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", ev.id);
        intent.putExtra("nombre", ev.nombre);
        intent.putExtra("descripcion", ev.descripcion);
        intent.putExtra("fecha", ev.fechaTexto());
        intent.putExtra("anual", ev.anual);

        PendingIntent pi = PendingIntent.getBroadcast(
                context, ev.id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, disparo, pi);
        }
    }

    public static void cancelar(Context context, int idEvento) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, idEvento, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pi);
        }
        pi.cancel();
    }
}
