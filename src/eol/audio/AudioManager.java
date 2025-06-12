package eol.audio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import javax.sound.sampled.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AudioManager {
    private static AudioManager instance;

    private HashMap<String, Clip> music = new HashMap<>();
    private HashMap<String, List<Clip>> sfx = new HashMap<>();
    private Clip musicPlaying;
    private boolean muted = false;

    // Private constructor to block other classes from creating a new audiomanager
    private AudioManager() {
    }

    /*
     * Allows for global access. e.x:
     * AudioManager.getInstance().playMusic("menu")
     */
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    // loads music, give filename and set hashmap id
    public void loadMusic(String id, String file) {
        Clip clip = createClip("/assets/music/" + file);
        if (clip != null) {
            //FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            //gainControl.setValue(-5.0f);
            music.put(id, clip);
            System.out.println("Loaded music: " + id);
        }
    }

    // Plays music given the id
    public void playMusic(String id) {
        Clip clip = music.get(id);
        if (clip == null || muted) return;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        musicPlaying = clip;
    }

    // stops currently playing music
    public void stopMusic() {
        if (musicPlaying != null) {
            musicPlaying.stop();
        }
    }

    // loads a pool of sounds, give filename and set hashmap id
    public void loadSound(String id, String file, int poolSize) {
        List<Clip> pool = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Clip clip = createClip("/assets/sounds/" + file);
            if (clip != null) {
                pool.add(clip);
            }
        }
        if (!pool.isEmpty()) {
            sfx.put(id, pool);
            System.out.println("Loaded sound: " + id);
        }
    }


    // Plays sound given the id
    public void playSound(String id) {
        List<Clip> pool = sfx.get(id);
        if (pool == null || muted) return;
        for (Clip clip : pool) {
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
                return;
            }
        }
        Clip clip = pool.get(0);
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    // creates a clip given the file path
    public Clip createClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("Audio resource not found: " + path);
                return null;
            }
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                return clip;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadAll() {
        loadMusic("menu", "menu.wav");
        loadMusic("songOne", "songOne.wav");
        loadMusic("songTwo", "songTwo.wav");
        loadMusic("songThree", "songThree.wav");
        loadMusic("songFour", "songFour.wav");
        loadMusic("songFive", "songFive.wav");

        loadSound("menuSound", "menuSound.wav", 3);
        loadSound("hit", "hitSound.wav", 6);
        loadSound("jump", "jumpSound.wav", 3);
        loadSound("sword", "swordSlash.wav", 6);
        loadSound("greatsword", "greatswordSlash.wav", 6);
        loadSound("dagger", "daggerSlash.wav", 6);
        loadSound("plasmaSword", "plasmaSlash.wav", 0);
        loadSound("mage_shoot", "mageShoot.wav", 20);
        loadSound("fire_blast", "fireBlast.wav", 6);
        loadSound("sand_storm", "sandStorm.wav", 6);
        loadSound("zombie_dead", "zombieDead.wav", 6);
        loadSound("newWave", "newWave.wav", 1);
        loadSound("burnTick", "burnTick.wav", 6);
        loadSound("beamShoot", "beamShoot.wav", 3);
        loadSound("epicFail", "epicFail.wav", 1);
    
        // load more as needed
    }

    public void toggleMuted() {
        muted = !muted;
    }

}