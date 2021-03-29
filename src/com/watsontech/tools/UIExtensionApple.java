package com.watsontech.tools;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Watson on 2021/2/21.
 */
public class UIExtensionApple {
//    static com.apple.eawt.Application macApplication;
    static Object macApplication;
    static {
        // Replace: import com.apple.eawt.Application
        String className = "com.apple.eawt.Application";
        Class<?> cls = null;
        try {
            cls = Class.forName(className);

            // Replace: Application application = Application.getApplication();
            macApplication = cls.newInstance().getClass().getMethod("getApplication").invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void setDockIconImage(Image image) {
        if(UIExtensionApple.macApplication==null) return;

        // Retrieve the Image object from the locally stored image file
        // "frame" is the name of my JFrame variable, and "filename" is the name of the image file
//        Image image = Toolkit.getDefaultToolkit().getImage(frame.getClass().getResource(fileName));

        // Replace: application.setDockIconImage(image);
        try {
            UIExtensionApple.macApplication.getClass().getMethod("setDockIconImage", Image.class)
                    .invoke(UIExtensionApple.macApplication, image);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void updateDockerWord(String word) {
        if(macApplication!=null) {
            try {
                macApplication.getClass().getMethod("setDockIconBadge", String.class)
                        .invoke(macApplication, word);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

//            macApplication.setDockIconBadge(word);
        }
    }

    public static void requestForeground() {
        if(macApplication!=null) {
            try {
                macApplication.getClass().getMethod("requestForeground", boolean.class)
                        .invoke(macApplication, true);
                macApplication.getClass().getMethod("requestUserAttention", boolean.class)
                        .invoke(macApplication, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

//            macApplication.requestForeground(true);
//            macApplication.requestUserAttention(true);
        }
    }
}
