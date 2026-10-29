package parchis;

import parchis.controlador.ControladorJugador;
import parchis.modelo.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class MainJugador {

    static void jugar(Color mi) throws Exception {
        ControladorJugador ctrl = new ControladorJugador(mi, new RepositorioArchivo("partida.sav"));
        ctrl.suscribir(p -> dibujar(p, mi));            
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Eres el jugador " + mi);
        if (mi == Color.AMARILLO) {                      
            ctrl.nuevaPartida();
            dibujar(ctrl.getPartida(), mi);
        } else {
            System.out.println("Esperando a que AMARILLO inicie la partida...");
        }

        boolean avisado = false;
        while (true) {
            ctrl.sincronizar();                         
            if (ctrl.terminada()) { banner(ctrl.getPartida().getGanador(), mi); return; }

            if (!ctrl.esMiTurno()) {
                if (!avisado) {
                    System.out.println("\nEsperando el turno de " + ctrl.getPartida().getTurno() + " ...");
                    avisado = true;
                }
                Thread.sleep(500);
                continue;
            }
            avisado = false;

            while (in.ready()) in.readLine();           
            System.out.print("\nTU TURNO (" + mi + ") - valor del dado (1-6) o q para salir: ");
            String cmd = in.readLine();
            if (cmd == null || cmd.trim().equalsIgnoreCase("q")) return;
            try {
                ctrl.ponerDado(Integer.parseInt(cmd.trim()));
                jugarTurno(ctrl, in);
            } catch (NumberFormatException e) {
                System.out.println("Escribe un numero del 1 al 6.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void jugarTurno(ControladorJugador ctrl, BufferedReader in) throws Exception {
        List<Ficha> opciones = ctrl.fichasMovibles();
        if (opciones.isEmpty()) {
            System.out.println("Sin movimientos. Se pasa el turno.");
            ctrl.pasarTurno();
            return;
        }
        System.out.print("Elige ficha: ");
        opciones.forEach(f -> System.out.print("[" + f.getId() + "] "));
        System.out.println();
        while (true) {
            String s = in.readLine();
            if (s == null) return;
            try { ctrl.moverFicha(Integer.parseInt(s.trim())); return; }
            catch (IllegalArgumentException e) { System.out.println("Opcion no valida."); }
        }
    }

    private static void dibujar(Partida p, Color mi) {
        System.out.println("\n===== PARCHIS - Jugador " + mi + " =====");
        System.out.println("Turno: " + p.getTurno() + " | Ultimo dado: " + p.getUltimoDado());
        for (Color c : Color.values()) {
            StringBuilder sb = new StringBuilder(String.format("%-9s", c));
            for (Ficha f : p.getFichas().get(c)) sb.append(describir(f, p.getTablero())).append("  ");
            System.out.println(sb);
        }
    }

    private static void banner(Color ganador, Color mi) {
        System.out.println("\n****************************************");
        System.out.println("            JUEGO TERMINADO");
        System.out.println("            GANADOR: " + ganador);
        System.out.println(ganador == mi ? "            Ganaste!" : "            Perdiste esta vez.");
        System.out.println("****************************************");
    }

    private static String describir(Ficha f, Tablero t) {
        String pos = f.enCasa() ? "CASA" : f.enMeta() ? "META"
                   : f.enPasillo() ? "P" + (f.getPasos() - Ficha.FIN_CIRCUITO)
                   : "c" + t.casillaAbsoluta(f);
        return "F" + f.getId() + ":" + pos;
    }
}
