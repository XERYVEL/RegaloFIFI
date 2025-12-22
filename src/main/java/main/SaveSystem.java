package main;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SaveSystem {

    private static final String SAVE_FOLDER = "saves";
    private static final String SAVE_FILE = "partidas.json";

    public SaveSystem() {
        // Crear carpeta de guardado si no existe
        try {
            Files.createDirectories(Paths.get(SAVE_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creando carpeta de guardado: " + e.getMessage());
        }
    }

    // Clase interna para representar una partida
    public static class SaveData {
        public String nombreJugador;
        public int nivelActual;
        public boolean[] nivelesCompletados;
        public String fechaGuardado;
        public int tiempoTotal; // en segundos

        public SaveData(String nombre, int nivel, boolean[] completados) {
            this.nombreJugador = nombre;
            this.nivelActual = nivel;
            this.nivelesCompletados = completados;
            this.fechaGuardado = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.tiempoTotal = 0;
        }
    }

    // Guardar una nueva partida o actualizar una existente
    public void guardarPartida(String nombreJugador, int nivelActual, boolean[] nivelesCompletados) {
        try {
            List<SaveData> partidas = cargarTodasLasPartidas();

            // Buscar si ya existe una partida con ese nombre
            boolean encontrada = false;
            for (int i = 0; i < partidas.size(); i++) {
                if (partidas.get(i).nombreJugador.equalsIgnoreCase(nombreJugador)) {
                    // Actualizar partida existente
                    SaveData save = partidas.get(i);
                    save.nivelActual = nivelActual;
                    save.nivelesCompletados = nivelesCompletados;
                    save.fechaGuardado = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    encontrada = true;
                    break;
                }
            }

            // Si no existe, crear nueva partida
            if (!encontrada) {
                partidas.add(new SaveData(nombreJugador, nivelActual, nivelesCompletados));
            }

            // Guardar todo en el archivo JSON
            JSONArray jsonArray = new JSONArray();
            for (SaveData save : partidas) {
                JSONObject jsonSave = new JSONObject();
                jsonSave.put("nombreJugador", save.nombreJugador);
                jsonSave.put("nivelActual", save.nivelActual);
                jsonSave.put("fechaGuardado", save.fechaGuardado);
                jsonSave.put("tiempoTotal", save.tiempoTotal);

                JSONArray nivelesArray = new JSONArray();
                for (boolean completado : save.nivelesCompletados) {
                    nivelesArray.put(completado);
                }
                jsonSave.put("nivelesCompletados", nivelesArray);

                jsonArray.put(jsonSave);
            }

            // Escribir al archivo
            String filePath = SAVE_FOLDER + File.separator + SAVE_FILE;
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonArray.toString(4)); // 4 espacios de indentación
                System.out.println("Partida guardada exitosamente: " + nombreJugador);
            }

        } catch (IOException e) {
            System.err.println("Error guardando partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cargar todas las partidas guardadas
    public List<SaveData> cargarTodasLasPartidas() {
        List<SaveData> partidas = new ArrayList<>();
        String filePath = SAVE_FOLDER + File.separator + SAVE_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            return partidas; // Retornar lista vacía si no hay archivo
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonSave = jsonArray.getJSONObject(i);

                String nombre = jsonSave.getString("nombreJugador");
                int nivel = jsonSave.getInt("nivelActual");

                JSONArray nivelesArray = jsonSave.getJSONArray("nivelesCompletados");
                boolean[] completados = new boolean[nivelesArray.length()];
                for (int j = 0; j < nivelesArray.length(); j++) {
                    completados[j] = nivelesArray.getBoolean(j);
                }

                SaveData save = new SaveData(nombre, nivel, completados);
                save.fechaGuardado = jsonSave.getString("fechaGuardado");
                save.tiempoTotal = jsonSave.optInt("tiempoTotal", 0);

                partidas.add(save);
            }

        } catch (Exception e) {
            System.err.println("Error cargando partidas: " + e.getMessage());
        }

        return partidas;
    }

    // Cargar una partida específica por nombre
    public SaveData cargarPartida(String nombreJugador) {
        List<SaveData> partidas = cargarTodasLasPartidas();

        for (SaveData save : partidas) {
            if (save.nombreJugador.equalsIgnoreCase(nombreJugador)) {
                return save;
            }
        }

        return null;
    }

    // Eliminar una partida
    public void eliminarPartida(String nombreJugador) {
        try {
            List<SaveData> partidas = cargarTodasLasPartidas();
            partidas.removeIf(save -> save.nombreJugador.equalsIgnoreCase(nombreJugador));

            // Guardar el array actualizado
            JSONArray jsonArray = new JSONArray();
            for (SaveData save : partidas) {
                JSONObject jsonSave = new JSONObject();
                jsonSave.put("nombreJugador", save.nombreJugador);
                jsonSave.put("nivelActual", save.nivelActual);
                jsonSave.put("fechaGuardado", save.fechaGuardado);
                jsonSave.put("tiempoTotal", save.tiempoTotal);

                JSONArray nivelesArray = new JSONArray();
                for (boolean completado : save.nivelesCompletados) {
                    nivelesArray.put(completado);
                }
                jsonSave.put("nivelesCompletados", nivelesArray);

                jsonArray.put(jsonSave);
            }

            String filePath = SAVE_FOLDER + File.separator + SAVE_FILE;
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonArray.toString(4));
                System.out.println("Partida eliminada: " + nombreJugador);
            }

        } catch (IOException e) {
            System.err.println("Error eliminando partida: " + e.getMessage());
        }
    }

    // Verificar si existen partidas guardadas
    public boolean hayPartidasGuardadas() {
        return !cargarTodasLasPartidas().isEmpty();
    }
}