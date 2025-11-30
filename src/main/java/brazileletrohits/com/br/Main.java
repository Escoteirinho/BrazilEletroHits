package brazileletrohits.com.br;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;

public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("primary.fxml"));
        Parent root = loader.load();
        scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("BrazilElectroHits");
    
        File diretorio = new File("music");
        FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
        File[] listaArquivos = diretorio.listFiles(wavFilter);
        Playlists allAudios = null;
        MenuPrincipal controleDeFaixas = loader.getController();

        try {
            File playlistFile = new File("playlist.xml");
            if (playlistFile.exists()) {
                // load saved playlist (paths must be valid)
                allAudios = Playlists.loadFromXml(playlistFile);
            }
        } catch (Exception e) {
            System.out.println("Falha ao carregar playlist.xml: " + e.getMessage());
        }

        if (allAudios == null) {
            if (listaArquivos == null || listaArquivos.length == 0) {
                System.out.println("Diretório vazio ou inválido: " + diretorio.getAbsolutePath());
            } else {
                allAudios = new Playlists("Musicas");
                for (File f : listaArquivos) {
                    System.out.println(f.getName());
                    try {
                        Audio musica = new Audio(f);
                        allAudios.addMusica(musica);
                    } catch (Exception ex) {
                        System.out.println("Ignorando arquivo inválido: " + f.getName());
                    }
                }
                // try to save generated playlist for next runs
                try {
                    allAudios.saveToXml(new File("playlist.xml"));
                } catch (Exception ex) {
                    System.out.println("Não foi possível salvar playlist.xml: " + ex.getMessage());
                }
            }
        }

        if (allAudios != null) {
            controleDeFaixas.playPlaylist(allAudios, 0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
