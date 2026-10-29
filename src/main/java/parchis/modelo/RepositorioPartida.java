package parchis.modelo;

public interface RepositorioPartida {
    void guardar(Estado e) throws Exception;
    Estado cargar() throws Exception;
    boolean existe();
}
