package brazileletrohits.com.br;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


public class MenuPrincipal {
    private ArrayList<Audio> listaDeReproducao;
    private static Clip clip;
    private static int indexAtual = 0;
    private static boolean manualStop = false; // controla se o STOP foi ação do usuário
    private LineListener musicaLinha;
    private Timeline barraProgresso;

    @FXML
    private Button playbutton;
    @FXML
    private Button skipbutton;
    @FXML
    private Button previousbutton;
    @FXML
    private ProgressBar barraprogresso;
    @FXML
    private Slider volume;
    @FXML
    private ToolBar toolbar;
    @FXML
    private AnchorPane ancora;
    @FXML
    private Label title;

    public MenuPrincipal() {
        this.listaDeReproducao = new ArrayList<>();
        if (clip == null) {
            try {
                clip = AudioSystem.getClip();
            } catch (LineUnavailableException e) {
                throw new IllegalArgumentException("Não foi possivel localizar uma linha de saida de audio");
            }
        }
    }

    // Carrega e toca a música do índice atual
    private void tocar(int index) {
        if (listaDeReproducao == null || listaDeReproducao.isEmpty()) 
            return;
        if (index < 0 || index >= listaDeReproducao.size()) 
            return;
        try {
            manualStop = false;
            if (clip.isOpen()) {
                clip.close();
            }
            // Gere um novo arquivo de musica toda vez, evitando que de problema e nao tocar.
            File file = listaDeReproducao.get(index).getFile();
            AudioInputStream musica = AudioSystem.getAudioInputStream(file);
            clip.open(musica);
            clip.setFramePosition(0);
            System.out.println("Tocando agora " + listaDeReproducao.get(index).getName());
            if (title != null) {
                title.setText(listaDeReproducao.get(index).getName());
            }
            fimMusica();
            iniciarBarraProgresso();
            clip.start();
        } catch (Exception e) {
            System.out.println("Falha ao abrir áudio: " + e.getMessage());
        }
    }

    // Passa para a proxima musica quando termina atual
    private void fimMusica() {
        // Remove a musica antiga da linha de reproducao
        if (musicaLinha != null) {
            clip.removeLineListener(musicaLinha);
        }
        // Cria e adiciona a musica na linha de reproducao
        musicaLinha = event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                if (!manualStop) {
                    avancar();
                }
            }
        };
        clip.addLineListener(musicaLinha);
    }

    
    private void avancar() {
        indexAtual = (indexAtual + 1) % listaDeReproducao.size();
        tocar(indexAtual);
    }

    // Usa o OnAction dos botoes para fazer as acoes. Lembrando que o JAVAFX roda tudo diretamente em um loop interno.
    @FXML
    public void play(){
        if (clip == null) return;
        if (clip.isRunning()) {
            manualStop = true;
            clip.stop();
        } else {
            // se chegou ao fim, reinicia antes de tocar
            if (clip.getFramePosition() >= clip.getFrameLength()) {
                clip.setFramePosition(0);
            }
            manualStop = false;
            clip.start();
        }
    } 

    @FXML
    public void skip(){
        if (clip == null || listaDeReproducao.isEmpty()) return;
        manualStop = true;
        if (clip.isRunning()) clip.stop();
        if(clip.isOpen()){
            clip.close();
        }
        avancar();
    }

    @FXML
    public void previous(){
        if (clip == null || listaDeReproducao.isEmpty()) return;
        manualStop = true;
        if (clip.isRunning()) clip.stop();
        if(clip.isOpen()){
            clip.close();
        }
        indexAtual = (indexAtual - 1 + listaDeReproducao.size()) % listaDeReproducao.size();
        tocar(indexAtual);
    }

    private void iniciarBarraProgresso(){
        if (barraProgresso != null) 
            barraProgresso.stop();
        barraProgresso = new Timeline(
            new KeyFrame(Duration.millis(100), e -> atualizarBarra())
        );
        barraProgresso.setCycleCount(Timeline.INDEFINITE);
        barraProgresso.play();
    }

    private void atualizarBarra(){
        // Quando a musica estiver tocando, vai atualizando a barra com o rate marcado pelo SceneBuilder
        if (clip != null && barraprogresso != null && clip.isOpen()) {
            // Calculo do progresso baseado no frame atual, total e rate
            double progresso = (double) clip.getFramePosition() / clip.getFrameLength();
            barraprogresso.setProgress(progresso);
            // Para a barra
            if (progresso >= 1.0 || !clip.isRunning()) {
                barraProgresso.stop();
            }
        }
    }
@FXML
private void initialize() {
    if (volume != null) {
        volume.setValue(100); // valor inicial máximo
        volume.valueProperty().addListener((obs, oldVal, newVal) -> ajustarVolume(newVal.intValue()));
    }
}

@FXML
private void ajustarVolume(int valor) {
    if (clip != null && clip.isOpen()) {
        try {
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = control.getMinimum(); 
            float max = control.getMaximum(); 
            float volumePercent = valor / 100f;
            float db;
            if (volumePercent <= 0.0f) {
                db = min;
            } else {
                db = (float) (min + (max - min) * Math.log10(1 + 19 * volumePercent) / Math.log10(10));
            }
            if (db > max) db = max;
            control.setValue(db);
        } catch (Exception e) {
        }
    }
}

    // Inicializa playlist e começa a tocar do indice informado
    public void playPlaylist(Playlists playlist, int index) {
        listaDeReproducao = new ArrayList<>(playlist.getListaMusicas());
        if (listaDeReproducao.isEmpty()) 
            return;
        if (index < 0 || index >= listaDeReproducao.size()) 
            indexAtual = 0;
        else indexAtual = index;
        tocar(indexAtual);
    }
}