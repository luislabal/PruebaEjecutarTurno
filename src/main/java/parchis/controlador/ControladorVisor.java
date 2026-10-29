package parchis.controlador;

import parchis.modelo.*;


public class ControladorVisor {
    private final RepositorioPartida repo;
    private int ultimaVersion = -1;

    public ControladorVisor(RepositorioPartida repo) { this.repo = repo; }

    
    public Estado leerSiCambio() {
        if (!repo.existe()) return null;
        try {
            Estado e = repo.cargar();
            if (e.version() == ultimaVersion) return null;
            ultimaVersion = e.version();
            return e;
        } catch (Exception ocupado) {
            return null;                    
        }
    }

    /** Para no releer lo que uno mismo acaba de guardar. */
    public void marcarVisto(int version) { ultimaVersion = version; }
}
