package client.res;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Streams .wav audio files without the need for external libraries.
 */
public class Sound {

    /*
     * Directories
     */
    public static final String AUDIO_DIR = ResourceLoader.DATA_DIR + "audio/";

    @SuppressWarnings("unused")
    private static final String[] sounds = { "advance", "anvil", "chisel", "click", "closedoor",
            "coins", "takeobject", "victory", "combat1a", "combat1b", "combat2a",
            "combat2b", "combat3a", "combat3b", "cooking", "death", "dropobject", "eat",
            "filljug", "fish", "foundgem", "recharge", "underattack", "mechanical", "mine",
            "mix", "spellok", "opendoor", /* "out_of_ammo", */ "potato", "spellfail",
            "prayeroff", "prayeron", "prospect", "shoot", "retreat", "secretdoor" };

//    // Testing
//    public static void main(String[] args) {
//        for (int i = 0; i < sounds.length; i++) {
//            play(sounds[i]);
//            try {
//                Thread.sleep(600);
//            } catch (InterruptedException e) {
//            }
//        }
//    }
    
    public static void play(String sound) {
        play(new File(AUDIO_DIR + sound + ".wav"));
    }

    private static void play(File file) {
        try {

            // Load the audio file.
            final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            // Listen for end of file.
            clip.addLineListener(new LineListener() {

                @Override
                public void update(LineEvent event) {
                    // Close the stream.
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }

            });

            // Begin streaming data from the file.
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
        } catch (Exception e) {
            Logger.getLogger(Sound.class.getName()).log(Level.WARNING, "Error streaming audio file: " + file.getName(),
                    e);
        }
    }

}
