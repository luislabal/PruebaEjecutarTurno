package parchis.controlador;

import parchis.modelo.*;
import java.util.List;


public class ControladorJugador {
    private final Color miColor;
    private final Partida partida = new Partida();
    private final RepositorioPartida repo;
    private final ControladorVisor puente;

    public ControladorJugador(Color miColor, RepositorioPartida repo) {
        this.miColor = miColor;
        this.repo = repo;
        this.puente = new ControladorVisor(repo);
    }

    public void suscribir(Partida.Observador o) { partida.agregarObservador(o); }

    
    public void nuevaPartida() throws Exception { guardar(); }

    public boolean sincronizar() {
        Estado e = puente.leerSiCambio();
        if (e == null) return false;
        partida.importar(e);
        return true;
    }

    public boolean esMiTurno()  { return partida.getGanador() == null && partida.getTurno() == miColor; }
    public boolean terminada()  { return partida.getGanador() != null; }

    public void ponerDado(int valor) throws Exception {
        partida.ponerDado(miColor, valor);     
        guardar();
    }

    public List<Ficha> fichasMovibles() { return partida.movibles(); }

    public void moverFicha(int id) throws Exception {
        exigirMiTurno();
        Ficha f = partida.movibles().stream()
                .filter(x -> x.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ficha no movible: " + id));
        partida.mover(f);
        guardar();
    }

    public void pasarTurno() throws Exception {
        exigirMiTurno();
        partida.pasarTurno();
        guardar();
    }

    private void exigirMiTurno() {
        if (!esMiTurno()) throw new IllegalStateException("No es tu turno, juega " + partida.getTurno());
    }

    private void guardar() throws Exception {
        Estado e = partida.exportar();
        repo.guardar(e);
        puente.marcarVisto(e.version());
    }

    public Color getMiColor() { return miColor; }
    public Partida getPartida() { return partida; }   
}
