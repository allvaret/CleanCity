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

    public SoundManager(AssetManager assets) {
        this.assets = assets;
    }

    // Registra os caminhos para carregar
    public void queueLoad(){
        // Musica
        assets.load("sounds/8bit Bossa.mp3", Music.class);

        // Sons
        assets.load("sounds/Jingle_lose_00.wav", Sound.class);
        assets.load("sounds/sfx_Delivery.wav", Sound.class);
        assets.load("sounds/sfx_sound_Collect.wav", Sound.class);
        assets.load("sounds/sfx_sound_death.wav", Sound.class);
        assets.load("sounds/sfx_Win.wav", Sound.class);
    }

    public void create(){
        // Musica
        musics.put(Mfx.TRACK, assets.get("sounds/8bit Bossa.mp3", Music.class));

        // Sons (Jingle é muito pequeno para usar Music.class)
        sounds.put(Sfx.LOSE, assets.get("sounds/Jingle_lose_00.wav", Sound.class));
        sounds.put(Sfx.DELIVERY, assets.get("sounds/sfx_Delivery.wav", Sound.class));
        sounds.put(Sfx.C_TRASH, assets.get("sounds/sfx_sound_Collect.wav", Sound.class));
        sounds.put(Sfx.DEATH, assets.get("sounds/sfx_sound_death.wav", Sound.class));
        sounds.put(Sfx.WIN, assets.get("sounds/sfx_Win.wav", Sound.class));
    }

    // Métodos dos Sounds
    // É um long, pois quando usamos "play" do LibGDX ele retorna um ID do playback
    public long play(Sfx sfx, float volume){
        Sound s = sounds.get(sfx);
        if (s == null) return -1; // Not Found / Nothing happened
        return s.play(masterVolume * volume);
    }

    public long play(Sfx sfx){return play(sfx, 1f);}

    public void stop(Sfx sfx){
        Sound s = sounds.get(sfx);
        if (s !=null) s.stop();
    }

    // Métodos da Music
    public long play(Mfx mfx, float volume){
        Sound m = sounds.get(mfx);
        if (m == null) return -1; // Not Found / Nothing happened
        return m.play(masterVolume * volume);
    }

    public long play(Mfx mfx){return play(mfx, 1f);}

    public void stop(Mfx mfx){
        Sound m = sounds.get(mfx);
        if (m !=null) m.stop();
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
