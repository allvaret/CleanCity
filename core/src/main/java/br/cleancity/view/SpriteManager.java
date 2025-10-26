package br.cleancity.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Gerencia recursos simples de renderização (texturas e fontes) criados em runtime.
 * Evita dependência de arquivos de imagem usando um pixel branco 1x1.
 */
public class SpriteManager {
    private final Texture white;
    private final BitmapFont font;

    /**
     * Cria a textura branca 1x1 e a fonte padrão do LibGDX.
     */
    public SpriteManager() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        white = new Texture(pm);
        pm.dispose();
        font = new BitmapFont();
    }

    /** Retorna a textura branca 1x1. */
    public Texture white() { return white; }
    /** Retorna a fonte padrão. */
    public BitmapFont font() { return font; }

    /** Libera os recursos gráficos alocados. */
    public void dispose() {
        white.dispose();
        font.dispose();
    }
}
