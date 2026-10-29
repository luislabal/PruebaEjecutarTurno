package parchis.modelo;

public class Ficha {
    public static final int CASA = 0, FIN_CIRCUITO = 64, META = 72;
    private final Color color;
    private final int id;
    private int pasos;

    public Ficha(Color color, int id) { this.color = color; this.id = id; }

    public Color getColor() { return color; }
    public int getId() { return id; }
    public int getPasos() { return pasos; }
    void setPasos(int p) { this.pasos = p; }  

    public boolean enCasa()     { return pasos == CASA; }
    public boolean enCircuito() { return pasos >= 1 && pasos <= FIN_CIRCUITO; }
    public boolean enPasillo()  { return pasos > FIN_CIRCUITO && pasos < META; }
    public boolean enMeta()     { return pasos == META; }
}
