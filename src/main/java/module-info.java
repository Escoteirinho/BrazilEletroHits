module brazileletrohits.com.br {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.desktop;

    opens brazileletrohits.com.br to javafx.fxml;
    exports brazileletrohits.com.br;
}
