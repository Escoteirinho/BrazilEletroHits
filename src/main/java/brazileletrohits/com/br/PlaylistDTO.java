package brazileletrohits.com.br;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "playlist")
public class PlaylistDTO {
    private String name;
    private List<AudioDTO> audios = new ArrayList<>();

    public PlaylistDTO() {}

    public PlaylistDTO(String name) {
        this.name = name;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "audio")
    public List<AudioDTO> getAudios() {
        return audios;
    }

    public void setAudios(List<AudioDTO> audios) {
        this.audios = audios;
    }
}
