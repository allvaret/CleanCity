package br.cleancity.controller;

import br.cleancity.model.GameWorld;
import br.cleancity.model.Player;
import br.cleancity.model.Trash;

/**
 * Responsável por detectar colisões e aplicar as regras de coleta/entrega de lixo.
 */
public class CollisionHandler {
    private final GameWorld world;

    public CollisionHandler(GameWorld world) {
        this.world = world;
    }

    /**
     * Atualiza as colisões do frame:
     * - Coleta lixo (aumenta carriedTrash e remove do mundo).
     * - Entrega lixo quando o jogador encosta no caminhão (converte carriedTrash em score).
     * Ignora processamento após game over.
     */
    public void update() {
        if (world.gameOver) return;

        Player p = world.player;
        // Coleta de lixo: aumenta carriedTrash
        for (int i = world.trashList.size - 1; i >= 0; i--) {
            Trash t = world.trashList.get(i);
            if (overlaps(p.x, p.y, p.width, p.height, t.x, t.y, t.width, t.height)) {
                world.trashList.removeIndex(i);
                world.carriedTrash += 1;
            }
        }
        // Entrega de lixo ao caminhão
        if (world.carriedTrash > 0) {
            if (overlaps(p.x, p.y, p.width, p.height, world.truck.x, world.truck.y, world.truck.width, world.truck.height)) {
                world.score.value += world.carriedTrash;
                world.carriedTrash = 0;
            }
        }
        // Condição de vitória: todo lixo coletado e entregue
        if (world.trashList.size == 0 && world.carriedTrash == 0) {
            world.gameWon = true;
            world.gameOver = true;
        }
    }

    /** Verifica sobreposição AABB entre dois retângulos. */
    private boolean overlaps(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}
