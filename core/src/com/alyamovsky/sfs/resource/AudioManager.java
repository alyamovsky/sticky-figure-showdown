package com.alyamovsky.sfs.resource;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;

public class AudioManager {
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    private final Music music;

    private final Sound blockSound;
    private final Sound booSound;
    private final Sound cheerSound;
    private final Sound clickSound;
    private final Sound hitSound;

    private final ArrayList<Sound> allSounds;

    public AudioManager(AssetManager assets) {
        music = assets.get(Assets.MUSIC);

        blockSound = assets.get(Assets.BLOCK_SOUND);
        booSound = assets.get(Assets.BOO_SOUND);
        cheerSound = assets.get(Assets.CHEER_SOUND);
        clickSound = assets.get(Assets.CLICK_SOUND);
        hitSound = assets.get(Assets.HIT_SOUND);

        allSounds = new ArrayList<>();
        allSounds.add(blockSound);
        allSounds.add(booSound);
        allSounds.add(cheerSound);
        allSounds.add(clickSound);
        allSounds.add(hitSound);

        music.setLooping(true);
    }

    public void toggleMusic() {
        musicEnabled = !musicEnabled;

        if (musicEnabled) {
            music.play();
        } else {
            music.stop();
        }
    }

    public void playMusic() {
        if (musicEnabled && !music.isPlaying()) {
            music.play();
        }
    }

    public void pauseMusic() {
        if (music.isPlaying()) {
            music.pause();
        }
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void playSound(String soundAsset) {
        if (soundEnabled) {
            switch (soundAsset) {
                case Assets.BLOCK_SOUND:
                    blockSound.play();
                    break;
                case Assets.BOO_SOUND:
                    booSound.play();
                    break;
                case Assets.CHEER_SOUND:
                    cheerSound.play();
                    break;
                case Assets.CLICK_SOUND:
                    clickSound.play();
                    break;
                case Assets.HIT_SOUND:
                    hitSound.play();
                    break;
            }
        }
    }

    public void pauseSounds() {
        for (Sound sound : allSounds) {
            sound.pause();
        }
    }

    public void resumeSounds() {
        for (Sound sound : allSounds) {
            sound.resume();
        }
    }

    public void stopSounds() {
        for (Sound sound : allSounds) {
            sound.stop();
        }
    }
}
