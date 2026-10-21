package com.example.remindly;

import org.json.JSONObject;

import java.util.Calendar;

public class Evento {

    public int id;
    public String nombre;
    public String descripcion;
    public String imagen;   // nombre del archivo de imagen en Internal Storage (null si no tiene)
    public int dia, mes, anio;
    public int hora = -1, minuto;
    public boolean anual;
    public int plazoDias = 1;

    public Evento() {
    }

    public String fechaTexto() {
        String f = String.format("%02d/%02d/%04d", dia, mes, anio);
        if (hora >= 0) {
            f += String.format(" %02d:%02d", hora, minuto);
        }
        return f;
    }

    public long proximaOcurrenciaMillis() {
        Calendar c = Calendar.getInstance();
        int h = hora >= 0 ? hora : 9;
        int m = hora >= 0 ? minuto : 0;
        c.set(anio, mes - 1, dia, h, m, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (anual) {
            Calendar ahora = Calendar.getInstance();
            if (c.getTimeInMillis() <= ahora.getTimeInMillis()) {
                int saltos = ahora.get(Calendar.YEAR) - c.get(Calendar.YEAR) + 1;
                c.add(Calendar.YEAR, saltos);
            }
        }
        return c.getTimeInMillis();
    }

    public String tiempoRestante() {
        long diff = proximaOcurrenciaMillis() - System.currentTimeMillis();
        if (diff <= 0) return "Es hoy";
        long dias = diff / 86400000L;
        long horas = (diff % 86400000L) / 3600000L;
        if (dias > 0) return "Faltan " + dias + " días y " + horas + " h";
        if (horas > 0) return "Faltan " + horas + " horas";
        return "Falta menos de 1 hora";
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("id", id);
            o.put("nombre", nombre);
            o.put("descripcion", descripcion);
            o.put("imagen", imagen);
            o.put("dia", dia);
            o.put("mes", mes);
            o.put("anio", anio);
            o.put("hora", hora);
            o.put("minuto", minuto);
            o.put("anual", anual);
            o.put("plazo", plazoDias);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static Evento fromJson(JSONObject o) {
        Evento e = new Evento();
        try {
            e.id = o.getInt("id");
            e.nombre = o.getString("nombre");
            e.descripcion = o.getString("descripcion");
            e.imagen = o.has("imagen") ? o.optString("imagen", null) : null;
            if (e.imagen != null && e.imagen.isEmpty()) e.imagen = null;
            e.dia = o.getInt("dia");
            e.mes = o.getInt("mes");
            e.anio = o.getInt("anio");
            e.hora = o.getInt("hora");
            e.minuto = o.getInt("minuto");
            e.anual = o.getBoolean("anual");
            e.plazoDias = o.getInt("plazo");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return e;
    }
}
