module com.watsontech.tools.sshcrab2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.kordamp.bootstrapfx.core;
    requires org.slf4j;
    requires java.desktop;
    requires java.security.jgss;

    opens com.watsontech.tools.sshcrab2 to javafx.fxml;
    exports com.watsontech.tools.sshcrab2;
}