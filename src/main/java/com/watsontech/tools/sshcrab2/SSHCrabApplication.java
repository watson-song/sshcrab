package com.watsontech.tools.sshcrab2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SSHCrabApplication extends Application {

    public enum OS {Mac, Windows, Linux}

    static OS currentOs = OS.Mac;
    public static final String userHomeDir;
    public static final String userDir;
    public static final String userDefaultConfigDir;

    public static Stage primaryStage;

    //设置mac系统dock图标
    static {
        //获得操作系统
        String OsName = System.getProperty("os.name");
        //是mac 就设置dock图标
        if (OsName.contains("Mac")) {
            currentOs = OS.Mac;

//            if(logoIconImage!=null) {
//                UIExtensionApple.setDockIconImage(logoIconImage);
//            }
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
            hideWindow();
            event.consume();
        });

        primaryStage.show();

        // 创建系统托盘图标
        createTrayIcon();
    }

    //创建图标
    private void createTrayIcon() {
        if (SystemTray.isSupported()) {
            // 获取系统托盘
            SystemTray tray = SystemTray.getSystemTray();
            // 创建弹出菜单
            PopupMenu popupMenu = new PopupMenu();
            // 创建系统托盘图标
            Image image = new Image(this.getClass().getResourceAsStream("/logo.png"));
            TrayIcon trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(image, null), "SSH Crab 远程发蟹 v2.0", popupMenu);
            trayIcon.setImageAutoSize(true);
            // 创建打开菜单项
            MenuItem openMenuItem = new MenuItem("打开(Show)");

            openMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Open测试");
                    Platform.runLater(() -> {
                        System.out.println("Open测试2");
                        showWindow();
                    });
                }
            });

            // 创建退出菜单项
            MenuItem exitMenuItem = new MenuItem(new String("退出(Quit)".getBytes(StandardCharsets.UTF_8)));
            exitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exit测试");
                    Platform.exit();
                    tray.remove(trayIcon);
                }
            });

            // 将菜单项添加到弹出菜单
            popupMenu.add(openMenuItem);
            popupMenu.add(exitMenuItem);
            // 将弹出菜单设置到系统托盘图标
            trayIcon.setPopupMenu(popupMenu);
            // 将系统托盘图标添加到系统托盘
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("Failed to add tray icon.");
            }

            // 设置托盘图标双击事件
            trayIcon.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            showWindow();
                        }
                    });
                }
            });
        } else {
            System.out.println("System tray is not supported.");
        }
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