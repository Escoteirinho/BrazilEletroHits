import javax.sound.sampled.*;
import java.io.File;

public class PlayTest {
    public static void main(String[] args) throws Exception {
        File f = new File("music\\Dumb - Nirvana.wav");
        if (!f.exists()) {
            System.err.println("File not found: " + f.getAbsolutePath());
            return;
        }
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(f)) {
            AudioFormat format = ais.getFormat();
            System.out.println("Format: " + format);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            System.out.println("Started playing");
            Thread.sleep(5000);
            clip.stop();
            clip.close();
            System.out.println("Stopped");
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Line unavailable: " + e.getMessage());
        }
    }
}
