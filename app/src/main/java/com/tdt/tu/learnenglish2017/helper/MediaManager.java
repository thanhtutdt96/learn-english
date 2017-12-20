package com.tdt.tu.learnenglish2017.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by 1stks on 19-Dec-17.
 */

public class MediaManager {
    private SoundPool soundPool;
    private HashMap<String, Integer> soundPoolMap;
    private Context context;

    public MediaManager(Context context) {
        this.context = context;
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();
    }

    public void addSound(String name, int id) {
        soundPoolMap.put(name, soundPool.load(context, id, 1));
    }

    public void playSound(String name) {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        soundPool.play(soundPoolMap.get(name), volume, volume, 1, 0, 1);
    }

    public void release() {
        soundPool.release();
    }
}
