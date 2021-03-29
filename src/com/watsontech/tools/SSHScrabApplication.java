package com.watsontech.tools;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Watson on 2021/2/22.
 */
public class SSHScrabApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final SwingNode swingNode = new SwingNode();

        createSwingContent(swingNode);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        primaryStage.setTitle("SSH Crab 远程发蟹");
        try {
            primaryStage.getIcons().add(new Image(new FileInputStream(SSHCrab.class.getClassLoader().getResource("sshcrab.png").getFile())));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(pane, 350, 450));
        primaryStage.show();
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(new SSHCrab().getMainPanel());
        });
    }
}
