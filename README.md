# CleanCity

## Guia do Jogo (PT-BR)

Um jogo desktop (Java, LibGDX LWJGL3) onde você coleta os lixos e os entrega no caminhão antes do tempo acabar. Inclui fases (níveis), HUD em PT-BR, condição de vitória e reinício/avanço de fase pelo teclado.

## Requisitos

- Java 17
- Git (para clonar o repositório)
- SO: Windows, macOS ou Linux (LWJGL3 apenas desktop)

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
  - Importe como projeto Gradle e execute a tarefa `lwjgl3:run`

### Assets

- As imagens ficam em `assets/sprites/`
- As texturas são carregadas como arquivos soltos pelo `SpriteManager` via `sprites.get(key)`
- Sprites usados atualmente:
  - Fundo: `Street`, `Street1`
  - Jogador: `front_view_character`, `back_view_character`, `side_view_character_Final`
  - Caminhão: `Art Garbage Truck_Right`
  - Lixos: `Trash_Pixel1` … `Trash_Pixel6`
  - Intro: `intro1` … `intro4`
  - Defeated: `DefeatedCharacter`

## Controles

- **Movimento**: Setas ou WASD
- **Reiniciar fase**: R
- **Próxima fase**: N (após vencer)
- **Pular introdução**: ENTER

## Objetivo e Regras

- **Coleta**: passe sobre o lixo para coletá-lo (incrementa "Lixo carregado").
- **Entrega**: encoste no caminhão pelas laterais ou traseira para entregar (converte em "Pontuação"). A frente não entrega, te atropela.
- **Caminhão**: inicia na borda esquerda e move para a direita, com velocidade baseada no tempo da fase.
- **Tempo**: regressivo; ao zerar, o jogo termina.
- **Vitória**: quando não houver mais lixo no mapa e o jogador não estiver carregando lixo. Ao vencer, o jogo é pausado e o caminhão para.
- **Derrota**: apenas a frente do caminhão é letal.

## HUD

- Exibe: "Pontuação", "Lixo carregado", "Tempo"
- Mensagens:
  - Vitória: "Você venceu!" (dica para N)
  - Derrota: "Fim de jogo" (dica para R)
  - Coleta todos os lixos: "Limpe esta rua antes de avançar!"
  - Todos os níveis completados: "Parabéns! Você completou todos os níveis!"

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

## Estrutura do Projeto

- `core/`: código do jogo (model, controller, view)
  - `br/cleancity/model/`
    - `GameWorld`, `Player`, `Trash`, `Truck`, `Score`, `Level`
  - `br/cleancity/controller/`
    - `InputController`, `GameController`, `CollisionHandler`
  - `br/cleancity/view/`
    - `SpriteManager` (carrega texturas soltas e mantém um pixel branco 1x1 e fonte padrão)
    - `GameRenderer` (mundo) e `HUDRenderer` (interface)
  - `br/cleancity/CleanCityGame` (ciclo de vida LibGDX e níveis)

## Conceitos-chave (LibGDX)

- **OrthographicCamera**: define um "mundo" 2D com dimensões lógicas; aplicamos `camera.combined` no `SpriteBatch` antes de desenhar.
- **SpriteBatch**: desenho de sprites iniciado/encerrado em `CleanCityGame.render()`.
- **Texturas**: `SpriteManager.get(key)` carrega `Texture` e armazena na GPU.
- **HUD com câmera própria**: o HUD troca a projeção do `SpriteBatch` para coordenadas de tela.

### Tuning de colisão

- Ajuste da hitbox do caminhão: em `core/src/main/java/br/cleancity/controller/CollisionHandler.java`, constante `TRUCK_COLLISION_SCALE` (ex.: `0.9f`).
- Largura da faixa letal frontal: método `isHitByTruckFrontBounds(...)`, variável `lethalStripWidth` (ex.: `12f`).

## Detalhes Técnicos

- Tamanhos de render são percentuais da altura do viewport, preservando o aspecto e tamanho do sprite em `GameRenderer`.
- `syncHitboxesToSpriteSizes()` mantém as hitboxes consistentes com o que é desenhado.
- A colisão do caminhão usa uma hitbox reduzida e centralizada (por padrão 90% do tamanho visual) aplicada em `CollisionHandler.update()`; isso evita o "retângulo invisível" muito maior que o sprite.
- A velocidade inicial do caminhão é calculada em `GameWorld` com base na largura do mundo e na largura inicial do caminhão; o tamanho visual final é ajustado no render.
- Cada `Trash` possui `spriteKey` estável, evitando que os sprites remanescentes mudem após coletas (Bug inicial).
