package brazileletrohits.com.br;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "audio")
public class AudioDTO {
    private String name;
    private String path;

    public AudioDTO() {
    }

    public AudioDTO(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
