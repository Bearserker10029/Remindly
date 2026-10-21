package com.example.remindly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            NotificationHelper.crearCanales(context);
            ArrayList<Evento> eventos = EventoStorage.cargar(context);
            for (Evento ev : eventos) {
                NotificationHelper.cancelar(context, ev.id);
                NotificationHelper.programar(context, ev);
            }
        }
    }
}
