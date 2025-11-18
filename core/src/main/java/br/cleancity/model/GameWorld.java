package br.cleancity.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import br.cleancity.model.Truck;
import br.cleancity.model.Level;

/**
 * Representa o estado do jogo (Modelo) contendo o mundo, entidades e regras básicas.
 * Mantém jogador, caminhão, lixo, pontuação e controle de tempo/estado de game over.
 *
 * Parâmetros de fase (ver `Level`): tempo total, quantidade/tamanho do lixo, velocidade do jogador,
 * largura/altura do caminhão. A velocidade do caminhão é calculada para que ele atravesse a tela
 * ao final do tempo total (incluindo a própria largura), o que define uma condição natural de fim.
 *
 * Geração de lixo (`spawnTrash`): posiciona itens aleatoriamente evitando a área inicial do jogador
 * (um retângulo em torno do spawn) para não gerar coleta imediata.
 */
@SuppressWarnings("unused")
public class GameWorld {
    /** Largura lógica do mundo (em unidades de mundo). */
    public final float worldWidth;
    /** Altura lógica do mundo (em unidades de mundo). */
    public final float worldHeight;

    /** Entidade do jogador. */
    public final Player player;
    /** Controle de pontuação. */
    public final Score score;
    /** Lista de objetos de lixo espalhados no mundo. */
    public final Array<Trash> trashList = new Array<>();
    /** Caminhão coletor que se move da esquerda para a direita. */
    public final Truck truck;
    /** Parâmetros da fase atual. */
    public final Level level;
    /** Duração total da fase (segundos). */
    public final float totalTime;
    /** Tempo restante (segundos). */
    public float timeLeft;
    /** Indica se o jogo terminou (tempo zerou ou caminhão saiu da tela). */
    public boolean gameOver = false;
    /** Indica se o jogador venceu a fase (todo lixo coletado e entregue). */
    public boolean gameWon = false;
    /** Quantidade de lixo carregado pelo jogador e ainda não entregue ao caminhão. */
    public int carriedTrash = 0;

    /**
     * Cria um mundo com tempo padrão de 60s.
     */
    public GameWorld(float worldWidth, float worldHeight) {
        this(worldWidth, worldHeight, 60f);
    }

    /**
     * Cria um mundo com dimensões e tempo total informados.
     * Velocidade do caminhão é definida para alcançar a borda direita quando o tempo zerar.
     */
    public GameWorld(float worldWidth, float worldHeight, float totalTime) {
        this(worldWidth, worldHeight, new Level(totalTime, 15, 20f, 240f, 64f, 32f, "Street"));
    }

    /**
     * Cria um mundo usando uma definição de fase (Level).
     */
    public GameWorld(float worldWidth, float worldHeight, Level level) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.level = level;
        this.totalTime = level.totalTime;
        this.timeLeft = this.totalTime;

        this.player = new Player(worldWidth / 2f - 16f, worldHeight / 2f - 16f, 26f, 26f, level.playerSpeed);
        this.score = new Score();
        this.truck = new Truck(0f, worldHeight / 2f, level.truckWidth, level.truckHeight, 0f);
        // Velocidade para o caminhão sair da tela quando o tempo acabar (inclui a largura do próprio caminhão)
        this.truck.speed = (worldWidth + truck.width) / this.totalTime;
        spawnTrash(level.trashCount, level.trashSize);
    }

    /**
     * Gera lixo aleatoriamente no mundo, evitando a área inicial do jogador.
     * @param count quantos itens gerar
     * @param size tamanho (lado do quadrado) de cada lixo
     */
    public void spawnTrash(int count, float size) {
        for (int i = 0; i < count; i++) {
            float x, y;
            int guard = 0;
            do {
                x = MathUtils.random(0f, worldWidth - size);
                y = MathUtils.random(0f, worldHeight - size);
                guard++;
                if (guard > 1000) break; // segurança para evitar loop infinito
            } while (overlaps(x, y, size, size, player.x, player.y, player.width * 3f, player.height * 3f));
            Trash trash = new Trash(x, y, size, size);
            // Atribui uma chave de sprite estável para não mudar após remoções na lista
            String[] keys = new String[] {"Trash_Pixel1","Trash_Pixel2","Trash_Pixel3","Trash_Pixel4","Trash_Pixel5","Trash_Pixel6"};
            if (trash.spriteKey == null && keys.length > 0) trash.spriteKey = keys[i % keys.length];
            trashList.add(trash);
        }
    }

    /**
     * Verifica sobreposição AABB entre dois retângulos.
     */
    private boolean overlaps(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}
