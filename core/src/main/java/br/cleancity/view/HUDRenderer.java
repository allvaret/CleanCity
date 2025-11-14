package br.cleancity.view;

import br.cleancity.model.Score;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Responsável por desenhar informações de interface (HUD): pontuação, lixo carregado,
 * tempo restante e mensagens de estado.
 */
public class HUDRenderer {
    private final OrthographicCamera hudCamera;
    private final BitmapFont font;

    /**
     * Configura a câmera do HUD no espaço de tela e cria uma fonte padrão para o HUD.
     * A fonte é a `BitmapFont` padrão do LibGDX, com cor branca e escala 1.0.
     */
    public HUDRenderer(SpriteManager sprites) {
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, 1280, 720);
        this.font = new BitmapFont(); // Cria uma fonte padrão do LibGDX
        this.font.getData().setScale(1f); 
        this.font.setColor(Color.WHITE); 
        
    }

    /**
     * Desenha os elementos do HUD, alternando a projeção do batch para a câmera do HUD.
     * Mostra valores numéricos simples, mensagens de fim de jogo e mensagens especiais.
     * @param batch SpriteBatch já iniciado
     * @param score pontuação atual
     * @param carried quantidade de lixo carregado pelo jogador
     * @param timeLeft tempo restante (segundos)
     * @param gameOver indica se o jogo terminou
     * @param gameWon indica se o jogador venceu
     * @param showCollectAll exibe o aviso "Colete todo o lixo antes de avançar!" quando true
     * @param showAllLevelsCompleted exibe "Parabéns! Você completou todos os níveis!" quando true
     * @param delta tempo desde o último frame
     */
    public void render(SpriteBatch batch, Score score, int carried, float timeLeft, boolean gameOver, boolean gameWon, boolean showCollectAll, boolean showAllLevelsCompleted, float delta) {
        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);

        // Desenha as informações básicas do HUD
        font.draw(batch, "Pontuação: " + score.value, 10, hudCamera.viewportHeight - 10);
        font.draw(batch, "Lixo carregado: " + carried, 10, hudCamera.viewportHeight - 30);
        font.draw(batch, String.format("Tempo: %.0f", Math.max(0f, timeLeft)), 10, hudCamera.viewportHeight - 50);

        // Mensagens de fim de jogo (prioriza vitória)
        if (gameWon) {
            font.draw(batch, "VOCÊ VENCEU! \nAperte 'n' para avançar para a próxima rua", 
                     (hudCamera.viewportWidth - 120) / 2, 
                     hudCamera.viewportHeight / 2);
        } else if (gameOver) {
            font.draw(batch, "FIM DE JOGO \nAperte 'r' para recomeçar", 
                     (hudCamera.viewportWidth - 100) / 2, 
                     hudCamera.viewportHeight / 2);
        }

        // Mensagens especiais (tentativa de avançar e fim de todos os níveis)
        if (showCollectAll) {
            font.draw(batch, "Limpe esta rua antes de avançar!",
                     (hudCamera.viewportWidth - 320) / 2,
                     hudCamera.viewportHeight - 30);
        }
        if (showAllLevelsCompleted) {
            font.draw(batch, "Parabéns! Você completou todos os níveis!",
                     (hudCamera.viewportWidth - 400) / 2,
                     hudCamera.viewportHeight - 30);
        }
    }
}