module brazileletrohits.com.br {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.desktop;
    requires jakarta.xml.bind;

    opens brazileletrohits.com.br to javafx.fxml, jakarta.xml.bind;
    exports brazileletrohits.com.br;
}