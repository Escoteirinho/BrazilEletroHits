import java.util.ArrayList;

public class Playlists {
    private String nome;
    private double totalTime;
    private ArrayList<Audio> musicas;

    public Playlists(String nome) {
        this.nome = nome;
        this.totalTime = 0;
        this.musicas = new ArrayList<>();
    }

    private void calcularTotalTimeSeconds(Audio musica) {
        totalTime += musica.getDuracaoSegundos();
        return;
    }

    public double getTotalTimeSeconds() {
        return totalTime;
    }

    public void addMusica(Audio musica) {
        musicas.add(musica);
        calcularTotalTimeSeconds(musica);
        return;
    }

    public void removeMusica(int index) {
        musicas.remove(index);
        return;
    }

    public ArrayList<Audio> getListaMusicas() {
        return musicas;
    }

}
