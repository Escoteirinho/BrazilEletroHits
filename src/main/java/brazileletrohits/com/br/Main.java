package brazileletrohits.com.br;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Main extends Application {

    private static Scene scene;


    // Define os valores e inicializa a janela principal

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("primary.fxml"));
        Parent root = loader.load();
        scene = new Scene(root, 640, 420);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setTitle("BrazilEletroHits");
        
        File diretorio = new File("music");
        FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
        File[] listaArquivos = diretorio.listFiles(wavFilter);
        Playlists allAudios = null;
        MenuPrincipal controleDeFaixas = loader.getController();

        try {
            File playlistFile = new File("playlist.xml");
            if (playlistFile.exists()) {
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

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
