package brazileletrohits.com.br;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

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

    // Save playlist to XML file (serializes paths and names)
    public void saveToXml(File xmlFile) throws Exception {
        PlaylistDTO dto = new PlaylistDTO(this.nome);
        List<AudioDTO> entries = new ArrayList<>();
        for (Audio a : this.musicas) {
            if (a != null && a.getFile() != null) {
                entries.add(new AudioDTO(a.getName(), a.getFile().getPath()));
            }
        }
        dto.setAudios(entries);
        JAXBContext ctx = JAXBContext.newInstance(PlaylistDTO.class, AudioDTO.class);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(dto, xmlFile);
    }

    // Load playlist from XML file, converting DTOs into Audio objects.
    public static Playlists loadFromXml(File xmlFile) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(PlaylistDTO.class, AudioDTO.class);
        Unmarshaller u = ctx.createUnmarshaller();
        PlaylistDTO dto = (PlaylistDTO) u.unmarshal(xmlFile);
        Playlists p = new Playlists(dto.getName());
        if (dto.getAudios() != null) {
            for (AudioDTO adto : dto.getAudios()) {
                try {
                    File f = new File(adto.getPath());
                    if (f.exists()) {
                        Audio audio = new Audio(f);
                        p.addMusica(audio);
                    }
                } catch (Exception ex) {
                    // ignore bad entries
                }
            }
        }
        return p;
    }
}
