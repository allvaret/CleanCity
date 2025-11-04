package br.cleancity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Gerencia recursos de renderização (texturas e fontes).
 *
 * Recursos básicos:
 * - Pixel branco 1x1 (útil para placeholders e retângulos coloridos).
 * - Fonte padrão do LibGDX para HUD.
 *
 * Carregamento de sprites:
 * - Usa `Gdx.files.internal("sprites")` para listar e carregar imagens soltas da pasta de assets `sprites/`.
 * - Define filtro `Nearest` para evitar borrões em pixel art (mantém bordas nítidas ao escalar).
 *
 * Ciclo de vida:
 * - `dispose()` libera texturas únicas, a textura branca e a fonte. Evita vazamentos de memória na GPU.
 */
public class SpriteManager {
    private final Texture white;
    private final BitmapFont font;
    private final Map<String, Texture> textures;

    /**
     * Cria a textura branca 1x1 e a fonte padrão do LibGDX e carrega sprites.
     */
    public SpriteManager() {
        // Pixel branco 1x1
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        white = new Texture(pm);
        pm.dispose();

        font = new BitmapFont();
        textures = new HashMap<>();


        // Carrega imagens soltas em assets/sprites (no runtime: "sprites/") como fallback
        try {
            FileHandle spritesDir = Gdx.files.internal("sprites");
            if (spritesDir.exists() && spritesDir.isDirectory()) {
                for (FileHandle fh : spritesDir.list()) {
                    if (fh.isDirectory()) continue;
                    String ext = fh.extension().toLowerCase();
                    if (!(ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg"))) continue;

                    // Ignore atlas output pages like sprites.png, sprites2.png, ...
                    String baseName = fh.nameWithoutExtension();
                    if (baseName.startsWith("sprites")) continue;

                    Texture tex = new Texture(fh);
                    tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    textures.put(baseName, tex);
                    textures.put(fh.name(), tex);
                }
            }
        } catch (Exception ignored) {
            // Se algo falhar, continuamos com o pixel branco e a fonte
        }
    }



    /** Obtém uma textura pelo nome (chave). Se não existir, retorna o pixel branco. */
    public Texture get(String key) {
        Texture t = textures.get(key);
        return t != null ? t : white;
    }

    /** Verifica se uma textura existe. */
    public boolean has(String key) {
        return textures.containsKey(key);
    }

    /** Retorna a textura branca 1x1. */
    public Texture white() { return white; }

    /** Retorna a fonte padrão. */
    public BitmapFont font() { return font; }

    /** Libera os recursos gráficos alocados. */
    public void dispose() {
        // No atlas to dispose
        Set<Texture> unique = new HashSet<>(textures.values());
        for (Texture t : unique) t.dispose();
        white.dispose();
        font.dispose();
    }
}
