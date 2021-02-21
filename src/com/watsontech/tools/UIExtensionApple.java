package com.watsontech.tools;

import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Watson on 2021/2/21.
 */
public class UIExtensionApple {
    static com.apple.eawt.Application macApplication;

    public void loadDockImage(JFrame frame, String fileName) {
        // Retrieve the Image object from the locally stored image file
        // "frame" is the name of my JFrame variable, and "filename" is the name of the image file
        Image image = Toolkit.getDefaultToolkit().getImage(frame.getClass().getResource(fileName));

        try {
            // Replace: import com.apple.eawt.Application
            String className = "com.apple.eawt.Application";
            Class<?> cls = Class.forName(className);

            // Replace: Application application = Application.getApplication();
            Object application = cls.newInstance().getClass().getMethod("getApplication")
                    .invoke(null);

            // Replace: application.setDockIconImage(image);
            application.getClass().getMethod("setDockIconImage", java.awt.Image.class)
                    .invoke(application, image);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException
                | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void setDockIconImage(Image logoIconImage) {
        //指定mac 的dock图标
        Application macApplication = com.apple.eawt.Application.getApplication();
        macApplication.setDockIconImage(logoIconImage);
        macApplication.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
            @Override
            public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent preferencesEvent) {
                System.out.println("handle preferences "+ preferencesEvent.getSource());
            }
        });

        macApplication.setQuitHandler(new com.apple.eawt.QuitHandler() {
            @Override
            public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent quitEvent, com.apple.eawt.QuitResponse quitResponse) {
                System.out.println("app is quited" + quitEvent.toString() + quitResponse.toString());
            }
        });

        macApplication.setAboutHandler(new com.apple.eawt.AboutHandler() {
            @Override
            public void handleAbout(com.apple.eawt.AppEvent.AboutEvent aboutEvent) {
                System.out.println("about has been clicked" + aboutEvent.toString());

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        AboutWindow.MainPanel panel = new AboutWindow.MainPanel();
                        AboutWindow win = new AboutWindow(panel);
                    }
                });

            }
        });

        macApplication.setQuitStrategy(com.apple.eawt.QuitStrategy.CLOSE_ALL_WINDOWS);

        UIExtensionApple.macApplication = macApplication;
    }

    public static void updateDockerWord(String word) {
        if(macApplication!=null) {
            macApplication.setDockIconBadge(word);
        }
    }

    public static void requestForeground() {
        if(macApplication!=null) {
            macApplication.requestForeground(true);
            macApplication.requestUserAttention(true);
        }
    }
}
