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
        try {
            Files.createDirectories(Paths.get(SAVE_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creando carpeta de guardado: " + e.getMessage());
        }
    }

    public static class SaveData {
        public String nombreJugador;
        public int nivelActual;
        public boolean[] nivelesCompletados;
        public String fechaGuardado;
        public int tiempoTotal;

        // ⭐ NUEVO: Campos para gemas
        public int gemasAzulesRecolectadas;
        public int gemasRojasRecolectadas;
        public boolean[] gemasAzulesPorNivel;
        public boolean[] gemasRojasPorNivel;

        public SaveData(String nombre, int nivel, boolean[] completados) {
            this.nombreJugador = nombre;
            this.nivelActual = nivel;
            this.nivelesCompletados = completados;
            this.fechaGuardado = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            this.tiempoTotal = 0;

            // ⭐ Inicializar arrays de gemas
            this.gemasAzulesRecolectadas = 0;
            this.gemasRojasRecolectadas = 0;
            this.gemasAzulesPorNivel = new boolean[16];
            this.gemasRojasPorNivel = new boolean[16];
        }
    }

    // ⭐ MODIFICADO: Ahora incluye datos de gemas
    public void guardarPartida(String nombreJugador, int nivelActual, boolean[] nivelesCompletados,
                               int gemasAzules, int gemasRojas,
                               boolean[] gemasAzulesPorNivel, boolean[] gemasRojasPorNivel) {
        try {
            List<SaveData> partidas = cargarTodasLasPartidas();

            boolean encontrada = false;
            for (int i = 0; i < partidas.size(); i++) {
                if (partidas.get(i).nombreJugador.equalsIgnoreCase(nombreJugador)) {
                    SaveData save = partidas.get(i);
                    save.nivelActual = nivelActual;
                    save.nivelesCompletados = nivelesCompletados;
                    save.fechaGuardado = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                    // ⭐ Actualizar gemas
                    save.gemasAzulesRecolectadas = gemasAzules;
                    save.gemasRojasRecolectadas = gemasRojas;
                    save.gemasAzulesPorNivel = gemasAzulesPorNivel;
                    save.gemasRojasPorNivel = gemasRojasPorNivel;

                    encontrada = true;
                    break;
                }
            }

            if (!encontrada) {
                SaveData newSave = new SaveData(nombreJugador, nivelActual, nivelesCompletados);
                newSave.gemasAzulesRecolectadas = gemasAzules;
                newSave.gemasRojasRecolectadas = gemasRojas;
                newSave.gemasAzulesPorNivel = gemasAzulesPorNivel;
                newSave.gemasRojasPorNivel = gemasRojasPorNivel;
                partidas.add(newSave);
            }

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

                // ⭐ Guardar datos de gemas
                jsonSave.put("gemasAzulesRecolectadas", save.gemasAzulesRecolectadas);
                jsonSave.put("gemasRojasRecolectadas", save.gemasRojasRecolectadas);

                JSONArray gemasAzulesArray = new JSONArray();
                for (boolean recolectada : save.gemasAzulesPorNivel) {
                    gemasAzulesArray.put(recolectada);
                }
                jsonSave.put("gemasAzulesPorNivel", gemasAzulesArray);

                JSONArray gemasRojasArray = new JSONArray();
                for (boolean recolectada : save.gemasRojasPorNivel) {
                    gemasRojasArray.put(recolectada);
                }
                jsonSave.put("gemasRojasPorNivel", gemasRojasArray);

                jsonArray.put(jsonSave);
            }

            String filePath = SAVE_FOLDER + File.separator + SAVE_FILE;
            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonArray.toString(4));
                System.out.println("💾 Partida guardada: " + nombreJugador +
                        " (Gemas Azules: " + gemasAzules + "/16, Gemas Rojas: " + gemasRojas + "/16)");
            }

        } catch (IOException e) {
            System.err.println("Error guardando partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<SaveData> cargarTodasLasPartidas() {
        List<SaveData> partidas = new ArrayList<>();
        String filePath = SAVE_FOLDER + File.separator + SAVE_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            return partidas;
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

                // ⭐ Cargar datos de gemas (con valores por defecto para compatibilidad con partidas antiguas)
                save.gemasAzulesRecolectadas = jsonSave.optInt("gemasAzulesRecolectadas", 0);
                save.gemasRojasRecolectadas = jsonSave.optInt("gemasRojasRecolectadas", 0);

                if (jsonSave.has("gemasAzulesPorNivel")) {
                    JSONArray gemasAzulesArray = jsonSave.getJSONArray("gemasAzulesPorNivel");
                    save.gemasAzulesPorNivel = new boolean[gemasAzulesArray.length()];
                    for (int j = 0; j < gemasAzulesArray.length(); j++) {
                        save.gemasAzulesPorNivel[j] = gemasAzulesArray.getBoolean(j);
                    }
                } else {
                    save.gemasAzulesPorNivel = new boolean[16];
                }

                if (jsonSave.has("gemasRojasPorNivel")) {
                    JSONArray gemasRojasArray = jsonSave.getJSONArray("gemasRojasPorNivel");
                    save.gemasRojasPorNivel = new boolean[gemasRojasArray.length()];
                    for (int j = 0; j < gemasRojasArray.length(); j++) {
                        save.gemasRojasPorNivel[j] = gemasRojasArray.getBoolean(j);
                    }
                } else {
                    save.gemasRojasPorNivel = new boolean[16];
                }

                partidas.add(save);
            }

        } catch (Exception e) {
            System.err.println("Error cargando partidas: " + e.getMessage());
        }

        return partidas;
    }

    public SaveData cargarPartida(String nombreJugador) {
        List<SaveData> partidas = cargarTodasLasPartidas();

        for (SaveData save : partidas) {
            if (save.nombreJugador.equalsIgnoreCase(nombreJugador)) {
                return save;
            }
        }

        return null;
    }

    public void eliminarPartida(String nombreJugador) {
        try {
            List<SaveData> partidas = cargarTodasLasPartidas();
            partidas.removeIf(save -> save.nombreJugador.equalsIgnoreCase(nombreJugador));

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

                // ⭐ Incluir gemas al eliminar/reescribir
                jsonSave.put("gemasAzulesRecolectadas", save.gemasAzulesRecolectadas);
                jsonSave.put("gemasRojasRecolectadas", save.gemasRojasRecolectadas);

                JSONArray gemasAzulesArray = new JSONArray();
                for (boolean recolectada : save.gemasAzulesPorNivel) {
                    gemasAzulesArray.put(recolectada);
                }
                jsonSave.put("gemasAzulesPorNivel", gemasAzulesArray);

                JSONArray gemasRojasArray = new JSONArray();
                for (boolean recolectada : save.gemasRojasPorNivel) {
                    gemasRojasArray.put(recolectada);
                }
                jsonSave.put("gemasRojasPorNivel", gemasRojasArray);

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

    public boolean hayPartidasGuardadas() {
        return !cargarTodasLasPartidas().isEmpty();
    }
}