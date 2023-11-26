package com.watsontech.tools.sshcrab2;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Watson on 2018/12/19.
 */
public class SSHCrabController {
    static Logger logger = LoggerFactory.getLogger(SSHCrabController.class);

    @FXML
    private TextFlow labelMessage;
    @FXML
    private AnchorPane labelMessageScrollPanel;
    @FXML
    private javafx.scene.control.Button buttonConnect,buttonStop,configFileChooseButton,saveConfigFileButton;

    @FXML
    private Label labelConfigFileChoseLabel, labelSaveConfigFileLabel, labelSSHHost, labelForwardHost, labelLocalHost, labelSSHKeyPath, labelSSHHostsPath, labelSelectedConfigFile, labelUseSSHKey;
//    private JComboBox<SSHConnectionParams.AuthType> selectBoxUseSSHKey;
    @FXML
    private TextField textFieldSSHHost,textFieldSSHPort,textFieldForwardHost,textFieldForwardPort,textFieldLocalHost,textFieldLocalPort,textFieldSSHKeyPath,textFieldSSHKeyPhrase,textFieldSSHHostUsername,textFieldSSHHostsPath;

    static {
        Platform.setImplicitExit(false);//隐式退出开关，设置关闭所有窗口后程序仍不退出
    }

    @FXML
    protected void onConnectButtonClick(Event event) {
        updateMessageLabel(String.format("SSH转发启动中..."));
        try {
            final SSHConnectionParams connectionParams = getParams();
            if (connectionParams!=null) {
                conexionssh = new SSHConnection(connectionParams);
                conexionssh.startSSH(6, new SSHConnection.ConnectionCallback() {
                    @Override
                    public void onConnected(Session session) {
                        SSHCrabController.this.buttonConnect.setVisible(false);
                        SSHCrabController.this.buttonStop.setVisible(true);

                        //更新配置到文件
                        saveConfigFile(connectionParams);

                        try {
                            updateMessageLabel(String.format("SSH端口转发已启动, %s", session.getPortForwardingL()));
                        } catch (JSchException e) {
                            e.printStackTrace();
                            updateMessageLabel(String.format("SSH端口转发已启动"));
                        }

                        SSHCrabApplication.hideWindow();
                    }

                    @Override
                    public void onConnecteFailed(Session session) {
                        SSHCrabController.this.buttonConnect.setVisible(true);
                        SSHCrabController.this.buttonStop.setVisible(true);

                        updateMessageLabel(String.format("SSH端口连接失败"));
                    }
                });
            }
        } catch (JSchException e) {
            handleConnectException(e);
        } catch (IllegalArgumentException e) {
            handleConnectException(e);
        }
    }

    private void handleConnectException(Exception e) {
        e.printStackTrace();

        updateMessageLabel(String.format("启动失败：%s", e.getMessage()));
        if (conexionssh!=null&&conexionssh.isConnected()) conexionssh.closeSSH();
        SSHCrabController.this.buttonConnect.setVisible(true);
        SSHCrabController.this.buttonStop.setVisible(false);

        SSHCrabApplication.showWindow();
    }

    @FXML
    protected void onStopButtonClick() {
        conexionssh.closeSSH();
        SSHCrabController.this.buttonConnect.setVisible(true);
        SSHCrabController.this.buttonStop.setVisible(false);
        updateMessageLabel(String.format("SSH端口转发停止"));
    }

    @FXML
    protected void onConfigureFileChooseClick() {
        String currentDir = SSHCrabApplication.userHomeDir+File.separator+".sshcrab";
        String filename = "config.properties";
        if (sshConnectionParams!=null) {
            if (sshConnectionParams.getConfigFile()!=null) {
                currentDir = sshConnectionParams.getConfigFile().getParent();
                filename = sshConnectionParams.getConfigFile().getName();
            }
        }

        FileChooser fileChooser = new FileChooser();
        if (currentDir!=null&&currentDir.trim().length()>0) {
            fileChooser.setInitialDirectory(new File(currentDir));
        }
        if (filename!=null&&filename.trim().length()>0) {
            fileChooser.setInitialFileName(filename);
        }
        fileChooser.setTitle("请选择配置文件");
//        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("properties", ".*"));

        File file = fileChooser.showOpenDialog(labelMessageScrollPanel.getScene().getWindow());
        if (file!=null&&file.isFile()) {
            loadConfigParams(file.getAbsolutePath());
        }
    }

    SSHConnection conexionssh;
    SSHConnectionParams sshConnectionParams;

    public SSHCrabController() {}

    private void updateMessageLabel(String message) {
        Text text;
        if(labelMessage.getChildren().size()==0){
            text = new Text(message);
        } else {
            // Add new line if not the first child
            text = new Text("\n" + message);
        }
        labelMessage.getChildren().add(text);
    }

    private void loadConfigParams(String filepath) {
        if (filepath==null) {
            filepath = PropertiesHelper.userDefaultConfigFilePath;
        }

        final String configFilePath = filepath;
        try {
            sshConnectionParams = PropertiesHelper.readPropertyFile(logger, configFilePath);
            if (sshConnectionParams!=null) {
                if (sshConnectionParams.getConfigFile()!=null) {
                    configFileChooseButton.setText(sshConnectionParams.getConfigFile().getName());
                    updateMessageLabel(String.format("已加载配置文件配置：%s", sshConnectionParams.getConfigFile().getAbsolutePath()));
                }else {
                    updateMessageLabel(String.format("已加载配置文件配置：%s", configFilePath));
                }

                updateMessageLabel(String.format("配置文件内容：\n%s", sshConnectionParams.toString()));

                textFieldSSHHost.setText(stringValue(sshConnectionParams.getSshRemoteHost()));
                textFieldSSHPort.setText(stringValue(sshConnectionParams.getRemoteSSHPort()));
                textFieldForwardHost.setText(stringValue(sshConnectionParams.getForwardFromRemoteHost()));
                textFieldForwardPort.setText(stringValue(sshConnectionParams.getForwardFromRemotePort()));
                textFieldLocalHost.setText(stringValue(sshConnectionParams.getForwardToLocalHost()));
                textFieldLocalPort.setText(stringValue(sshConnectionParams.getForwardToLocalPort()));

                textFieldSSHKeyPath.setText(stringValue(sshConnectionParams.getPrivateKeyPath()));
                textFieldSSHKeyPhrase.setText(stringValue(sshConnectionParams.getPrivateKeyPhrase()));
                textFieldSSHHostsPath.setText(stringValue(sshConnectionParams.getKnowHostsPath()));
                textFieldSSHHostUsername.setText(stringValue(sshConnectionParams.getSshUserName()));
//                        selectBoxUseSSHKey.setSelectedItem(sshConnectionParams.getAuthType());
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateMessageLabel(String.format("加载配置文件%s异常：%s", filepath, e.getMessage()));
        }
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

//        SSHConnectionParams.AuthType authType = (SSHConnectionParams.AuthType) selectBoxUseSSHKey.getSelectedItem();
        SSHConnectionParams.AuthType authType = SSHConnectionParams.AuthType.key;
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
//            JOptionPane.showMessageDialog(null, "请输入SSH主机地址", "SSH主机地址【出错啦】", JOptionPane.ERROR_MESSAGE);
            updateMessageLabel("启动失败：SSH主机地址【出错啦】");
            return null;
        }

        if(!isValidHostName(params.getSshRemoteHost())) {
//            JOptionPane.showMessageDialog(null, "不合法的SSH主机地址", "SSH主机地址【出错啦】",  JOptionPane.ERROR_MESSAGE);
            updateMessageLabel("启动失败：SSH主机地址【出错啦】，不合法的主机地址："+params.getSshRemoteHost());
            return null;
        }
        if(isEmpty(params.getForwardFromRemoteHost())) {
//            JOptionPane.showMessageDialog(null, "请输入目标远程转发源地址", "远程转发源地址【出错啦】", JOptionPane.ERROR_MESSAGE);
            updateMessageLabel("启动失败：远程转发源地址【出错啦】，不能为空");
            return null;
        }
        if(!isValidHostName(params.getForwardFromRemoteHost())) {
//            JOptionPane.showMessageDialog(null, "不合法的远程转发源地址", "远程转发源地址【出错啦】", JOptionPane.ERROR_MESSAGE);
            updateMessageLabel("启动失败：远程转发源地址【出错啦】，不合法的主机名："+params.getForwardFromRemoteHost());
            return null;
        }

        if(isEmpty(params.getPrivateKeyPhrase())&&isEmpty(params.getPrivateKeyPath())) {
//            JOptionPane.showMessageDialog(null, "请输入SSH私钥地址或用户名密码", "授权失败【出错啦】", JOptionPane.ERROR_MESSAGE);
            updateMessageLabel("启动失败：身份认证失败【出错啦】，公钥密码密码不能为空");
            return null;
        }

        return params;
    }

    private boolean isEmpty(Object value) {
        if (value==null) return true;
        return "".equals(value.toString().trim());
    }

    private boolean isValidHostName(String value) {
        return true;
//        return value!=null&&("localhost".equalsIgnoreCase(value)||(Pattern.compile(ipPattern).matcher(value).find()||Pattern.compile(hostPattern).matcher(value).find()));
    }

    private String stringValue(Object value) {
        if (isEmpty(value)) return "";
        return value.toString();
    }

    private void saveConfigFile(SSHConnectionParams connectionParams) {
        String savedMessage = PropertiesHelper.writePropertyFile(connectionParams);
        if (savedMessage!=null) {
            updateMessageLabel(savedMessage);
        }
    }

    static final String ipPattern = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
    static final String hostPattern = "^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";

}
