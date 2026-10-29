package parchis;

import parchis.controlador.ControladorVisor;
import parchis.modelo.*;
import java.time.LocalTime;
import java.util.Arrays;


public class MainVisorGuardado {
    public static void main(String[] args) throws Exception {
        ControladorVisor ctrl = new ControladorVisor(new RepositorioArchivo("partida.sav"));
        System.out.println("Esperando partida guardada...");
        while (true) {
            Estado e = ctrl.leerSiCambio();
            if (e != null) dibujar(e);
            Thread.sleep(500);
        }
    }

    private static void dibujar(Estado e) {
        System.out.println("\n===== POSICIONES GUARDADAS v" + e.version() + " (" + LocalTime.now().withNano(0) + ") =====");
        System.out.println("Turno guardado: " + e.turno() + " | Dado: " + e.ultimoDado());
        for (Color c : Color.values())
            System.out.printf("%-9s pasos: %s%n", c, Arrays.toString(e.posiciones().get(c)));
        if (e.ganador() != null) System.out.println("*** JUEGO TERMINADO - GANADOR: " + e.ganador() + " ***");
    }
}
