package brazileletrohits.com.br;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static final String PLAYLISTS_DIR = "playlists";

    static {
        File dir = new File(PLAYLISTS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // Essa classe gerencia a persistencia dos arquivos de playlist

    public static void savePlaylist(String playlistName, Playlists playlist) throws Exception {
        File dir = new File(PLAYLISTS_DIR);
        if (!dir.exists()) dir.mkdir();
        File file = new File(dir, sanitizeName(playlistName) + ".xml");
        playlist.saveToXml(file);
    }

    public static Playlists loadPlaylist(String playlistName) throws Exception {
        File dir = new File(PLAYLISTS_DIR);
        File file = new File(dir, sanitizeName(playlistName) + ".xml");
        if (!file.exists()) throw new Exception("Playlist não encontrada: " + playlistName);
        return Playlists.loadFromXml(file);
    }

    public static List<String> listPlaylists() {
        List<String> names = new ArrayList<>();
        File dir = new File(PLAYLISTS_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, n) -> n.endsWith(".xml"));
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    names.add(name.substring(0, name.length() - 4));
                }
            }
        }
        return names;
    }

    public static boolean deletePlaylist(String playlistName) {
        File dir = new File(PLAYLISTS_DIR);
        File file = new File(dir, sanitizeName(playlistName) + ".xml");
        return file.delete();
    }

    private static String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
