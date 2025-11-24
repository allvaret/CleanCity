package br.cleancity.audio;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.EnumMap;


public class SoundManager {
    // Passamos AssetManager como atributo, mas instânciamos apenas na classe principal
    private final AssetManager assets;
    private final EnumMap<Sfx, Sound> sounds = new EnumMap<>(Sfx.class);
    private final EnumMap<Mfx, Music> musics = new EnumMap<>(Mfx.class);
    private final float masterVolume = 1.0f;
    private float waitTimer = 0;
    private Runnable pendingAction = null;

    public SoundManager(AssetManager assets) {
        this.assets = assets;
    }

    // Registra os caminhos para carregar
    public void queueLoad(){
        // Musica
        assets.load("sounds/8bit Bossa.mp3", Music.class);

        // Sons
        assets.load("sounds/Jingle_Lose_00.wav", Sound.class);
        assets.load("sounds/sfx_Delivery.wav", Sound.class);
        assets.load("sounds/sfx_sound_Collect.wav", Sound.class);
        assets.load("sounds/sfx_sound_death.wav", Sound.class);
        assets.load("sounds/sfx_Win.wav", Sound.class);
    }

    public void create(){
        // Musica
        musics.put(Mfx.TRACK, assets.get("sounds/8bit Bossa.mp3", Music.class));

        // Sons (Jingle é muito pequeno para usar Music.class)
        sounds.put(Sfx.LOSE, assets.get("sounds/Jingle_Lose_00.wav", Sound.class));
        sounds.put(Sfx.DELIVERY, assets.get("sounds/sfx_Delivery.wav", Sound.class));
        sounds.put(Sfx.C_TRASH, assets.get("sounds/sfx_sound_Collect.wav", Sound.class));
        sounds.put(Sfx.DEATH, assets.get("sounds/sfx_sound_death.wav", Sound.class));
        sounds.put(Sfx.WIN, assets.get("sounds/sfx_Win.wav", Sound.class));
    }

    public void loadAll(){
        queueLoad();
        assets.finishLoading(); // garante que estão carregados
        create();

    }

    // Tocar após x tempo
    public void waitAndRun(float ms, Runnable action){
        this.waitTimer = ms / 1000f;
        this.pendingAction = action;
    }

    // Atualiza o timer de execução com base no tempo da fase
    public void update(float delta) {
        if (waitTimer > 0) {
            waitTimer -= delta;
            if (waitTimer <= 0 && pendingAction != null) {
                pendingAction.run();
                pendingAction = null;
            }
        }
    }

    // Métodos dos Sounds
    // É um long, pois quando usamos "play" do LibGDX ele retorna um ID do playback
    public long playS(Sfx sfx, float volume){
        Sound s = sounds.get(sfx);
        if (s == null) return -1; // Not Found / Nothing happened
        return s.play(masterVolume * volume);
    }

    public void playS(Sfx sfx){
        playS(sfx, 1f);
    }

    public void stopS(Sfx sfx){
        Sound s = sounds.get(sfx);
        if (s !=null) s.stop();
    }

    // Métodos da Music
    public void playM(Mfx mfx, float volume){
        Music m = musics.get(mfx);
        if (m == null) return; // Not Found / Nothing happened
        m.setVolume(masterVolume * volume);
        m.play();
        m.setLooping(true);
    }

    public void playM(Mfx mfx){playM(mfx,1f);}

    public void stopM(Mfx mfx){
        Music m = musics.get(mfx);
        if (m !=null) m.stop();
    }

    public void fadeOut(Mfx mfx, float duration) {
        Music m = musics.get(mfx);
        if (m == null) return;

        new Thread(() -> {
            float volume = m.getVolume();
            int steps = 20;
            float delay = duration / steps;

            for (int i = 0; i < steps; i++) {
                volume -= (1f / steps);
                if (volume < 0) volume = 0;
                m.setVolume(volume);
                try { Thread.sleep((long)(delay * 1000)); } catch (Exception e) {}
            }

            m.pause();
        }).start();
    }

    public void fadeIn(Mfx mfx, float duration, float targetVolume) {
        Music m = musics.get(mfx);
        if (m == null) return;

        m.setVolume(0);
        m.play();
        m.setLooping(true);

        new Thread(() -> {
            float volume = 0f;
            int steps = 20;
            float delay = duration / steps;

            for (int i = 0; i < steps; i++) {
                volume += targetVolume / steps;
                if (volume > targetVolume) volume = targetVolume;
                m.setVolume(volume);
                try { Thread.sleep((long)(delay * 1000)); } catch (Exception e) {}
            }
        }).start();
    }

    public void disposeSound(){
        for(Sound s: sounds.values()){
            if (s != null) s.dispose();
        }
        for (Music m: musics.values()){
            if (m != null) m.dispose();
        }
        sounds.clear();
        musics.clear();
    }

}
