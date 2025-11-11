import javax.sound.sampled.*;
import java.io.File;

public class Audio extends AudioSystem {
    
    private String nome;

    public Audio(File arquivo) {
        this.nome = arquivo.getNome();
    }

    public String getName() {
        /**
         * Retorna uma string com o nome do arquivo de audio
         */
        return nome;
    }

    public double getDuracaoSegundos() {
        /**
         * Retorna um double com a duração em segundos do audio atraves da quantidade de frames e o frame rate do audio
         */
        double duracaoSegundos;
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo)) {
            AudioFormat formato = audio.getFormat();
            long num_frames = audio.getFrameLength();
            if (num_frames != AudioSystem.NOT_SPECIFIED && formato.getFrameRate() > 0) {
                duracaoSegundos = frames/formato.getFrameRate();
                return; 
            }
        }
        return duracaoSegundos;
    }
}