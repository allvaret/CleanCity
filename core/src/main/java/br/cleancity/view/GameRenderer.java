package br.cleancity.view;

import br.cleancity.model.GameWorld;
import br.cleancity.model.Player;
import br.cleancity.model.Trash;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/**
 * Responsável por desenhar os elementos do mundo (lixo, caminhão e jogador) usando uma câmera ortográfica.
 *
 * Conceitos principais do LibGDX:
 * - {@link OrthographicCamera}: define o espaço lógico 2D (viewport) e sua projeção.
 * - {@link SpriteBatch}: batch de desenho para sprites 2D; `begin()`/`end()` acontecem fora desta classe.
 * - {@link Texture}: sprites carregados pelo `SpriteManager` a partir de `assets/sprites/`.
 *
 * Detalhes importantes:
 * - Sincronização de hitboxes: `syncHitboxesToSpriteSizes()` deixa `width/height` das entidades proporcional ao sprite,
 *   em função de uma fração da altura do viewport, preservando o aspecto (width = height * (tw/th)). Assim, colisão e
 *   render ficam consistentes mesmo mudando resolução.
 * - Ordem de desenho condicional: quando o jogador está derrotado, desenhamos o jogador primeiro e depois o caminhão,
 *   para criar a sensação de atropelamento (caminhão por cima). Caso contrário, caminhão abaixo e jogador acima.
 * - Espelhamento horizontal: largura negativa em `batch.draw` reflete o sprite lateral quando olhando à esquerda.
 */
public class GameRenderer {
    private final OrthographicCamera worldCamera;
    private final Texture white;
    private final SpriteManager sprites;

    public GameRenderer(SpriteManager sprites, float worldWidth, float worldHeight) {
        this.sprites = sprites;
        this.white = sprites.white();
        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, worldWidth, worldHeight);
    }

    private static final float TRASH_HEIGHT_PCT = 0.06f;
    private static final float TRUCK_HEIGHT_PCT = 0.14f;
    private static final float PLAYER_HEIGHT_PCT = 0.10f;

    // Mantém hitboxes proporcionais ao sprite e ao viewport, garantindo que colisão e desenho "batam" visualmente.
    private void syncHitboxesToSpriteSizes(GameWorld world) {
        float vh = worldCamera.viewportHeight;

        // Truck size (usa Texture direta)
        {
            Texture t = sprites.get("Art Garbage Truck_Right");
            float tw = t.getWidth();
            float th = t.getHeight();
            float targetH = vh * TRUCK_HEIGHT_PCT;
            world.truck.width = targetH * (tw / th);
            world.truck.height = targetH;
        }

        // Player size (usa o sprite lateral para aspecto, via Texture)
        {
            Texture t = sprites.get("side_view_character");
            float tw = t.getWidth();
            float th = t.getHeight();
            float targetH = vh * PLAYER_HEIGHT_PCT;
            world.player.width = targetH * (tw / th);
            world.player.height = targetH;
        }

        // Trash size (comum para todos com base em um sprite representativo) usando Texture
        {
            String key = "Trash_Pixel1";
            Texture t = sprites.get(key);
            float tw = t.getWidth();
            float th = t.getHeight();
            float targetH = vh * TRASH_HEIGHT_PCT;
            float targetW = targetH * (tw / th);
            for (int i = 0; i < world.trashList.size; i++) {
                world.trashList.get(i).width = targetW;
                world.trashList.get(i).height = targetH;
            }
        }
    }

    /**
     * Renderiza o mundo usando a câmera do jogo.
     * - Atualiza a câmera e aplica sua matriz de projeção com `batch.setProjectionMatrix(camera.combined)`.
     * - Desenha o fundo cobrindo todo o viewport.
     * - Desenha lixo, caminhão e jogador, com fallback para `Texture` quando não há `TextureRegion`.
     * - Usa espelhamento horizontal (largura negativa) para o sprite lateral do jogador quando olhando à esquerda.
     * @param batch SpriteBatch já iniciado (fora desta classe deve-se chamar `batch.begin()`/`batch.end()`)
     * @param world estado atual do jogo
     */
    public void render(SpriteBatch batch, GameWorld world) {
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        // keep hitboxes equal to render sizes
        syncHitboxesToSpriteSizes(world);

        // Fundo por nível: usa a chave definida em world.level.backgroundKey como fallback
        batch.setColor(Color.WHITE);
        String bgKey = (world.level != null && world.level.backgroundKey != null) ? world.level.backgroundKey : "Street";
        Texture bgTex = sprites.get(bgKey);
        if (bgTex == sprites.white()) bgTex = sprites.get("Street");
        batch.draw(bgTex, 0, 0, worldCamera.viewportWidth, worldCamera.viewportHeight);

        // Lixo: usa a chave estável armazenada em cada Trash (spriteKey). Fallback para pixel branco.
        for (int i = 0; i < world.trashList.size; i++) {
            Trash t = world.trashList.get(i);
            String key = t.spriteKey;
            if (key != null) {
                Texture tt = sprites.get(key);
                batch.draw(tt, t.x, t.y, t.width, t.height);
            } else {
                batch.draw(white, t.x, t.y, t.width, t.height);
            }
        }

        // Renderização do jogador e caminhão
        // Observação: quando p.isDefeated == true, desenhamos o jogador antes e o caminhão depois (por cima)
        // para reforçar o efeito visual de atropelamento.
        Player p = world.player;
        Texture truckTex = sprites.get("Art Garbage Truck_Right");

        if (p.isDefeated) {
            // Desenha o jogador derrotado primeiro (embaixo)
            Texture texDefeated = sprites.get("DefeatedCharacter");
            if (texDefeated == null) texDefeated = sprites.white();

            float scale = 1f;
            batch.draw(texDefeated,
                      p.x - (p.width * (scale - 1)) / 2,
                      p.y - (p.height * (scale - 1)) / 2,
                      p.width/2, p.height/2,
                      p.width * scale, p.height * scale,
                      1f, 1f,
                      180f,
                      0, 0,
                      texDefeated.getWidth(), texDefeated.getHeight(),
                      false, false);

            // ...e depois o caminhão por cima
            batch.draw(truckTex, world.truck.x, world.truck.y, world.truck.width, world.truck.height);
        } else {
            // Jogador vivo: mantém ordem atual (caminhão abaixo, jogador acima)
            batch.draw(truckTex, world.truck.x, world.truck.y, world.truck.width, world.truck.height);

            // Jogador normal: usa frente, costas ou lado; reflete lado para a esquerda
            Texture texFront = sprites.get("front_view_character");
            Texture texBack  = sprites.get("back_view_character");
            Texture texSide  = sprites.get("side_view_character");

            float ax = Math.abs(p.faceX);
            float ay = Math.abs(p.faceY);
            if (ax >= ay && ax > 0f) {
                boolean left = p.faceX < 0f;
                float drawX = left ? p.x + p.width : p.x;
                batch.draw(texSide, drawX, p.y, left ? -p.width : p.width, p.height);
            } else if (p.faceY > 0f) {
                batch.draw(texBack, p.x, p.y, p.width, p.height);
            } else {
                batch.draw(texFront, p.x, p.y, p.width, p.height);
            }
        }

        batch.setColor(Color.WHITE);
    }
}
