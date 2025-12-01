package brazileletrohits.com.br;

import javax.sound.sampled.*;
import java.io.File;

public class Audio {
    private String nome;
    private AudioInputStream audio;
    private File file;

    public Audio(File arquivo) {
        this.nome = arquivo.getName();
        this.file = arquivo;
        try {
            this.audio = AudioSystem.getAudioInputStream(arquivo);
        } catch (UnsupportedAudioFileException | java.io.IOException e) {
            throw new IllegalArgumentException("Não foi possível abrir o arquivo de áudio: " + arquivo, e);
        }
    }

    public String getName() {
        /* Retorna uma string com o nome do arquivo de audio */
        return nome;
    }

    public File getFile(){
        return file;
    }

    public AudioInputStream getAudioStream() {
        return audio;
    }

    public double getDuracaoSegundos() {
        /* Retorna um double com a duração em segundos do audio 
        atraves da quantidade de frames e o frame rate do audio */

        double duracaoSegundos = 0;
        AudioFormat formato = audio.getFormat();
        long num_frames = audio.getFrameLength();
        
        if (num_frames != AudioSystem.NOT_SPECIFIED && formato.getFrameRate() > 0) {
            duracaoSegundos = num_frames/formato.getFrameRate();
        }
        return duracaoSegundos;
    }
}
