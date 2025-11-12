import java.io.File;
import java.io.FilenameFilter;

public class Main {
    public static void main(String[] args) {

       File diretorio = new File("/home/sou_eu/Downloads/Audios");      // Cria um type file q é um diretorio aonde deve estar todas as musicas 
                                                                        // deve ser mudado para q o usuario escolha qual é esse diretorio a partir da interface grafica
       FilenameFilter wavFilter = (dir, name) -> name.toLowerCase().endsWith(".wav");
       File[] listaArquivos = diretorio.listFiles(wavFilter);           // Retorna uma lista com todos os arquivos dentro do diretorio te terminam com .wav
       Playlists allAudios = new Playlists("Musicas");            // Cria uma playlist com todas as musicas do diretorio escolhido pelo usuario
       MenuPrincipal controleDeFaixas = new MenuPrincipal();           // Cria um Menu principal para o controle das musicas que estão tocando

       for (File f : listaArquivos) {                                  // Adiciona todos os arquivos no diretorio para a lista de musicas da playlist allAudios
           System.out.println(f.getName());
           Audio musica = new Audio(f);
           allAudios.addMusica(musica);
       }
       controleDeFaixas.playPlaylist(allAudios, 0);              // Teste da função playPlaylist, so toca a primeira musica pois a função MenuPrincipal.menuOpções()
                                                                       // deve funcionar a partir da interface grafica, então ainda não foi implementada
    }
}