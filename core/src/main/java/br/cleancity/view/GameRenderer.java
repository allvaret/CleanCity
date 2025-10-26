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
 */
public class GameRenderer {
    private final OrthographicCamera worldCamera;
    private final Texture white;

    public GameRenderer(SpriteManager sprites, float worldWidth, float worldHeight) {
        this.white = sprites.white();
        this.worldCamera = new OrthographicCamera();
        this.worldCamera.setToOrtho(false, worldWidth, worldHeight);
    }

    /**
     * Renderiza a cena do jogo: lixo (amarelo), caminhão (vermelho) e jogador (verde).
     * @param batch SpriteBatch já iniciado
     * @param world estado atual do jogo
     */
    public void render(SpriteBatch batch, GameWorld world) {
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);

        // Lixo em quadrados amarelos
        batch.setColor(Color.GOLD);
        for (int i = 0; i < world.trashList.size; i++) {
            Trash t = world.trashList.get(i);
            batch.draw(white, t.x, t.y, t.width, t.height);
        }

        // Caminhão em vermelho
        batch.setColor(Color.SCARLET);
        batch.draw(white, world.truck.x, world.truck.y, world.truck.width, world.truck.height);

        // Jogador em verde
        Player p = world.player;
        batch.setColor(Color.FOREST);
        batch.draw(white, p.x, p.y, p.width, p.height);
        batch.setColor(Color.WHITE);
    }
}
