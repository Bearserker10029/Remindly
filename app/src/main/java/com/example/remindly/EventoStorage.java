package com.example.remindly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class EventoStorage {

    private static final String ARCHIVO_EVENTOS = "eventos.json";

    public static ArrayList<Evento> cargar(Context context) {
        ArrayList<Evento> lista = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(ARCHIVO_EVENTOS);
             BufferedReader br = new BufferedReader(new FileReader(fis.getFD()))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                lista.add(Evento.fromJson(arr.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void guardar(Context context, ArrayList<Evento> lista) {
        JSONArray arr = new JSONArray();
        for (Evento e : lista) {
            arr.put(e.toJson());
        }
        try (FileOutputStream fos = context.openFileOutput(ARCHIVO_EVENTOS, Context.MODE_PRIVATE)) {
            fos.write(arr.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int siguienteId(Context context) {
        int max = 0;
        for (Evento e : cargar(context)) {
            max = Math.max(max, e.id);
        }
        return max + 1;
    }

    public static String guardarImagen(Context context, Uri uri, int idEvento) {
        String nombre = "img_" + idEvento + "_" + System.currentTimeMillis() + ".jpg";
        try (InputStream in = context.getContentResolver().openInputStream(uri);
             FileOutputStream fos = context.openFileOutput(nombre, Context.MODE_PRIVATE)) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) > 0) {
                fos.write(buffer, 0, n);
            }
            return nombre;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap cargarImagen(Context context, String nombre) {
        try (FileInputStream fis = context.openFileInput(nombre)) {
            return BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            return null;
        }
    }

    public static void eliminarImagen(Context context, String nombre) {
        if (nombre != null) {
            context.deleteFile(nombre);
        }
    }
}
