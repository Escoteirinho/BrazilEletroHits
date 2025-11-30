package brazileletrohits.com.br;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class PlaylistEditorWindow {
    private Stage stage;
    private ListView<String> availableList;
    private ListView<String> playlistList;
    private ArrayList<Audio> availableAudios;
    private ArrayList<Audio> playlistAudios;
    private TextField playlistNameField;
    private MenuPrincipal controller;

    public PlaylistEditorWindow(MenuPrincipal controller, ArrayList<Audio> currentPlaylist) {
        this.controller = controller;
        this.playlistAudios = new ArrayList<>(currentPlaylist);
        this.availableAudios = new ArrayList<>();
        loadAvailableAudios();
        createWindow();
    }

    private void loadAvailableAudios() {
        File musicDir = new File("music");
        FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
        java.io.File[] files = musicDir.listFiles(wavFilter);
        if (files != null) {
            for (java.io.File f : files) {
                try {
                    availableAudios.add(new Audio(f));
                } catch (Exception ex) {
                    // ignore
                }
            }
        }
    }

    private void createWindow() {
        stage = new Stage();
        stage.setTitle("Gerenciar Playlist");
        stage.setWidth(700);
        stage.setHeight(500);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #45433c;");

        // Playlist name
        HBox nameBox = new HBox(5);
        Label nameLabel = new Label("Nome da Playlist:");
        nameLabel.setStyle("-fx-text-fill: white;");
        playlistNameField = new TextField();
        playlistNameField.setPrefWidth(300);
        nameBox.getChildren().addAll(nameLabel, playlistNameField);

        // Load saved playlists dropdown
        HBox loadBox = new HBox(5);
        Label loadLabel = new Label("Carregar salva:");
        loadLabel.setStyle("-fx-text-fill: white;");
        ComboBox<String> savedPlaylists = new ComboBox<>();
        savedPlaylists.setItems(FXCollections.observableArrayList(PlaylistManager.listPlaylists()));
        Button loadBtn = new Button("Carregar");
        loadBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");
        loadBtn.setOnAction(e -> loadPlaylistFromFile(savedPlaylists.getValue()));
        loadBox.getChildren().addAll(loadLabel, savedPlaylists, loadBtn);

        // Available musics and Playlist sections
        HBox listsBox = new HBox(10);
        listsBox.setPrefHeight(350);

        // Left: Available musics
        VBox leftBox = new VBox(5);
        Label availLabel = new Label("Músicas Disponíveis:");
        availLabel.setStyle("-fx-text-fill: white;");
        availableList = new ListView<>();
        availableList.setPrefHeight(300);
        updateAvailableList();
        leftBox.getChildren().addAll(availLabel, availableList);

        // Center: Buttons
        VBox centerBox = new VBox(5);
        centerBox.setPrefWidth(100);
        Button addBtn = new Button("  >> Adicionar");
        addBtn.setStyle("-fx-background-color: #18f000ff; -fx-text-fill: black;");
        addBtn.setOnAction(e -> addToPlaylist());
        Button removeBtn = new Button("Remover <<");
        removeBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> removeFromPlaylist());
        centerBox.getChildren().addAll(addBtn, removeBtn);

        // Right: Playlist musics
        VBox rightBox = new VBox(5);
        Label playlistLabel = new Label("Playlist Atual:");
        playlistLabel.setStyle("-fx-text-fill: white;");
        playlistList = new ListView<>();
        playlistList.setPrefHeight(300);
        updatePlaylistList();
        rightBox.getChildren().addAll(playlistLabel, playlistList);

        listsBox.getChildren().addAll(leftBox, centerBox, rightBox);

        // Action buttons
        HBox actionBox = new HBox(10);
        Button playBtn = new Button("▶ Tocar Playlist");
        playBtn.setStyle("-fx-background-color: #18f000ff; -fx-text-fill: black;");
        playBtn.setOnAction(e -> playCurrentPlaylist());
        Button saveBtn = new Button("💾 Salvar");
        saveBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");
        saveBtn.setOnAction(e -> savePlaylist());
        Button deleteBtn = new Button("🗑 Deletar Salva");
        deleteBtn.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deletePlaylist(savedPlaylists.getValue()));
        Button closeBtn = new Button("Fechar");
        closeBtn.setStyle("-fx-background-color: #666666; -fx-text-fill: white;");
        closeBtn.setOnAction(e -> stage.close());
        actionBox.getChildren().addAll(playBtn, saveBtn, deleteBtn, closeBtn);

        root.getChildren().addAll(nameBox, loadBox, listsBox, actionBox);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
    }

    private void updateAvailableList() {
        ArrayList<String> names = new ArrayList<>();
        for (Audio a : availableAudios) {
            names.add(a.getName());
        }
        availableList.setItems(FXCollections.observableArrayList(names));
    }

    private void updatePlaylistList() {
        ArrayList<String> names = new ArrayList<>();
        for (Audio a : playlistAudios) {
            names.add(a.getName());
        }
        playlistList.setItems(FXCollections.observableArrayList(names));
    }

    private void addToPlaylist() {
        int idx = availableList.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < availableAudios.size()) {
            playlistAudios.add(availableAudios.get(idx));
            updatePlaylistList();
        }
    }

    private void removeFromPlaylist() {
        int idx = playlistList.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < playlistAudios.size()) {
            playlistAudios.remove(idx);
            updatePlaylistList();
        }
    }

    private void playCurrentPlaylist() {
        if (playlistAudios.isEmpty()) {
            showAlert("Erro", "Playlist vazia!", Alert.AlertType.WARNING);
            return;
        }
        Playlists pl = new Playlists("Playlist Temporária");
        for (Audio a : playlistAudios) {
            pl.addMusica(a);
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
            for (Audio a : playlistAudios) {
                pl.addMusica(a);
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
        if (confirm.showAndWait().isPresent() && confirm.showAndWait().get() == ButtonType.OK) {
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
}
