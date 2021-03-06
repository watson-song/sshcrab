package com.watsontech.tools;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static com.watsontech.tools.SSHCrab.OS.Mac;
import static javax.swing.SwingConstants.RIGHT;
import static javax.swing.SwingConstants.TRAILING;

/**
 * Created by Watson on 2018/12/19.
 */
public class SSHCrab extends Frame {
    static Logger logger = LoggerFactory.getLogger(SSHCrab.class);
    protected JTextPane labelMessage;
    private JPanel panelMessage, panelButtons;
    private MainPanel mainPanel;
    private JButton buttonConnect, buttonStop;
    SSHConnection conexionssh;
    SSHConnectionParams sshConnectionParams;

    public JComponent getMainPanel() {
        return mainPanel;
    }

    public enum OS {Mac, Windows, Linux}

    static OS currentOs = Mac;

    static Image statusBarIconImage, logoIconImage;

    //设置mac系统dock图标
    static {
        // 加载一个图片用于托盘图标的显示
        logoIconImage = loadIconImage();

        //获得操作系统
        String OsName = System.getProperty("os.name");
        //是mac 就设置dock图标
        if (OsName.contains("Mac")) {
            currentOs = Mac;

            if(logoIconImage!=null) {
                UIExtensionApple.setDockIconImage(logoIconImage);
            }
        }else if (OsName.contains("Windows")) {
            currentOs = OS.Windows;
        }else {
            currentOs = OS.Linux;
        }
    }

    private static Image loadIconImage() {
        if(logoIconImage==null) {
            // 加载一个图片用于托盘图标的显示
            logoIconImage = Toolkit.getDefaultToolkit().getImage(SSHCrab.class.getClassLoader().getResource("sshcrab.png"));
            statusBarIconImage = Toolkit.getDefaultToolkit().getImage(SSHCrab.class.getClassLoader().getResource("logo.png"));
        }

        return logoIconImage;
    }

    public SSHCrab() {
        this.buttonConnect = new JButton("立即启动");
        this.buttonStop = new JButton("立即停止");
        this.buttonStop.setVisible(false);

        this.panelMessage = new JPanel();
        this.panelMessage.setBorder(new EmptyBorder(0, 0, 0, 0));

        JScrollPane labelMessageScrollPanel = new JScrollPane();
        labelMessageScrollPanel.setPreferredSize(new Dimension(350, 100));
        labelMessageScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.labelMessage = new JTextPane();
        this.labelMessage.setText("SSHCrab V1.0 已启动完成！");
        this.labelMessage.setEditable(false);
        this.labelMessage.setAutoscrolls(true);

        labelMessageScrollPanel.setViewportView(this.labelMessage);
        this.panelMessage.add(labelMessageScrollPanel);

        this.panelButtons = new JPanel();
        this.panelButtons.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.panelButtons.add(this.buttonConnect);
        this.panelButtons.add(this.buttonStop);
        this.mainPanel = new MainPanel();
        this.mainPanel.setSize(300, 300);

        this.buttonConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateMessageLabel(String.format("SSH转发启动中..."), Color.GREEN);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            final SSHConnectionParams connectionParams = mainPanel.getParams();
                            if (connectionParams!=null) {
                                conexionssh = new SSHConnection(connectionParams);
                                conexionssh.startSSH(6, new SSHConnection.ConnectionCallback() {
                                    @Override
                                    public void onConnected(Session session) {
                                        SSHCrab.this.buttonConnect.setVisible(false);
                                        SSHCrab.this.buttonStop.setVisible(true);

                                        //更新配置到文件
                                        saveConfigFile(connectionParams);
                                        if (currentOs== Mac) {
                                            UIExtensionApple.updateDockerWord("起");
                                        }

                                        try {
                                            updateMessageLabel(String.format("SSH端口转发已启动, %s", session.getPortForwardingL()), Color.GREEN);
                                        } catch (JSchException e) {
                                            e.printStackTrace();
                                            updateMessageLabel(String.format("SSH端口转发已启动"), Color.GREEN);
                                        }

                                        SSHCrab.this.setVisible(false);
                                    }

                                    @Override
                                    public void onConnecteFailed(Session session) {
                                        SSHCrab.this.buttonConnect.setVisible(true);
                                        SSHCrab.this.buttonStop.setVisible(true);

                                        if (currentOs== Mac) {
                                            UIExtensionApple.updateDockerWord("停");
                                        }

                                        updateMessageLabel(String.format("SSH端口连接失败"), Color.YELLOW);
                                        SSHCrab.this.setVisible(true);
                                    }
                                });
                            }
                        } catch (JSchException e) {
                            e.printStackTrace();

                            if (currentOs== Mac) {
                                UIExtensionApple.updateDockerWord("失败");
                            }

                            updateMessageLabel(String.format("启动失败：%s", e.getMessage()), Color.red);
                            conexionssh.closeSSH();
                            SSHCrab.this.buttonConnect.setVisible(true);
                            SSHCrab.this.buttonStop.setVisible(false);
                        }
                    }
                }.start();

            }
        });

        this.buttonStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                conexionssh.closeSSH();
                SSHCrab.this.buttonConnect.setVisible(true);
                SSHCrab.this.buttonStop.setVisible(false);
                updateMessageLabel(String.format("SSH端口转发停止"), Color.ORANGE);

                if (currentOs== Mac) {
                    UIExtensionApple.updateDockerWord("停");
                }
            }
        });

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                if(conexionssh!=null&&conexionssh.isConnected()) {
                    SSHCrab.this.setVisible(false);
                }else {
                    System.exit(0);
                }
            }
        });

        this.setName("SSHCrab 远程发蟹");
        this.setTitle("SSH Crab - 远程发蟹V1.0");
        this.setLayout(new BorderLayout(0, 0));

        this.setLocationRelativeTo(null);
        this.add(panelMessage, BorderLayout.NORTH);
        this.add(mainPanel);
        this.add(panelButtons, BorderLayout.SOUTH);
        this.setResizable(false);
        this.pack();

        /*
         * 添加系统托盘
         */
        if (SystemTray.isSupported()) {
            // 获取当前平台的系统托盘
            SystemTray tray = SystemTray.getSystemTray();

            // 创建点击图标时的弹出菜单
            PopupMenu popupMenu = new PopupMenu();

            MenuItem openItem = new MenuItem("打开(Show)");
            MenuItem exitItem = new MenuItem("退出(Quit)");

            openItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 点击打开菜单时显示窗口
                    if (!SSHCrab.this.isShowing()) {
                        SSHCrab.this.setVisible(true);
                    }

                    SSHCrab.this.toFront();

                    if (currentOs== Mac) {
                        UIExtensionApple.requestForeground();
                    }
                }
            });
            exitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 点击退出菜单时退出程序
                    System.exit(0);
                }
            });

            popupMenu.add(openItem);
            popupMenu.add(exitItem);

            loadIconImage();

            if(statusBarIconImage!=null) {
                // 创建一个托盘图标
                TrayIcon trayIcon = new TrayIcon(statusBarIconImage, "SSH Crab 远程发蟹 v1.0", popupMenu);
                //关键点，设置托盘图标的自适应属性，这样才能在系统显示托盘处正常显示出需要的图片。
                trayIcon.setImageAutoSize(true);

                // 添加托盘图标到系统托盘
                try {
                    tray.add(trayIcon);
                    trayIcon.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            trayIcon.getPopupMenu().show(null, 0,0);
                        }
                    });
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("当前系统不支持系统托盘");
        }

        this.setVisible(true);
    }

    private void updateMessageLabel(String message, Color color) {
        String currentMessage = this.labelMessage.getText();
        this.labelMessage.setForeground(color);
        this.labelMessage.setText(currentMessage+message+"\n");

        this.labelMessage.setSelectedTextColor(Color.red);
        this.labelMessage.setSelectionStart(currentMessage.length());
        this.labelMessage.setSelectionStart(currentMessage.length()+message.length());
    }

    public class MainPanel extends JPanel {

        private JLabel labelConfigFileChoseLabel, labelSaveConfigFileLabel, labelSSHHost, labelForwardHost, labelLocalHost, labelSSHKeyPath, labelSSHHostsPath, labelSelectedConfigFile, labelUseSSHKey;
        private JButton configFileChooseButton;
        private JButton saveConfigFileButton;
        private JComboBox<SSHConnectionParams.AuthType> selectBoxUseSSHKey;
        private JTextField textFieldSSHHost;
        private JTextField textFieldSSHPort;
        private JTextField textFieldForwardHost;
        private JTextField textFieldForwardPort;
        private JTextField textFieldLocalHost;
        private JTextField textFieldLocalPort;

        private JTextField textFieldSSHKeyPath;
        private JTextField textFieldSSHKeyPhrase;
        private JTextField textFieldSSHHostUsername;
//        private JTextField textFieldSSHHostsPath;

        private JPanel panelConfigFileChooseHost;
        private JPanel panelSSHHost;
        private JPanel panelForwardHost;
        private JPanel panelLocalHost;
        private JPanel panelSSHKeyPath;
        private JPanel panelSSHHostsPath;
        private JPanel panelSaveConfigFile;
        private JPanel panelUseSSHKey;

        public MainPanel() {
            this.setBorder(new EmptyBorder(0, 0, 0, 0));

            this.labelConfigFileChoseLabel = new JLabel("选择配置文件", TRAILING);
            this.labelSelectedConfigFile = new JLabel("", TRAILING);
            this.configFileChooseButton = new JButton("选择文件");
            this.labelSaveConfigFileLabel = new JLabel("保存配置文件", TRAILING);
            this.saveConfigFileButton = new JButton("保存当前配置");

            this.configFileChooseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentDir = null;
                    String filename = "";
                    if (sshConnectionParams!=null) {
                        if (sshConnectionParams.getConfigFile()!=null) {
                            currentDir = sshConnectionParams.getConfigFile().getParent();
                            filename = sshConnectionParams.getConfigFile().getName();
                        }
                    }

                    JFileChooser jfc = new JFileChooser(currentDir);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
                    jfc.setFileFilter(new FileNameExtensionFilter("选择配置文件", "properties", "txt"));
                    jfc.showDialog(new JLabel("切换："+filename), "选择配置");

                    File file = jfc.getSelectedFile();

                    if (file!=null&&file.isFile()) {
                        loadConfigParams(file.getAbsolutePath());
                    }
                }
            });
            this.saveConfigFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveConfigFile(mainPanel.getParams());
                }
            });

            this.labelSSHHost = new JLabel("SSH主机/端口 ", RIGHT);

            this.labelForwardHost = new JLabel("转发源地址/端口 ", RIGHT);
            this.labelLocalHost = new JLabel("目标本地地址/端口 ", RIGHT);

//            this.labelSSHKeyPath = new JLabel("SSH公钥路径/密码 ", RIGHT);
//            this.labelSSHHostsPath = new JLabel("SSH hosts文件路径/主机账号 ", RIGHT);

            this.labelSSHKeyPath = new JLabel("主机账号/密码 ", RIGHT);
            this.labelSSHHostsPath = new JLabel("SSH私钥路径 ", RIGHT);
            this.labelUseSSHKey = new JLabel("登陆方式 ", RIGHT);

            this.textFieldSSHHost = new JTextField("SSH主机IP/域名", 9);
            this.textFieldSSHPort = new JTextField("22", 3);
            this.textFieldForwardHost = new JTextField("127.0.0.1", 9);
            this.textFieldForwardPort = new JTextField("443", 3);
            this.textFieldLocalHost = new JTextField("localhost",9);
            this.textFieldLocalPort = new JTextField("",3);

            this.textFieldSSHKeyPath = new JTextField("~/.ssh/id_rsa",13);
//            this.textFieldSSHHostsPath = new JTextField("~/.ssh/known_hosts",6);

            this.textFieldSSHHostUsername = new JTextField("root",4);
            this.textFieldSSHKeyPhrase = new JTextField("~密码~",8);
            this.selectBoxUseSSHKey = new JComboBox<>();
            this.selectBoxUseSSHKey.addItem(SSHConnectionParams.AuthType.password);
            this.selectBoxUseSSHKey.addItem(SSHConnectionParams.AuthType.key);
            this.selectBoxUseSSHKey.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    System.out.println("selected id:"+e.getID());
                    if(e.getStateChange()==ItemEvent.SELECTED) {
                        SSHConnectionParams.AuthType authType = (SSHConnectionParams.AuthType) e.getItem();
                        switch (authType) {
                            case key:
                                panelSSHHostsPath.setVisible(true);
                                labelSSHHostsPath.setVisible(true);
                                labelSSHKeyPath.setText("主机账号/SSH密钥密码");
                                break;
                            default:
                                panelSSHHostsPath.setVisible(false);
                                labelSSHHostsPath.setVisible(false);
                                labelSSHKeyPath.setText("主机账号/密码 ");
                                break;
                        }
                    }
                }
            });

            this.panelConfigFileChooseHost = new JPanel();
            this.panelSSHHost = new JPanel();
            this.panelForwardHost = new JPanel();
            this.panelLocalHost = new JPanel();
            this.panelSSHKeyPath = new JPanel();
            this.panelSSHHostsPath = new JPanel();
            this.panelSaveConfigFile = new JPanel();
            this.panelUseSSHKey = new JPanel();

            GridLayout layoutManager = new GridLayout(7, 2, 0, 0);
            this.setLayout(layoutManager);  //网格式布局

            this.panelConfigFileChooseHost.add(this.configFileChooseButton);

            this.panelSSHHost.add(this.textFieldSSHHost);
            this.panelSSHHost.add(this.textFieldSSHPort);

            this.panelForwardHost.add(this.textFieldForwardHost);
            this.panelForwardHost.add(this.textFieldForwardPort);

            this.panelLocalHost.add(this.textFieldLocalHost);
            this.panelLocalHost.add(this.textFieldLocalPort);

            this.panelSSHKeyPath.add(this.textFieldSSHHostUsername);
            this.panelSSHKeyPath.add(this.textFieldSSHKeyPhrase);

            this.panelSSHHostsPath.add(this.textFieldSSHKeyPath);
//            this.panelSSHHostsPath.add(this.textFieldSSHHostsPath);

            this.panelSaveConfigFile.add(this.labelSaveConfigFileLabel);
            this.panelSaveConfigFile.add(this.saveConfigFileButton);

            this.panelUseSSHKey.add(this.selectBoxUseSSHKey);

            this.add(this.labelConfigFileChoseLabel);
            this.add(this.panelConfigFileChooseHost);

            this.add(this.labelSSHHost);
            this.add(this.panelSSHHost);
            this.add(this.labelForwardHost);
            this.add(this.panelForwardHost);
            this.add(this.labelLocalHost);
            this.add(this.panelLocalHost);
            this.add(this.labelUseSSHKey);
            this.add(this.panelUseSSHKey);
            this.add(this.labelSSHKeyPath);
            this.add(this.panelSSHKeyPath);
            this.add(this.labelSSHHostsPath);
            this.add(this.panelSSHHostsPath);

//            this.add(this.labelSaveConfigFileLabel);
//            this.add(this.panelSaveConfigFile);

            this.loadConfigParams("config.properties");
        }

        private void loadConfigParams(String filepath) {
            if (filepath==null) {
                filepath = "config.properties";
            }

            final String configFilePath = filepath;
            new Thread(){
                @Override
                public void run() {
                    try {
                        sshConnectionParams = PropertiesHelper.readPropertyFile(logger, configFilePath);
                        if (sshConnectionParams!=null) {
                            if (sshConnectionParams.getConfigFile()!=null) {
                                configFileChooseButton.setText(sshConnectionParams.getConfigFile().getName());
                                updateMessageLabel(String.format("已加载配置文件配置：%s", sshConnectionParams.getConfigFile().getAbsolutePath()), Color.gray);
                            }else {
                                updateMessageLabel(String.format("已加载配置文件配置：%s", configFilePath), Color.gray);
                            }

                            textFieldSSHHost.setText(stringValue(sshConnectionParams.getSshRemoteHost()));
                            textFieldSSHPort.setText(stringValue(sshConnectionParams.getRemoteSSHPort()));
                            textFieldForwardHost.setText(stringValue(sshConnectionParams.getForwardFromRemoteHost()));
                            textFieldForwardPort.setText(stringValue(sshConnectionParams.getForwardFromRemotePort()));
                            textFieldLocalHost.setText(stringValue(sshConnectionParams.getForwardToLocalHost()));
                            textFieldLocalPort.setText(stringValue(sshConnectionParams.getForwardToLocalPort()));

                            textFieldSSHKeyPath.setText(stringValue(sshConnectionParams.getPrivateKeyPath()));
                            textFieldSSHKeyPhrase.setText(stringValue(sshConnectionParams.getPrivateKeyPhrase()));
//                            textFieldSSHHostsPath.setText(stringValue(sshConnectionParams.getKnowHostsPath()));
                            textFieldSSHHostUsername.setText(stringValue(sshConnectionParams.getSshUserName()));
                            selectBoxUseSSHKey.setSelectedItem(sshConnectionParams.getAuthType());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        updateMessageLabel(String.format("加载配置文件%s异常：%s", "config.properties", e.getMessage()), Color.RED);
                    }
                }
            }.start();
        }

        private SSHConnectionParams getParams() {
            SSHConnectionParams params = new SSHConnectionParams();
            params.setForwardFromRemoteHost(textFieldForwardHost.getText());
            if (!"".equals(textFieldForwardPort.getText())) {
                try {
                    params.setForwardFromRemotePort(Integer.parseInt(textFieldForwardPort.getText()));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }

            params.setForwardToLocalHost(textFieldLocalHost.getText());

            if (!"".equals(textFieldLocalPort.getText())) {
                try {
                    params.setForwardToLocalPort(Integer.parseInt(textFieldLocalPort.getText()));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    params.setForwardToLocalPort(params.getForwardFromRemotePort());
                }
            }else {
                params.setForwardToLocalPort(params.getForwardFromRemotePort());
            }
            params.setSshRemoteHost(textFieldSSHHost.getText());
            if (!"".equals(textFieldSSHPort.getText())) {
                try {
                    params.setRemoteSSHPort(Integer.parseInt(textFieldSSHPort.getText()));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }

            SSHConnectionParams.AuthType authType = (SSHConnectionParams.AuthType) selectBoxUseSSHKey.getSelectedItem();
            params.setAuthType(authType);

            if(!isEmpty(textFieldSSHKeyPath.getText())) {
                params.setPrivateKeyPath(textFieldSSHKeyPath.getText());
            }
//            if(!isEmpty(textFieldSSHHostsPath.getText())) {
//                params.setKnowHostsPath(textFieldSSHHostsPath.getText());
//            }
            if(!isEmpty(textFieldSSHKeyPhrase.getText())) {
                params.setPrivateKeyPhrase(textFieldSSHKeyPhrase.getText());
            }
            if(!isEmpty(textFieldSSHHostUsername.getText())) {
                params.setSshUserName(textFieldSSHHostUsername.getText());
            }

            if(isEmpty(params.getSshRemoteHost())) {
                JOptionPane.showMessageDialog(null, "请输入SSH主机地址", "SSH主机地址【出错啦】", JOptionPane.ERROR_MESSAGE);
                updateMessageLabel("启动失败：SSH主机地址【出错啦】", Color.gray);
                return null;
            }

            if(!isValidHostName(params.getSshRemoteHost())) {
                JOptionPane.showMessageDialog(null, "不合法的SSH主机地址", "SSH主机地址【出错啦】",  JOptionPane.ERROR_MESSAGE);
                updateMessageLabel("启动失败：SSH主机地址【出错啦】", Color.gray);
                return null;
            }
            if(isEmpty(params.getForwardFromRemoteHost())) {
                JOptionPane.showMessageDialog(null, "请输入目标远程转发源地址", "远程转发源地址【出错啦】", JOptionPane.ERROR_MESSAGE);
                updateMessageLabel("启动失败：远程转发源地址【出错啦】", Color.gray);
                return null;
            }
            if(!isValidHostName(params.getForwardFromRemoteHost())) {
                JOptionPane.showMessageDialog(null, "不合法的远程转发源地址", "远程转发源地址【出错啦】", JOptionPane.ERROR_MESSAGE);
                updateMessageLabel("启动失败：远程转发源地址【出错啦】", Color.gray);
                return null;
            }

            if(isEmpty(params.getPrivateKeyPhrase())&&isEmpty(params.getPrivateKeyPath())) {
                JOptionPane.showMessageDialog(null, "请输入SSH私钥地址或用户名密码", "授权失败【出错啦】", JOptionPane.ERROR_MESSAGE);
                updateMessageLabel("启动失败：身份认证失败【出错啦】", Color.gray);
                return null;
            }

            return params;
        }

        private boolean isEmpty(Object value) {
            if (value==null) return true;
            return "".equals(value.toString().trim());
        }

        private boolean isValidHostName(String value) {
            return value!=null&&("localhost".equalsIgnoreCase(value)||(Pattern.compile(ipPattern).matcher(value).find()||Pattern.compile(hostPattern).matcher(value).find()));
        }

        private String stringValue(Object value) {
            if (isEmpty(value)) return "";
            return value.toString();
        }
    }

    private void saveConfigFile(SSHConnectionParams connectionParams) {
        String savedMessage = PropertiesHelper.writePropertyFile(connectionParams);
        if (savedMessage!=null) {
            updateMessageLabel(savedMessage, Color.gray);
        }
    }

    static final String ipPattern = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
    static final String hostPattern = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SSHCrab win = new SSHCrab();
                win.setName("SSH Crab");
            }
        });
    }

}
