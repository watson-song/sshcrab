package com.watsontech.tools;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Watson on 2021/2/22.
 */
public class SSHScrabApplication extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Title");

        final Parameters params = getParameters();
        final java.util.List<String> parameters = params.getRaw();
        final String imageUrl = !parameters.isEmpty() ? parameters.get(0) : "";

        primaryStage.show();
    }

}
