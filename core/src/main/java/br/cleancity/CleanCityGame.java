package br.cleancity;

import br.cleancity.controller.GameController;
import br.cleancity.controller.InputController;
import br.cleancity.controller.CollisionHandler;
import br.cleancity.model.GameWorld;
import br.cleancity.view.GameRenderer;
import br.cleancity.view.HUDRenderer;
import br.cleancity.view.SpriteManager;
import br.cleancity.view.IntroRenderer;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import br.cleancity.model.Level;

/**
 * Ponto de entrada do jogo no LibGDX.
 *
 * Esta classe organiza os principais componentes seguindo uma separação de responsabilidades
 * semelhante a MVC:
 * - Modelo (`br.cleancity.model.*`): estado do jogo, entidades e regras básicas
 * - Controle (`br.cleancity.controller.*`): atualiza o estado a cada frame com base em entrada e regras
 * - Visão (`br.cleancity.view.*`): desenha o mundo e o HUD usando os recursos gráficos
 *
 * Ciclo de vida do LibGDX:
 * - `create()`: inicializa recursos (texturas, fontes, câmeras) e constrói os níveis
 * - `render()`: é chamado a cada frame; atualiza lógica (controle/colisões) e desenha (mundo/HUD)
 * - `dispose()`: libera os recursos alocados na GPU/CPU
 *
 * Controles:
 * - Setas ou WASD: mover jogador
 * - R: reiniciar o nível atual
 * - N: ir para o próximo nível
 */
public class CleanCityGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpriteManager sprites;
    private GameWorld world;
    private InputController input;
    private GameController controller;
    private CollisionHandler collisionHandler;
    private GameRenderer gameRenderer;
    private HUDRenderer hudRenderer;
    private IntroRenderer intro;

    // Timers para mensagens especiais do HUD
    private float collectAllMsgTimer = 0f;
    private float allLevelsCompletedMsgTimer = 0f;

    // Níveis
    private final List<Level> levels = new ArrayList<>();
    private int currentLevelIndex = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        sprites = new SpriteManager();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        input = new InputController();
        intro = new br.cleancity.view.IntroRenderer(sprites);
        gameRenderer = new GameRenderer(sprites, w, h);
        hudRenderer = new HUDRenderer(sprites);
        buildLevels();
        loadLevel(0);
    }

    /**
     * Define a lista de fases (níveis) do jogo.
     */
    private void buildLevels() {
        // totalTime, trashCount, trashSize, playerSpeed, truckW, truckH, backgroundKey
        levels.add(new Level(60f, 15, 18f, 250f, 64f, 32f, "Street"));
        levels.add(new Level(50f, 18, 20f, 260f, 64f, 32f, "StreetLDestN"));
        levels.add(new Level(40f, 22, 18f, 270f, 64f, 32f, "StreetRedUrban"));
        levels.add(new Level(35f, 24, 18f, 280f, 64f, 32f, "StreetMedianNight"));
        levels.add(new Level(27f, 28, 16f, 280f, 64f, 32f, "StreetBiscuit"));
    }

    /**
     * Carrega o nível informado (reinicia mundo e controladores).
     */
    private void loadLevel(int index) {
        currentLevelIndex = Math.max(0, Math.min(index, levels.size() - 1));
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        world = new GameWorld(w, h, levels.get(currentLevelIndex));
        controller = new GameController(world, input);
        collisionHandler = new CollisionHandler(world);
    }

    private void restart() {
        // Reinicia o nível atual
        loadLevel(currentLevelIndex);
    }

    private void nextLevel() {
        if (levels.isEmpty()) return;

        // Verifica se todo o lixo foi coletado e entregue
        if (world.trashList.size > 0 || world.carriedTrash > 0) {
            // Ativa mensagem no HUD por alguns segundos
            collectAllMsgTimer = 3.0f;
            return; // Não avança de nível se ainda houver lixo para coletar
        }

        int next = currentLevelIndex + 1;
        if (next >= levels.size()) {
            next = 0; // volta ao primeiro ao finalizar a lista
            // Ativa mensagem de conclusão de todos os níveis
            allLevelsCompletedMsgTimer = 3.0f;
        }
        loadLevel(next);
    }

    /**
     * Loop principal por frame: processa entrada, atualiza lógica e renderiza.
     * Usa `Gdx.graphics.getDeltaTime()` para obter o tempo entre frames (delta),
     * garantindo movimento e temporização independentes do FPS.
     */
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // Atualiza timers de mensagens do HUD
        if (collectAllMsgTimer > 0f) collectAllMsgTimer = Math.max(0f, collectAllMsgTimer - delta);
        if (allLevelsCompletedMsgTimer > 0f) allLevelsCompletedMsgTimer = Math.max(0f, allLevelsCompletedMsgTimer - delta);

        // Intro antes do jogo (delegado para IntroRenderer)
        if (intro != null && !intro.isDone()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                intro.skip();
            }
            intro.update(delta);
            ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f);
            batch.begin();
            intro.render(batch);
            batch.end();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) restart();
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) nextLevel();
        controller.update(delta);
        collisionHandler.update();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        gameRenderer.render(batch, world);
        hudRenderer.render(
            batch,
            world.score,
            world.carriedTrash,
            world.timeLeft,
            world.gameOver,
            world.gameWon,
            collectAllMsgTimer > 0f,
            allLevelsCompletedMsgTimer > 0f,
            delta
        );
        batch.end();
    }

    /**
     * Libera recursos gráficos alocados. Importante para evitar vazamentos
     * (texturas e fontes residem em memória de GPU/CPU no LibGDX).
     */
    @Override
    public void dispose() {
        batch.dispose();
        sprites.dispose();

    }
}
