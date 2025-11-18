package br.cleancity.model;

/**
 * Define os parâmetros de uma fase (nível) do jogo.
 */
public class Level {
    /** Tempo total da fase, em segundos. */
    public final float totalTime;
    /** Quantidade inicial de lixo a spawnar. */
    public final int trashCount;
    /** Tamanho (lado) de cada lixo em unidades de mundo. */
    public final float trashSize;
    /** Velocidade do jogador, em unidades/segundo. */
    public final float playerSpeed;
    /** Largura do caminhão. */
    public final float truckWidth;
    /** Altura do caminhão. */
    public final float truckHeight;
    /** Chave do sprite de fundo para este nível. */
    public final String backgroundKey;

    /**
     * Cria um nível com os parâmetros informados.
     */
    public Level(float totalTime, int trashCount, float trashSize, float playerSpeed, float truckWidth, float truckHeight, String backgroundKey) {
        this.totalTime = totalTime;
        this.trashCount = trashCount;
        this.trashSize = trashSize;
        this.playerSpeed = playerSpeed;
        this.truckWidth = truckWidth;
        this.truckHeight = truckHeight;
        this.backgroundKey = backgroundKey;
    }
}
