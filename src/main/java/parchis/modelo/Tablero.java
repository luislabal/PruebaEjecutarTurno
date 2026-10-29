package parchis.modelo;

import java.util.Set;

public class Tablero {
    public static final int CASILLAS = 68;
    private static final Set<Integer> SEGUROS =
        Set.of(5, 12, 17, 22, 29, 34, 39, 46, 51, 56, 63, 68);
    
    public int casillaAbsoluta(Ficha f) {
        if (!f.enCircuito()) return -1;
        return ((f.getColor().salida - 1) + (f.getPasos() - 1)) % CASILLAS + 1;
    }

    public boolean esSeguro(int casilla) { return SEGUROS.contains(casilla); }
}
