package brazileletrohits.com.br;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PlaylistEditorWindow {
    @FXML
    private ListView<String> availableList;
    @FXML
    private ListView<String> playlistList;
    @FXML
    private TextField playlistNameField;
    @FXML
    private ComboBox<String> savedPlaylists;
    @FXML
    private Button loadBtn, addBtn, removeBtn, playBtn, saveBtn, deleteBtn, closeBtn;

    private ArrayList<Audio> availableAudios;
    private ArrayList<Audio> playlistAudios;
    private MenuPrincipal controller;

    public PlaylistEditorWindow(MenuPrincipal controller, ArrayList<Audio> currentPlaylist) {
        this.controller = controller;
        this.playlistAudios = new ArrayList<>(currentPlaylist);
        this.availableAudios = new ArrayList<>();
        loadAvailableAudios();
    }

    @FXML
    public void initialize() {
        savedPlaylists.setItems(FXCollections.observableArrayList(PlaylistManager.listPlaylists()));
        updateAvailableList();
        updatePlaylistList();

        loadBtn.setOnAction(e -> loadPlaylistFromFile(savedPlaylists.getValue()));
        addBtn.setOnAction(e -> addToPlaylist());
        removeBtn.setOnAction(e -> removeFromPlaylist());
        playBtn.setOnAction(e -> playCurrentPlaylist());
        saveBtn.setOnAction(e -> savePlaylist());
        deleteBtn.setOnAction(e -> deletePlaylist(savedPlaylists.getValue()));
        closeBtn.setOnAction(e -> closeWindow());
    }
    
    private void loadAvailableAudios() {
        File musicDir = new File("music");
        FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
        java.io.File[] files = musicDir.listFiles(wavFilter);
        if (files != null) {
            for (java.io.File f : files) {
                try {
                    availableAudios.add(new Audio(f));
                } catch (Exception ex) {}
            }
        }
    }

    private void updateAvailableList() {
        ArrayList<String> names = new ArrayList<>();
        for (Audio aud : availableAudios) {
            names.add(aud.getName());
        }
        availableList.setItems(FXCollections.observableArrayList(names));
    }

    private void updatePlaylistList() {
        ArrayList<String> names = new ArrayList<>();
        for (Audio aud : playlistAudios) {
            names.add(aud.getName());
        }
        playlistList.setItems(FXCollections.observableArrayList(names));
    }

    private void addToPlaylist() {
        int index = availableList.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < availableAudios.size()) {
            playlistAudios.add(availableAudios.get(index));
            updatePlaylistList();
        }
    }

    private void removeFromPlaylist() {
        int index = playlistList.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < playlistAudios.size()) {
            playlistAudios.remove(index);
            updatePlaylistList();
        }
    }

    private void playCurrentPlaylist() {
        if (playlistAudios.isEmpty()) {
            showAlert("Erro", "Playlist vazia!", Alert.AlertType.WARNING);
            return;
        }
        Playlists pl = new Playlists("Playlist Temporária");
        for (Audio aud : playlistAudios) {
            pl.addMusica(aud);
        }
        controller.playPlaylist(pl, 0);
        showAlert("Sucesso", "Tocando playlist!", Alert.AlertType.INFORMATION);
    }

    private void savePlaylist() {
        String name = playlistNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Erro", "Digite o nome da playlist", Alert.AlertType.WARNING);
            return;
        }
        if (playlistAudios.isEmpty()) {
            showAlert("Erro", "Playlist vazia!", Alert.AlertType.WARNING);
            return;
        }
        try {
            Playlists pl = new Playlists(name);
            for (Audio aud : playlistAudios) {
                pl.addMusica(aud);
            }
            PlaylistManager.savePlaylist(name, pl);
            showAlert("Sucesso", "Playlist \"" + name + "\" salva!", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erro", "Erro ao salvar: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPlaylistFromFile(String name) {
        if (name == null || name.isEmpty()) {
            showAlert("Erro", "Selecione uma playlist", Alert.AlertType.WARNING);
            return;
        }
        try {
            Playlists pl = PlaylistManager.loadPlaylist(name);
            playlistAudios.clear();
            playlistAudios.addAll(pl.getListaMusicas());
            playlistNameField.setText(name);
            updatePlaylistList();
            showAlert("Sucesso", "Playlist \"" + name + "\" carregada!", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Erro", "Erro ao carregar: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deletePlaylist(String name) {
        if (name == null || name.isEmpty()) {
            showAlert("Erro", "Selecione uma playlist", Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Deletar playlist?");
        confirm.setContentText("Tem certeza que quer deletar \"" + name + "\"?");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (PlaylistManager.deletePlaylist(name)) {
                showAlert("Sucesso", "Playlist deletada!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erro", "Erro ao deletar", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}