package parchis.modelo;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RepositorioArchivo implements RepositorioPartida {
    private final Path archivo;
    public RepositorioArchivo(String nombre) { this.archivo = Paths.get(nombre); }

    @Override
    public void guardar(Estado e) throws Exception {
        List<String> lineas = new ArrayList<>();
        lineas.add("VERSION=" + e.version());
        lineas.add("TURNO=" + e.turno());
        lineas.add("DADO=" + e.ultimoDado());
        lineas.add("GANADOR=" + (e.ganador() == null ? "NINGUNO" : e.ganador().name()));
        for (Color c : Color.values()) {
            int[] a = e.posiciones().get(c);
            lineas.add(c.name() + "=" + a[0] + "," + a[1] + "," + a[2] + "," + a[3]);
        }
        Path tmp = Paths.get(archivo + ".tmp");     
        Files.write(tmp, lineas);
        for (int intento = 0; ; intento++) {       
            try {
                Files.move(tmp, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                return;
            } catch (IOException ex) {
                if (intento >= 9) throw ex;
                Thread.sleep(50);
            }
        }
    }

    @Override
    public Estado cargar() throws Exception {
        int version = 0, dado = 0;
        Color turno = Color.values()[0], ganador = null;
        Map<Color, int[]> pos = new EnumMap<>(Color.class);
        for (String l : Files.readAllLines(archivo)) {
            if (l.isBlank()) continue;
            String[] kv = l.split("=", 2);
            String v = kv[1].trim();
            switch (kv[0].trim()) {
                case "VERSION" -> version = Integer.parseInt(v);
                case "TURNO"   -> turno = Color.valueOf(v);
                case "DADO"    -> dado = Integer.parseInt(v);
                case "GANADOR" -> ganador = v.equals("NINGUNO") ? null : Color.valueOf(v);
                default -> {
                    Color c;
                    try { c = Color.valueOf(kv[0].trim()); }
                    catch (IllegalArgumentException ignorado) { continue; }   
                    String[] n = v.split(",");
                    int[] a = new int[4];
                    for (int i = 0; i < 4; i++) a[i] = Integer.parseInt(n[i].trim());
                    pos.put(c, a);
                }
            }
        }
        for (Color c : Color.values()) pos.putIfAbsent(c, new int[4]);
        return new Estado(turno, dado, ganador, version, pos);
    }

    @Override public boolean existe() { return Files.exists(archivo); }
}
