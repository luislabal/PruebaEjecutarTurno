package parchis.modelo;

import java.util.*;

public class Partida {
    public interface Observador { void partidaCambio(Partida p); }

    private final Map<Color, List<Ficha>> fichas = new EnumMap<>(Color.class);
    private final Tablero tablero = new Tablero();
    private final List<Observador> observadores = new ArrayList<>();
    private Color turno = Color.values()[0];
    private int ultimoDado;
    private Color ganador;      
    private int version;       

    public Partida() {
        for (Color c : Color.values()) {
            List<Ficha> lista = new ArrayList<>();
            for (int i = 1; i <= 4; i++) lista.add(new Ficha(c, i));
            fichas.put(c, lista);
        }
    }

    public void agregarObservador(Observador o) { observadores.add(o); }
    private void notificar() { observadores.forEach(o -> o.partidaCambio(this)); }

    private void exigirEnCurso() {
        if (ganador != null) throw new IllegalStateException("El juego ya termino. Ganador: " + ganador);
    }

    public void ponerDado(Color jugador, int valor) {
        exigirEnCurso();
        if (jugador != turno) throw new IllegalStateException("No es tu turno, juega " + turno);
        if (valor < 1 || valor > 71) throw new IllegalArgumentException("El dado debe ser de 1 a 6");
        ultimoDado = valor;
        version++;
        notificar();
    }

    public List<Ficha> movibles() {
        List<Ficha> res = new ArrayList<>();
        if (ganador != null) return res;
        for (Ficha f : fichas.get(turno)) {
            if (f.enMeta()) continue;
            if (f.enCasa()) { if (ultimoDado == 5) res.add(f); }
            else if (f.getPasos() + ultimoDado <= Ficha.META) res.add(f);
        }
        return res;
    }

    public void mover(Ficha f) {
        exigirEnCurso();
        if (!movibles().contains(f)) throw new IllegalArgumentException("Movimiento no valido");
        f.setPasos(f.enCasa() ? 1 : f.getPasos() + ultimoDado);
        capturarSiAplica(f);
        if (todasEnMeta(f.getColor())) ganador = f.getColor();       
        else if (ultimoDado != 6) turno = siguiente(turno);
        version++;
        notificar();
    }

    public void pasarTurno() {
        exigirEnCurso();
        if (!movibles().isEmpty()) throw new IllegalStateException("Tienes movimientos disponibles");
        turno = siguiente(turno);
        version++;
        notificar();
    }

    private boolean todasEnMeta(Color c) {
        return fichas.get(c).stream().allMatch(Ficha::enMeta);
    }

    private void capturarSiAplica(Ficha f) {
        int casilla = tablero.casillaAbsoluta(f);
        if (casilla == -1 || tablero.esSeguro(casilla)) return;
        for (Color c : Color.values()) {
            if (c == f.getColor()) continue;
            for (Ficha otra : fichas.get(c))
                if (tablero.casillaAbsoluta(otra) == casilla) otra.setPasos(Ficha.CASA);
        }
    }

    private Color siguiente(Color c) { return Color.values()[(c.ordinal() + 1) % Color.values().length]; }

    
    public Color getTurno() { return turno; }
    public int getUltimoDado() { return ultimoDado; }
    public Color getGanador() { return ganador; }
    public int getVersion() { return version; }
    public Tablero getTablero() { return tablero; }
    public Map<Color, List<Ficha>> getFichas() { return Collections.unmodifiableMap(fichas); }

    
    public Estado exportar() {
        Map<Color, int[]> pos = new EnumMap<>(Color.class);
        fichas.forEach((c, l) -> {
            int[] a = new int[4];
            for (int i = 0; i < 4; i++) a[i] = l.get(i).getPasos();
            pos.put(c, a);
        });
        return new Estado(turno, ultimoDado, ganador, version, pos);
    }

    public void importar(Estado e) {
        this.turno = e.turno();
        this.ultimoDado = e.ultimoDado();
        this.ganador = e.ganador();
        this.version = e.version();
        for (Color c : Color.values()) {
            int[] a = e.posiciones().get(c);
            if (a == null) continue;
            for (int i = 0; i < 4; i++) fichas.get(c).get(i).setPasos(a[i]);
        }
        notificar();
    }
}
