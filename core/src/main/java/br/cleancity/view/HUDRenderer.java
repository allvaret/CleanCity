package br.cleancity.view;

import br.cleancity.model.Score;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Responsável por desenhar informações de interface (HUD): pontuação, lixo carregado,
 * tempo restante e mensagens de estado.
 *
 * Conceitos:
 * - Câmera do HUD: `OrthographicCamera` configurada no espaço de tela (pixels). Antes de desenhar o HUD,
 *   trocamos a matriz de projeção do `SpriteBatch` para `hudCamera.combined`.
 * - Fonte: `BitmapFont` padrão do LibGDX. Não possui métricas avançadas; usamos aproximações simples para centralizar.
 * - Coordenadas: desenhamos a partir da margem superior esquerda com offsets fixos para simplicidade.
 */
public class HUDRenderer {
    private final BitmapFont font;
    private final OrthographicCamera hudCamera;

    /**
     * Configura a câmera do HUD no espaço de tela e obtém os recursos visuais.
     */
    public HUDRenderer(SpriteManager sprites, float screenWidth, float screenHeight) {
        this.font = sprites.font();
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, screenWidth, screenHeight);
    }

    /**
     * Desenha os elementos do HUD, alternando a projeção do batch para a câmera do HUD.
     * Mostra valores numéricos simples e mensagens de fim de jogo.
     * @param batch SpriteBatch já iniciado
     * @param score pontuação atual
     * @param carried quantidade de lixo carregado pelo jogador
     * @param timeLeft tempo restante (segundos)
     * @param gameOver indica se o jogo terminou
     * @param gameWon indica se o jogador venceu
     */
    public void render(SpriteBatch batch, Score score, int carried, float timeLeft, boolean gameOver, boolean gameWon) {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);
        font.draw(batch, "Pontuação: " + score.value, 10, hudCamera.viewportHeight - 10);
        font.draw(batch, "Lixo carregado: " + carried, 10, hudCamera.viewportHeight - 30);
        font.draw(batch, String.format("Tempo: %.0f", Math.max(0f, timeLeft)), 10, hudCamera.viewportHeight - 50);

        if (gameOver) {
            if (gameWon) {
                String msg = "Você venceu!";
                float width = font.getRegion().getRegionWidth(); // aproximação; fonte padrão não possui métricas completas
                float x = hudCamera.viewportWidth * 0.5f - width * 0.5f;
                float y = hudCamera.viewportHeight * 0.9f;
                font.draw(batch, msg, x, y);

                String hint = "Pressione R para jogar novamente \nPressione N para avançar para o próximo level";
                font.draw(batch, hint, x, y - 20 );
            } else {
                String msg = "Game Over";
                float width = font.getRegion().getRegionWidth(); // aproximação; fonte padrão não possui métricas completas
                float x = hudCamera.viewportWidth * 0.5f - width * 0.5f;
                float y = hudCamera.viewportHeight * 0.5f;
                font.draw(batch, msg, x, y);

                String hint = "Pressione R para tentar novamente";
                font.draw(batch, hint, x, y - 20);
            }
        }
    }
}
