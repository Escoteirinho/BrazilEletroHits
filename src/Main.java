import javax.sound.sampled.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        File arquivo = new File("/home/sou_eu/Coisas do curso/Analise De Sinais/Arquivos/Áudios/4Seasons_Violin1.wav");
        if (!arquivo.exists() || !arquivo.isFile()) {
            System.err.println("Arquivo nao encontrado: " + arquivo.getAbsolutePath());
            return;
        }

        try (AudioInputStream musica = AudioSystem.getAudioInputStream(arquivo)) {
            Clip clip = AudioSystem.getClip();
            clip.open(musica);

            System.out.println("Comandos: p = play/pause, r = restart, s = stop, q = quit, h = help");
            // inicia tocando (opcional)
            clip.start();
            System.out.println("Tocando...");

            Thread inputThread = new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim().toLowerCase();
                        switch (line) {
                            case "p":
                            case "play":
                                if (clip.isRunning()) {
                                    clip.stop();
                                    System.out.println("Pausado");
                                } else {
                                    // se chegou ao fim, reinicia antes de tocar
                                    if (clip.getFramePosition() >= clip.getFrameLength()) {
                                        clip.setFramePosition(0);
                                    }
                                    clip.start();
                                    System.out.println("Tocando");
                                }
                                break;
                            case "r":
                            case "restart":
                                clip.stop();
                                clip.setFramePosition(0);
                                clip.start();
                                System.out.println("Reiniciado");
                                break;
                            case "s":
                            case "stop":
                                clip.stop();
                                clip.setFramePosition(0);
                                System.out.println("Parado");
                                break;
                            case "q":
                            case "quit":
                            case "exit":
                                System.out.println("Encerrando...");
                                return; // encerra o thread de input -> main vai fechar recursos
                            case "h":
                            case "help":
                                System.out.println("Comandos: p = play/pause, r = restart, s = stop, q = quit, h = help");
                                break;
                            default:
                                System.out.println("Comando desconhecido. Digite 'h' para ajuda.");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro lendo entrada: " + e.getMessage());
                }
            }, "InputThread");

            inputThread.setDaemon(true);
            inputThread.start();

            // aguarda o thread de input terminar (quando usuario digitar 'q' ou quando stdin fechar)
            inputThread.join();

            // garante que o clip pare e seja fechado
            if (clip.isRunning()) clip.stop();
            clip.close();
        }
    }
}