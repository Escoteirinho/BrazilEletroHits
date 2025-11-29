package brazileletrohits.com.br;
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

    public String getName() {
        /* Retorna uma string com o nome do arquivo de audio */
        return nome;
    }

    private void calcularTotalTimeSeconds(Audio musica) {
        totalTime += musica.getDuracaoSegundos();
    }

    public double getTotalTimeSeconds() {
        return totalTime;
    }

    public void addMusica(Audio musica) {
        musicas.add(musica);
        calcularTotalTimeSeconds(musica);
    }

    public void removeMusica(int index) {
        musicas.remove(index);
    }

    public ArrayList<Audio> getListaMusicas() {
        return musicas;
    }
}
