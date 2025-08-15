package util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioManager {
    private Clip gameplayClip;
    private Clip winClip;
    private Clip loseClip;

    private int gameplayFramePos = 0;

    public AudioManager() {
        gameplayClip = loadClip("audio/gameplay.wav");
        winClip = loadClip("audio/win.wav");
        loseClip = loadClip("audio/lose.wav");

        if (gameplayClip != null) {
            gameplayClip.loop(0);
        }
    }
    private Clip loadClip(String classpath) {
        try {
            URL url = getClass().getClassLoader().getResource(classpath);
            if(url == null) {
                System.err.println("Couldn't find resource " + classpath);
                return null;
            }
            AudioInputStream in = AudioSystem.getAudioInputStream(url);
            AudioFormat base = in.getFormat();

            AudioFormat decoded = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(), 16,
                    base.getChannels(), base.getChannels() * 2,
                    base.getSampleRate(), false
            );
            AudioInputStream din = AudioSystem.isConversionSupported(decoded,base)
                    ? AudioSystem.getAudioInputStream(decoded,in) :
                    in;

            DataLine.Info info = new DataLine.Info(Clip.class, decoded);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(din);
            return clip;
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Couldn't load clip " + classpath);
            return null;
        }
    }

    public void startGameplayLoop() {
        if (gameplayClip == null) return;
        stopGameplay();
        gameplayClip.setFramePosition(0);
        gameplayClip.loop(Clip.LOOP_CONTINUOUSLY);
        gameplayClip.start();
        gameplayFramePos = 0;
    }

    public void pauseGameplay() {
        if (gameplayClip == null) return;
        gameplayFramePos = gameplayClip.getFramePosition();
        gameplayClip.stop();
    }

    public void resumeGameplay() {
        if (gameplayClip == null) return;
        gameplayClip.setFramePosition(gameplayFramePos);
        gameplayClip.loop(Clip.LOOP_CONTINUOUSLY);
        gameplayClip.start();
    }

    public void stopGameplay() {
        if (gameplayClip == null) return;
        gameplayClip.stop();
        gameplayClip.flush();
    }

    public void playWin(){
        stopGameplay();
        playOnce(winClip);
        }

        public void playLose(){
        stopGameplay();
        playOnce(loseClip);
        }

        private void playOnce(Clip clip) {
        if(clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
        }

}


