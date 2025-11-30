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
import javafx.application.Platform;
import javax.sound.sampled.AudioFormat;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;


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
    @FXML
    private Label timeLabel;
    @FXML
    private ListView<String> playlistView;

    @FXML
    private Button managePlaylistButton;

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
            synchronized (this) {
                manualStop = false;
                // remove previous listener and close existing clip safely
                try {
                    if (musicaLinha != null && clip != null) {
                        clip.removeLineListener(musicaLinha);
                    }
                } catch (Exception ex) {
                }
                try {
                    if (clip != null && clip.isOpen()) {
                        clip.stop();
                        clip.flush();
                        clip.close();
                    }
                } catch (Exception ex) {
                }

                // create and open a fresh Clip for this file
                File file = listaDeReproducao.get(index).getFile();
                AudioInputStream musica = AudioSystem.getAudioInputStream(file);
                Clip newClip = AudioSystem.getClip();
                newClip.open(musica);
                clip = newClip;
                clip.setFramePosition(0);
                System.out.println("Tocando agora " + listaDeReproducao.get(index).getName());
                if (title != null) {
                    Platform.runLater(() -> title.setText(listaDeReproducao.get(index).getName()));
                }
                // update play button to show pause
                if (playbutton != null) {
                    Platform.runLater(() -> playbutton.setText("⏸"));
                }
                fimMusica();
                iniciarBarraProgresso();
                clip.start();
            }
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
            if (playbutton != null) Platform.runLater(() -> playbutton.setText("▶"));
        } else {
            // se chegou ao fim, reinicia antes de tocar
            if (clip.getFramePosition() >= clip.getFrameLength()) {
                clip.setFramePosition(0);
            }
            manualStop = false;
            clip.start();
            if (playbutton != null) Platform.runLater(() -> playbutton.setText("⏸"));
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
            // atualizar tempo
            if (timeLabel != null) {
                try {
                    double atual = 0;
                    double total = 0;
                    Audio current = listaDeReproducao.get(indexAtual);
                    total = current.getDuracaoSegundos();
                    AudioFormat formato = null;
                    try {
                        formato = current.getAudioStream().getFormat();
                    } catch (Exception ex) {
                        formato = null;
                    }
                    if (formato != null) {
                        atual = clip.getFramePosition() / formato.getFrameRate();
                    } else if (clip.getFrameLength() > 0) {
                        atual = (double) clip.getFramePosition() / clip.getFrameLength() * total;
                    }
                    final String text = String.format("%02d:%02d / %02d:%02d",
                            (int) (atual / 60), (int) (atual % 60), (int) (total / 60), (int) (total % 60));
                    Platform.runLater(() -> timeLabel.setText(text));
                } catch (Exception ex) {
                }
            }
        }
    }
@FXML
private void initialize() {
    if (volume != null) {
        volume.setValue(100); // valor inicial máximo
        volume.valueProperty().addListener((obs, oldVal, newVal) -> ajustarVolume(newVal.intValue()));
    }
    if (playlistView != null) {
        playlistView.setItems(FXCollections.observableArrayList());
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
        // populate playlist view
        if (playlistView != null) {
            ArrayList<String> names = new ArrayList<>();
            for (Audio a : listaDeReproducao) names.add(a.getName());
            Platform.runLater(() -> playlistView.setItems(FXCollections.observableArrayList(names)));
            if (!names.isEmpty()) Platform.runLater(() -> playlistView.getSelectionModel().select(indexAtual));
        }
        tocar(indexAtual);
    }

    @FXML
    private void playlistClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && playlistView != null) {
            int idx = playlistView.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < listaDeReproducao.size()) {
                indexAtual = idx;
                tocar(indexAtual);
            }
        }
    }

    @FXML
    private void openPlaylistManager() {
        PlaylistEditorWindow editor = new PlaylistEditorWindow(this, listaDeReproducao);
        editor.show();
    }
}