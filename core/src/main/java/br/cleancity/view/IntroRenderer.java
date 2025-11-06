package br.cleancity.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Renderizador da tela de introdução.
 *
 * Função: exibir 4 imagens (intro1..intro4) como um slideshow com transições
 * de fade-in → hold → fade-out e avanço automático. O controlador do jogo
 * (ex.: CleanCityGame) decide a tecla de pulo e chama {@link #skip()}.
 *
 * Como os assets são carregados:
 * - As texturas são obtidas via {@link SpriteManager#get(String)} com as chaves
 *   "intro1", "intro2", "intro3" e "intro4". O SpriteManager mapeia o nome do
 *   arquivo (sem extensão) para a textura correspondente em assets/sprites/.
 *
 * Como o fade funciona (alpha no SpriteBatch):
 * - O {@link com.badlogic.gdx.graphics.g2d.SpriteBatch} tem uma cor multiplicativa global
 *   que afeta todos os draws até ser alterada. Ao definir `batch.setColor(1,1,1,alpha)`,
 *   aplicamos um alpha (transparência) ao desenho do slide atual, implementando o fade
 *   sem precisar criar texturas adicionais.
 *
 * Fluxo por frame:
 * - {@link #update(float)} avança o temporizador, troca de slide quando necessário.
 * - {@link #render(SpriteBatch)} desenha a imagem atual em tela cheia, aplicando o alpha do fade
 *   e exibindo uma dica textual.
 *
 * Importante:
 * - O descarte das texturas é responsabilidade do {@link SpriteManager}. Esta classe não
 *   deve chamar `dispose()` nas texturas dos slides.
 */
public class IntroRenderer {
    private final SpriteManager sprites;
    private final Texture[] slides;
    private int index = 0;
    private float timer = 0f;
    private boolean done = false;

    // Durações em segundos
    private static final float FADE_IN = 0.6f;
    private static final float HOLD = 2.5f;
    private static final float FADE_OUT = 0.6f;

    /**
     * Constrói o renderizador de intro carregando as texturas "intro1".."intro4" do SpriteManager.
     *
     * Detalhes LibGDX:
     * - {@link SpriteManager#get(String)} localiza e carrega (ou reutiliza do cache) a textura do asset.
     *   As chaves costumam ser o nome do arquivo sem extensão colocado em `lwjgl3/assets/sprites/`.
     */
    public IntroRenderer(SpriteManager sprites) {
        this.sprites = sprites;
        this.slides = new Texture[] {
            sprites.get("intro1"), // busca textura para a primeira página de intro
            sprites.get("intro2"), // segunda página
            sprites.get("intro3"), // terceira página
            sprites.get("intro4")  // quarta página
        };
    }

    /**
     * Atualiza o temporizador do slide atual e avança automaticamente quando
     * a soma FADE_IN + HOLD + FADE_OUT é atingida.
     *
     * Parâmetros:
     * - delta: tempo em segundos desde o último frame (geralmente vindo de Gdx.graphics.getDeltaTime()).
     */
    public void update(float delta) {
        if (done) return;            // não faz nada se a intro já terminou
        timer += delta;              // acumula tempo do slide atual
        float total = FADE_IN + HOLD + FADE_OUT;
        if (timer >= total) next();  // troca de slide quando ciclo termina
    }

    /**
     * Pula para o próximo slide imediatamente. Se já estiver no último, marca a intro como concluída.
     * Pode ser acionado, por exemplo, ao pressionar ENTER no controlador principal.
     */
    public void skip() {
        if (done) return;
        next();
    }

    /**
     * Avança o índice de slide e reseta o temporizador; se não houver mais slides, finaliza a intro.
     */
    private void next() {
        if (index < slides.length - 1) {
            index++;
            timer = 0f;
        } else {
            done = true;
        }
    }

    /**
     * Desenha o slide atual em tela cheia aplicando o alpha de transição no SpriteBatch.
     *
     * Detalhes LibGDX usados aqui:
     * - {@link Gdx#graphics#getWidth()} e {@link Gdx#graphics#getHeight()} retornam o tamanho atual da janela.
     * - {@link SpriteBatch#setColor(float, float, float, float)} define a cor multiplicativa do batch;
     *   ao alterar o alpha (4º parâmetro), conseguimos o efeito de fade no desenho seguinte.
     * - {@link SpriteBatch#draw(com.badlogic.gdx.graphics.Texture, float, float, float, float)} desenha
     *   a textura com largura/altura informadas (aqui, full-screen). Em imagens com proporção diferente
     *   da janela, haverá esticamento; é normal para uma intro. Podemos evoluir para letterboxing se desejar.
     */
    public void render(SpriteBatch batch) {
        if (done) return;
        Texture slide = slides[Math.max(0, Math.min(index, slides.length - 1))];
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        // Calcula alpha de transição com base no timer e nas constantes de duração
        float alpha;
        if (timer < FADE_IN) alpha = timer / FADE_IN;         // 0 → 1 durante o fade-in
        else if (timer < FADE_IN + HOLD) alpha = 1f;          // mantém 1.0 durante o hold
        else {
            float t = timer - (FADE_IN + HOLD);
            alpha = Math.max(0f, 1f - (t / FADE_OUT));        // 1 → 0 durante o fade-out
        }

        // Aplica alpha, desenha em tela cheia e restaura a cor do batch
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(slide, 0, 0, sw, sh);
        batch.setColor(1f, 1f, 1f, 1f);

        // Tecla para pular (o controlador decide qual tecla chama skip())
        BitmapFont f = sprites.font();
        f.draw(batch, "ENTER para pular", 20, 30);
    }

    public boolean isDone() { return done; }
}
