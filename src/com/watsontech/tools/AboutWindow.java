package com.watsontech.tools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.SwingConstants.*;

/**
 * Created by Watson on 2019/12/10.
 */
public class AboutWindow extends JDialog {
    private JPanel panelMessage, panelButtons;

    private Image loadLogoImage() {
        return Toolkit.getDefaultToolkit().getImage(SSHCrab.class.getClassLoader().getResource("sshcrab.png"));
    }

    public AboutWindow(final MainPanel panel) {
        this.panelMessage = new JPanel();
        this.panelMessage.setBorder(new EmptyBorder(30, 50, 10, 50));

        this.panelButtons = new JPanel();
        this.panelButtons.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                AboutWindow.this.dispose();
            }
        });

        this.setTitle("About SSH Crab");
        this.setSize(400,400);
        this.setLayout(new BorderLayout(0, 0));

        this.setLocationRelativeTo(null);
        BorderLayout headPanelLayout = new BorderLayout();
        headPanelLayout.setVgap(20);
        this.panelMessage.setLayout(headPanelLayout);
        this.panelMessage.add(new JLabel(new ImageIcon(loadLogoImage())), BorderLayout.CENTER);

        JLabel nameLabel = new JLabel("<html><font face=\"宋体\";style=font:25pt>SSHCrab 远程发蟹</font>", CENTER);
        this.panelMessage.add(nameLabel, BorderLayout.SOUTH);

        this.add(panelMessage, BorderLayout.NORTH);
        this.add(panel, CENTER);
        this.add(panelButtons, BorderLayout.SOUTH);

        this.setResizable(false);
        this.pack();

        this.setVisible(true);
    }

    public static class MainPanel extends JPanel {

        private JLabel labelVersion, labelAuthor, labelMail, labelGithub;

        public MainPanel() {
            this.labelVersion = new JLabel("<html><font face=\"宋体\";style=font:12pt>Version：V1.0</font></html>", CENTER);
            this.labelAuthor = new JLabel("<html><font face=\"宋体\";style=font:12pt>Author：Watson Song</font></html>", CENTER);
            this.labelMail = new JLabel("<html><font face=\"宋体\"; style=font:12pt>Email：watson.song@gmail.com</font></html>", CENTER);
            this.labelGithub = new JLabel("<html><a href='https://github.com/watson-song/sshcrab'>https://github.com/watson-song/sshcrab</a></html>", CENTER);

            this.setLayout(new GridLayout(4, 1));  //网格式布局

            this.add(this.labelVersion);
            this.add(this.labelAuthor);
            this.add(this.labelMail);
            this.add(this.labelGithub);
        }
    }
}
