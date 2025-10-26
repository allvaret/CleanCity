package br.cleancity;

import br.cleancity.controller.GameController;
import br.cleancity.controller.InputController;
import br.cleancity.controller.CollisionHandler;
import br.cleancity.model.GameWorld;
import br.cleancity.view.GameRenderer;
import br.cleancity.view.HUDRenderer;
import br.cleancity.view.SpriteManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import br.cleancity.model.Level;

public class CleanCityGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private SpriteManager sprites;
    private GameWorld world;
    private InputController input;
    private GameController controller;
    private CollisionHandler collisionHandler;
    private GameRenderer gameRenderer;
    private HUDRenderer hudRenderer;

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
        gameRenderer = new GameRenderer(sprites, w, h);
        hudRenderer = new HUDRenderer(sprites, w, h);
        buildLevels();
        loadLevel(0);
    }

    /**
     * Define a lista de fases (níveis) do jogo.
     */
    private void buildLevels() {
        // totalTime, trashCount, trashSize, playerSpeed, truckW, truckH
        levels.add(new Level(60f, 15, 20f, 250f, 64f, 32f));
        levels.add(new Level(50f, 18, 20f, 260f, 64f, 32f));
        levels.add(new Level(40f, 22, 18f, 270f, 64f, 32f));
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
        int next = currentLevelIndex + 1;
        if (next >= levels.size()) {
            next = 0; // volta ao primeiro ao finalizar a lista
        }
        loadLevel(next);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) restart();
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) nextLevel();
        controller.update(delta);
        collisionHandler.update();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        gameRenderer.render(batch, world);
        hudRenderer.render(batch, world.score, world.carriedTrash, world.timeLeft, world.gameOver, world.gameWon);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        sprites.dispose();
    }
}
