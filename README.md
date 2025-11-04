# CleanCity

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

---

# Guia do Jogo (PT-BR)

Um jogo desktop (Java, LibGDX LWJGL3) onde você coleta lixo e entrega no caminhão antes do tempo acabar. Inclui fases (níveis), HUD em PT-BR, condição de vitória e reinício/avanço de fase pelo teclado.

## Requisitos

- Java 17 ou superior (recomendado 17 ou 21).
- Git (para clonar o repositório).
- SO: Windows, macOS ou Linux (LWJGL3 desktop).

Verifique sua versão do Java:
```bash
java -version
```

## Como executar

- Clonar e entrar no diretório do projeto:
```bash
git clone https://github.com/<seu-usuario>/<seu-repo>.git
cd <seu-repo>
```
- Rodar sem IDE:
  - Linux/macOS:
    ```bash
    ./gradlew lwjgl3:run
    ```
  - Windows (PowerShell ou CMD):
    ```powershell
    .\gradlew.bat lwjgl3:run
    ```
- Importar em IDE (opcional):
  - Importe como projeto Gradle e execute a tarefa `lwjgl3:run`.

### Assets

- As imagens ficam em `lwjgl3/assets/sprites/`.
- Não usamos mais TextureAtlas. As texturas são carregadas como arquivos soltos pelo `SpriteManager` via `sprites.get(key)`.
- Sprites usados atualmente:
  - Fundo: `Street`, `Street1`
  - Jogador: `front_view_character`, `back_view_character`, `side_view_character_Final`
  - Caminhão: `Art Garbage Truck_Right`
  - Lixos: `Trash_Pixel1` … `Trash_Pixel6`

## Controles

- Movimento: Setas ou WASD
- Reiniciar fase: R
- Próxima fase: N (após vencer)

## Objetivo e Regras

- Passe sobre o lixo para coletar (aumenta “Lixo carregado”).
- Encoste no caminhão para entregar (converte em “Pontuação”).
- O caminhão inicia na borda esquerda e move para a direita, com velocidade baseada no tempo da fase.
- O tempo é regressivo; ao zerar, o jogo termina (Fim de jogo).
- Vitória quando não há mais lixo no mundo e nada “Carregado”.

## HUD (em PT-BR)

- Exibe: “Pontuação”, “Lixo carregado”, “Tempo”.
- Mensagens:
  - Vitória: “Você venceu!” (dica para N)
  - Derrota: “Fim de jogo” (dica para R)

## Fases (Níveis)

- Os níveis definem: `tempo total`, `quantidade de lixo`, `tamanho do lixo`, `velocidade do jogador`, `tamanho do caminhão`.
- Para ajustar as fases, edite o método que cria a lista de níveis (por padrão em `CleanCityGame`, algo como `buildLevels()`):
```java
// Exemplo: totalTime, trashCount, trashSize, playerSpeed, truckW, truckH
levels.add(new Level(60f, 15, 20f, 250f, 64f, 32f));
levels.add(new Level(50f, 18, 20f, 260f, 64f, 32f));
levels.add(new Level(40f, 22, 18f, 270f, 64f, 32f));
```
- Fluxo:
  - `R` reinicia a fase atual
  - `N` avança para a próxima

## Empacotamento (Distribuição)

- Gerar pacote ZIP com dependências e scripts:
  - Linux/macOS:
    ```bash
    ./gradlew lwjgl3:distZip
    ```
  - Windows:
    ```powershell
    .\gradlew.bat lwjgl3:distZip
    ```
- Saída: `lwjgl3/build/distributions/*.zip`
- Alternativa: pasta instalável local
  ```bash
  ./gradlew lwjgl3:installDist
  ```
  Saída: `lwjgl3/build/install/<AppName>/`

Observação: com LWJGL (nativos), `distZip`/`installDist` é mais confiável do que tentar um “fat jar”.

## Estrutura do projeto

- `core/`: código do jogo (modelos, controladores, renderização)
  - `br/cleancity/model/`
    - `GameWorld`, `Player`, `Trash`, `Truck`, `Score`, `Level`
  - `br/cleancity/controller/`
    - `InputController`, `GameController`, `CollisionHandler`
  - `br/cleancity/view/`
    - `SpriteManager` (carrega texturas soltas e mantém um pixel branco 1x1 e fonte padrão)
    - `GameRenderer` (mundo) e `HUDRenderer` (interface)
  - `br/cleancity/CleanCityGame` (ciclo de vida LibGDX e níveis)

## Conceitos-chave (LibGDX)

- **OrthographicCamera**: define um “mundo” 2D com dimensões lógicas; aplicamos `camera.combined` no `SpriteBatch` antes de desenhar.
- **SpriteBatch**: desenho eficiente de sprites; iniciado/encerrado em `CleanCityGame.render()`.
- **Texturas sem atlas**: `SpriteManager.get(key)` carrega `Texture` diretamente.
- **HUD com câmera própria**: o HUD troca a projeção do `SpriteBatch` para coordenadas de tela.

## Escalas, sprites e colisões

- Tamanhos de render são percentuais da altura do viewport, preservando o aspecto do sprite em `GameRenderer`.
- `syncHitboxesToSpriteSizes()` mantém as hitboxes consistentes com o que é desenhado.
- Cada `Trash` possui `spriteKey` estável, evitando que os sprites remanescentes mudem após coletas.

## CI (Opcional — GitHub Actions)

Crie `.github/workflows/build.yml` para empacotar automaticamente a cada push:
```yaml
name: build-desktop
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Build distZip
        run: ./gradlew -Dorg.gradle.jvmargs="-Xmx2g" lwjgl3:distZip
      - name: Upload artifact
        uses: actions/upload-artifact@v4
        with:
          name: desktop-dist
          path: lwjgl3/build/distributions/*.zip
```

## Solução de Problemas

- Avisos PipeWire no Linux (client-rt.conf): inofensivos.
- Se falhar por versão do Java, garanta que está usando 17+.
- Performance baixa: reduza a quantidade de lixo nas fases.

