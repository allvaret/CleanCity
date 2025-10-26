package br.cleancity.model;

/**
 * Representa o caminhão coletor que atravessa a tela da esquerda para a direita.
 */
public class Truck {
    /** Posição X (canto inferior esquerdo). */
    public float x;
    /** Posição Y (canto inferior esquerdo). */
    public float y;
    /** Largura do caminhão. */
    public float width;
    /** Altura do caminhão. */
    public float height;
    /** Velocidade horizontal em unidades por segundo. */
    public float speed;

    /**
     * Cria um caminhão com posição, tamanho e velocidade informados.
     */
    public Truck(float x, float y, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
}
