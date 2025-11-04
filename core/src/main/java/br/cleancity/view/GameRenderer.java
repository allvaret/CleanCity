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
 * Conceitos principais do LibGDX usados aqui:
 * - {@link OrthographicCamera}: define um "mundo" 2D com largura/altura lógicas e gera a matriz de projeção.
 * - {@link SpriteBatch}: lote de desenho rápido para sprites 2D; é necessário chamar `begin()`/`end()` fora desta classe.
 * - {@link Texture}: sprites carregados diretamente pelo `SpriteManager` a partir de `assets/sprites/`.
 *
 * Técnicas de renderização:
 * - Sincronização de hitboxes: `syncHitboxesToSpriteSizes()` calcula largura/altura de cada entidade como porcentagem da
 *   altura do viewport da câmera, preservando o aspecto do sprite (width = height * (tw/th)). Isso mantém colisões e
 *   desenho consistentes.
 * - Espelhamento horizontal: desenhar com largura negativa (`batch.draw(..., -width, height)`) reflete o sprite no eixo X.
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
    private static final float TRUCK_HEIGHT_PCT = 0.15f;
    private static final float PLAYER_HEIGHT_PCT = 0.10f;

    // Mantém as hitboxes com os mesmos tamanhos usados na renderização (percentual do viewport, preservando aspecto).
    // Calcula width/height-alvo a partir do aspecto do sprite e de uma fração da altura do viewport.
    private void syncHitboxesToSpriteSizes(GameWorld world) {
        float vh = worldCamera.viewportHeight;

        // Truck size (usa Texture direta)
        {
            Texture t = sprites.get("Art Garbage Truck_Right");
            float tw = t.getWidth();
            float th = t.getHeight();
            float targetH = vh * TRUCK_HEIGHT_PCT;
            float targetW = targetH * (tw / th);
            world.truck.width = targetW;
            world.truck.height = targetH;
        }

        // Player size (usa o sprite lateral para aspecto, via Texture)
        {
            Texture t = sprites.get("side_view_character_Final");
            float tw = t.getWidth();
            float th = t.getHeight();
            float targetH = vh * PLAYER_HEIGHT_PCT;
            float targetW = targetH * (tw / th);
            world.player.width = targetW;
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

        /// Fundo (se existir). Usa Street (atlas) ou Street1 como fallback; cobre o viewport da câmera do mundo
        batch.setColor(Color.WHITE);
        Texture bgTex = sprites.get("Street");
        if (bgTex == sprites.white()) bgTex = sprites.get("Street1");
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

        // Caminhão
        Texture truckTex = sprites.get("Art Garbage Truck_Right");
        batch.draw(truckTex, world.truck.x, world.truck.y, world.truck.width, world.truck.height);

        // Jogador direcional: usa frente, costas ou lado; reflete lado para a esquerda
        Player p = world.player;
        Texture texFront = sprites.get("front_view_character");
        Texture texBack  = sprites.get("back_view_character");
        Texture texSide  = sprites.get("side_view_character_Final");

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

        batch.setColor(Color.WHITE);
    }
}
