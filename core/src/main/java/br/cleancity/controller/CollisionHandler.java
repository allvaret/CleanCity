package br.cleancity.controller;

import br.cleancity.audio.Mfx;
import br.cleancity.audio.Sfx;
import br.cleancity.audio.SoundManager;
import br.cleancity.model.GameWorld;
import br.cleancity.model.Player;
import br.cleancity.model.Trash;
import br.cleancity.model.Truck;
/**
 * Responsável por detectar colisões (AABB) e aplicar as regras do jogo envolvendo
 * jogador, caminhão e lixo:
 * - Frente do caminhão é letal (encerra a fase como derrota).
 * - Laterais/traseira não matam: resolvemos a interpenetração empurrando o jogador para fora.
 * - Coleta: ao tocar um lixo, remove-o e incrementa `carriedTrash`.
 * - Entrega: ao encostar no caminhão (fora da frente), converte `carriedTrash` em pontos.
 * - Vitória: se não houver mais lixo no mundo e o jogador não carregar nenhum, marca vitória,
 *   pausa o jogo e para o caminhão (speed = 0).
 * - Não processa nada quando `gameOver` já está ativo.
 */
public class CollisionHandler {
    private final GameWorld world;
    private final SoundManager audio;

    public CollisionHandler(GameWorld world, SoundManager audio) {
        this.world = world;
        this.audio = audio;
    }

    /**
     * Atualiza as colisões do frame, na ordem:
     * 1) Early-out em caso de vitória já alcançada (pausa e para o caminhão).
     * 2) Morte ao tocar a frente do caminhão.
     * 3) Resolução de interpenetração não-frontal com o caminhão.
     * 4) Coleta de lixo (iterando de trás pra frente na lista indexada).
     * 5) Entrega no caminhão (laterais/traseira) e checagem de vitória após a entrega.
     * Ignora todo o processamento se `gameOver` estiver ativo.
     */
    public void update() {
        if (world.gameOver) return;

        Player p = world.player;
        Truck t = world.truck;

        // Usa uma hitbox menor e centralizada para o caminhão (reduz "retângulo invisível, que sobrava anteriormente")
        final float TRUCK_COLLISION_SCALE = 0.9f; // 90% do tamanho visual
        float sW = t.width * TRUCK_COLLISION_SCALE;
        float sH = t.height * TRUCK_COLLISION_SCALE;
        float CollisionX = t.x + (t.width - sW) * 0.5f; // centraliza
        float CollisionY = t.y + (t.height - sH) * 0.5f;

        // Vitória antecipada da rua
        if (world.trashList.size == 0 && world.carriedTrash == 0) {
            world.gameWon = true;
            world.gameOver = true;
            t.speed = 0f; // para o caminhão imediatamente
            return;
        }

        // Lado frontal do caminhão é letal (usa a hitbox reduzida)
        if (isHitByTruckFrontBounds(p, CollisionX, CollisionY, sW, sH)) {
            world.gameOver = true;
            world.gameWon = false;
            p.isDefeated = true;
            audio.fadeOut(Mfx.TRACK,0.6f);
            audio.waitAndRun(500, () -> {
                audio.playS(Sfx.DEATH);
                audio.playS(Sfx.LOSE);
            });
            return;
        }

        // Impede atravessar o caminhão pelas laterais ou traseira (resolve interpenetração)
        if (overlaps(p.x, p.y, p.width, p.height, CollisionX, CollisionY, sW, sH)) {
            // Se não é a frente, apenas resolve a colisão para fora do caminhão
            if (!isHitByTruckFrontBounds(p, CollisionX, CollisionY, sW, sH)) {
                resolveNonFrontCollisionBounds(p, CollisionX, CollisionY, sW, sH);
            }
        }

        // Coleta de lixo
        for (int i = world.trashList.size - 1; i >= 0; i--) {
            Trash trash = world.trashList.get(i);
            if (overlaps(p.x, p.y, p.width, p.height, trash.x, trash.y, trash.width, trash.height)) {
                world.trashList.removeIndex(i);
                world.carriedTrash += 1;
                audio.playS(Sfx.C_TRASH, 0.8f);
            }
        }

        // Entrega de lixo quando encostar nas laterais/traseira do caminhão (sem ser a frente)
        if (!isHitByTruckFrontBounds(p, CollisionX, CollisionY, sW, sH) && isTouchingTruckForDeliveryBounds(p, CollisionX, CollisionY, sW, sH)) {
            if (world.carriedTrash > 0) {
                world.score.value += world.carriedTrash;
                world.carriedTrash = 0;
                audio.playS(Sfx.DELIVERY);
            }
            // Após entregar, verifica condição de vitória
            if (world.trashList.size == 0 && world.carriedTrash == 0) {
                world.gameWon = true;
                world.gameOver = true;
                t.speed = 0f; // para o caminhão
                audio.fadeOut(Mfx.TRACK,0.6f);
                audio.waitAndRun(500,() -> {
                    audio.playS(Sfx.WIN, 1.5f);
                });
                return;
            }
        }
    }


    // Versão baseada em bounds explícitos (hitbox reduzida)
    private boolean isHitByTruckFrontBounds(Player player, float bx, float by, float bw, float bh) {
        float lethalStripWidth = 12f; // largura da faixa frontal letal
        float frontX = bx + bw - lethalStripWidth;
        float frontW = lethalStripWidth;
        return overlaps(player.x, player.y, player.width, player.height,
                        frontX, by, frontW, bh);
    }


    // Nova versão que usa bounds explícitos (hitbox reduzida)
    private void resolveNonFrontCollisionBounds(Player p, float bx, float by, float bw, float bh) {
        float overlapLeft   = (p.x + p.width) - bx;
        float overlapRight  = (bx + bw) - p.x;
        float overlapBottom = (p.y + p.height) - by;
        float overlapTop    = (by + bh) - p.y;
        float minX = Math.min(overlapLeft, overlapRight);
        float minY = Math.min(overlapBottom, overlapTop);
        if (minX < minY) {
            if (overlapLeft < overlapRight) p.x = bx - p.width; else p.x = bx + bw;
        } else {
            if (overlapBottom < overlapTop) p.y = by - p.height; else p.y = by + bh;
        }
    }


    // Versão baseada em bounds explícitos (hitbox reduzida)
    private boolean isTouchingTruckForDeliveryBounds(Player p, float bx, float by, float bw, float bh) {
        float margin = 2f;
        float ex = bx - margin;
        float ey = by - margin;
        float ew = bw + margin * 2f;
        float eh = bh + margin * 2f;
        boolean nearTruck = overlaps(p.x, p.y, p.width, p.height, ex, ey, ew, eh);
        if (!nearTruck) return false;
        if (isHitByTruckFrontBounds(p, bx, by, bw, bh)) return false;
        return true;
    }

    /** Verifica sobreposição AABB entre dois retângulos. */
    private boolean overlaps(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}
