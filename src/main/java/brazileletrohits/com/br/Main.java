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
    
        File diretorio = new File("C:\\Users\\User\\BrazilEletroHits2\\brazileletrohitsfx\\music");
        FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
        File[] listaArquivos = diretorio.listFiles(wavFilter);

        if (listaArquivos == null) {
            System.out.println("Diretório vazio ou inválido: " + diretorio.getAbsolutePath());
            return;
        }

        Playlists allAudios = new Playlists("Musicas");
        MenuPrincipal controleDeFaixas = loader.getController();

        for (File f : listaArquivos) {
            System.out.println(f.getName());
            Audio musica = new Audio(f);
            allAudios.addMusica(musica);
        }

        controleDeFaixas.playPlaylist(allAudios, 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
