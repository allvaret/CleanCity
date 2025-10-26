package br.cleancity.model;

/**
 * Representa um item de lixo no mundo.
 */
public class Trash {
    /** Posição X do lixo (canto inferior esquerdo). */
    public float x;
    /** Posição Y do lixo (canto inferior esquerdo). */
    public float y;
    /** Largura do item de lixo. */
    public float width;
    /** Altura do item de lixo. */
    public float height;

    /**
     * Cria um item de lixo com posição e tamanho informados.
     */
    public Trash(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
