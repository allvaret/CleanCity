package br.cleancity.model;

/**
 * Representa o jogador no mundo: posição, tamanho e velocidade de movimento.
 */
public class Player {
    /** Posição X (canto inferior esquerdo). */
    public float x;
    /** Posição Y (canto inferior esquerdo). */
    public float y;
    /** Largura do jogador. */
    public float width;
    /** Altura do jogador. */
    public float height;
    /** Velocidade em unidades por segundo. */
    public float speed;
    /** Última direção de movimento usada para render (unitária, ou 0 se parado). */
    public float faceX = 0f;
    public float faceY = -1f; // por padrão olhando para frente

    /**
     * Cria um jogador com posição, tamanho e velocidade informados.
     */
    public Player(float x, float y, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
}
