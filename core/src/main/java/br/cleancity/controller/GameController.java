package br.cleancity.controller;

import br.cleancity.model.GameWorld;
import br.cleancity.model.Player;
import br.cleancity.model.Truck;
import com.badlogic.gdx.math.Vector2;

/**
 * Controla a lógica do jogo por frame:
 * - Atualiza o relógio (delta time) enquanto não estiver em game over.
 * - Move o caminhão sempre para a direita; se sair da tela, ativa game over.
 * - Não atualiza o jogador quando o jogo acaba.
 * - Quando ativo, aplica entrada ao jogador, registra a última direção para render e faz clamping aos limites do mundo.
 */
public class GameController {
    private final GameWorld world;
    private final InputController input;

    private final Vector2 move = new Vector2();

    public GameController(GameWorld world, InputController input) {
        this.world = world;
        this.input = input;
    }

    /**
     * Atualiza o estado do jogo para o frame atual, na ordem:
     * 1) Atualiza timer (se não estiver em game over).
     * 2) Move o caminhão e checa se saiu da tela (encerra o jogo).
     * 3) Se o jogo acabou, retorna.
     * 4) Aplica entrada ao jogador, registra direção e faz clamping aos limites do mundo.
     * @param delta tempo em segundos desde o último frame
     */
    public void update(float delta) {
        // Timer e condição de derrota por tempo
        if (!world.gameOver) {
            world.timeLeft -= delta;
            if (world.timeLeft <= 0f) {
                world.timeLeft = 0f;
                world.gameOver = true;
            }
        }

        // Move o caminhão apenas para a direita; sem quicar. Continua mesmo após game over.
        Truck t = world.truck;
        t.x += Math.abs(t.speed) * delta;
        // Quando o caminhão sai da tela à direita, o jogo termina
        if (t.x > world.worldWidth) {
            world.gameOver = true;
        }

        // Não atualiza o jogador após game over
        if (world.gameOver) return;

        Player p = world.player;
        Vector2 dir = input.getDirection();
        move.set(dir).scl(p.speed * delta);
        p.x += move.x;
        p.y += move.y;
        // Guarda a última direção não-nula para renderização direcional
        if (dir.len2() > 0f) {
            p.faceX = dir.x;
            p.faceY = dir.y;
        }
        // Restringe o jogador aos limites do mundo
        if (p.x < 0) p.x = 0;
        if (p.y < 0) p.y = 0;
        if (p.x + p.width > world.worldWidth) p.x = world.worldWidth - p.width;
        if (p.y + p.height > world.worldHeight) p.y = world.worldHeight - p.height;
    }
}
