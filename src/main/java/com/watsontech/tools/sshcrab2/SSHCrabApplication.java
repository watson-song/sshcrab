package com.watsontech.tools.sshcrab2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SSHCrabApplication extends Application {

    public enum OS {Mac, Windows, Linux}
    static java.awt.Image statusBarIconImage, logoIconImage;

    public static OS currentOs = OS.Mac;
    public static final String userHomeDir;
    public static final String userDir;
    public static final String userDefaultConfigDir;

    public static Stage primaryStage;

    //设置mac系统dock图标
    static {
        // 加载一个图片用于托盘图标的显示
        logoIconImage = loadIconImage();

        //获得操作系统
        String OsName = System.getProperty("os.name");
        //是mac 就设置dock图标
        if (OsName.contains("Mac")) {
            currentOs = OS.Mac;

            if(logoIconImage!=null) {
                UIExtensionApple.setDockIconImage(logoIconImage);
            }
        }else if (OsName.contains("Windows")) {
            currentOs = OS.Windows;
        }else {
            currentOs = OS.Linux;
        }

        userDir = System.getProperty("user.dir");
        userHomeDir = System.getProperty("user.home");

        //如果当前用户根目录没有.sshcrab目录，则新建一个用于保存配置
        userDefaultConfigDir = SSHCrabApplication.userHomeDir + File.separatorChar + PropertiesHelper.userDefaultConfigDirName;
        if (!new File(userDefaultConfigDir).exists()) {
            new File(userDefaultConfigDir).mkdirs();
        }
    }

    static {
        Platform.setImplicitExit(false);//隐式退出开关，设置关闭所有窗口后程序仍不退出
    }

    private static java.awt.Image loadIconImage() {
        if(logoIconImage==null) {
            // 加载一个图片用于托盘图标的显示
            logoIconImage = Toolkit.getDefaultToolkit().getImage(SSHCrabApplication.class.getClassLoader().getResource("sshcrab.png"));
            statusBarIconImage = Toolkit.getDefaultToolkit().getImage(SSHCrabApplication.class.getClassLoader().getResource("logo.png"));
        }

        return logoIconImage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(SSHCrabApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 345, 450);
        primaryStage.setTitle("SSH Crab - 远程发蟹V2.0");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/sshcrab128.png")));

        // 设置窗口关闭事件
        primaryStage.setOnCloseRequest(event -> {
            //如果未启动应用，则立即退出程序
            if (!((SSHCrabController)fxmlLoader.getController()).isSSHRunning()) System.exit(0);
        });

        primaryStage.show();
    }

    //打开窗口
    public static void showWindow() {
        if (primaryStage != null) {
            Platform.runLater(() -> {
                if (primaryStage.isIconified()) {
                    primaryStage.setIconified(false);
                }
                if (!primaryStage.isShowing()) {
                    primaryStage.show();
                }
                primaryStage.toFront();
            });
        }
        System.out.println("打开窗口");
    }

    //关闭窗口
    public static void hideWindow() {
        if (primaryStage != null) {
            Platform.runLater(() -> {
                primaryStage.hide();
            });
        }
        System.out.println("关闭窗口");
    }

    public static void main(String[] args) {
        launch();
    }
}