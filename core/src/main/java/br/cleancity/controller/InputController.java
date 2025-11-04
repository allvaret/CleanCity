package br.cleancity.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * Lê o teclado e converte em uma direção normalizada para movimentação do jogador.
 *
 * Mapeamento:
 * - Setas ou WASD em X/Y (LEFT/RIGHT, A/D, DOWN/UP, S/W).
 * - Quando ambas as direções são pressionadas (diagonal), normaliza com `Vector2.nor()`
 *   para manter a mesma velocidade efetiva em qualquer direção.
 *
 * Implementação via polling de `Gdx.input`(fica checando constantemente o input).
 */
public class InputController {
    private final Vector2 dir = new Vector2();

    /**
     * Retorna um vetor direção (x,y) com valores em {-1,0,1}, normalizado quando diagonal.
     * Usa setas e WASD.
     */
    public Vector2 getDirection() {
        dir.set(0f, 0f);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dir.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dir.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dir.y -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dir.y += 1f;
        if (dir.len2() > 1f) dir.nor();
        return dir;
    }
}
