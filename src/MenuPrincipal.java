import java.util.ArrayList;
import javax.sound.sampled.*;


public class MenuPrincipal {
    
    private ArrayList<Audio> listaDeReproducao;
    private int index;
    private Clip clip;

    public MenuPrincipal() {
        this.listaDeReproducao = new ArrayList<>();
        this.index = 0;
        this.clip = AudioSystem.getClip();
    }

    private void menuOpcoes() {
        /* Ferifica se existe algum input na tela enquanto o programa esta aberto, 
        por enquanto so tem os casos de controlar a musica q esta rolando no momento */

        while(true) {
            switch(/* Input da tela */) {
                case "p":
                    if (clip.isRunning()) {
                        clip.stop();
                    } else {
                        // se chegou ao fim, reinicia antes de tocar
                        if (clip.getFramePosition() >= clip.getFrameLength()) {
                            clip.setFramePosition(0);
                        }
                        clip.start();
                    }
                    break;
                case "restart":
                    clip.stop();
                    clip.setFramePosition(0);
                    clip.start();
                    break;
                case "stop":
                    clip.stop();
                    clip.setFramePosition(0);
                    break;
            }
        }
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.close();
        return;
    }

    public void playPlaylist(Playlists playlist, int index) {
        /* Toca uma playlist inteira a partir de um index que deve 
        corresponder a uma musica escolhida dentro da lista da playlist */  
        
        listaDeReproducao = new ArrayList<>(playlist.getListaMusicas());
        int tamanhoPlaylist = playlist.getListaMusicas().size();
        int indexAtual;
        for (indexAtual = index; indexAtual < tamanhoPlaylist; indexAtual++ ) {
            clip.open(listaDeReproducao.get(indexAtual));
            menuOpcoes();
        }
        return;
    }

}
