package parchis.modelo;

import java.util.Map;


public record Estado(Color turno, int ultimoDado, Color ganador, int version, Map<Color, int[]> posiciones) {}
