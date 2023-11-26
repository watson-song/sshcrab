module com.watsontech.tools.sshcrap2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;
    requires jsch;
    requires java.desktop;

    opens com.watsontech.tools.sshcrab2 to javafx.fxml;
    exports com.watsontech.tools.sshcrab2;
}