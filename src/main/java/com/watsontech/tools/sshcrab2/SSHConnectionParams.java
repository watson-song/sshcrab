package com.watsontech.tools.sshcrab2;

import java.io.File;

public class SSHConnectionParams {
    String privateKeyPath;
    String knowHostsPath = "~\\.ssh\\known_hosts";
    String privateKeyPhrase;
    enum AuthType {
        key("SSH密钥登陆"),password("密码登陆");
        String label;

        AuthType(String label) {
            this.label = label;
        }

        public String label() {return label;}

        @Override
        public String toString() {
            return label;
        }
    }
    //默认密码
    AuthType authType = AuthType.password;

    //代理到本地端口
    //by security policy, you must connect through a fowarded port
    //ssh -L 192.168.0.102:5555:192.168.0.101:3306 yunshouhu@192.168.0.102  正向代理
    int forwardToLocalPort = 3306;
    String forwardToLocalHost = "localhost";
    //远程主机端口
    int forwardFromRemotePort = 3306;
    String forwardFromRemoteHost;

    //远程ssh主机端口
    String sshRemoteHost;
    int remoteSSHPort = 22;
    String sshUserName;

    //配置文件路径
    private File configFile;

    public SSHConnectionParams() {}

    //缺省 本地和远程端口3306，本地主机：localhost，ssh远程主机端口：22，用户名：root，本地ssh秘钥路径mac：~/.ssh/id_rsa
    public SSHConnectionParams( String sshRemoteHost, String privateKeyPhrase, String forwardFromRemoteHost) {
        this.forwardFromRemoteHost = forwardFromRemoteHost;
        this.sshRemoteHost = sshRemoteHost;
        this.privateKeyPhrase = privateKeyPhrase;
    }

    //缺省 本地ssh秘钥路径mac：~/.ssh/id_rsa
    public SSHConnectionParams(int forwardToLocalPort, String forwardToLocalHost, int forwardFromRemotePort, String forwardFromRemoteHost, String sshRemoteHost, int remoteSSHPort, String sshUserName, String privateKeyPhrase) {
        this.forwardToLocalPort = forwardToLocalPort;
        this.forwardToLocalHost = forwardToLocalHost;
        this.forwardFromRemotePort = forwardFromRemotePort;
        this.forwardFromRemoteHost = forwardFromRemoteHost;
        this.sshRemoteHost = sshRemoteHost;
        this.remoteSSHPort = remoteSSHPort;
        this.sshUserName = sshUserName;
        this.privateKeyPhrase = privateKeyPhrase;
    }

    public SSHConnectionParams(String privateKeyPath, String knowHostsPath, String privateKeyPhrase, int forwardToLocalPort, String forwardToLocalHost, int forwardFromRemotePort, String forwardFromRemoteHost, String sshRemoteHost, int remoteSSHPort, String sshUserName) {
        this.privateKeyPath = privateKeyPath;
        this.knowHostsPath = knowHostsPath;
        this.privateKeyPhrase = privateKeyPhrase;
        this.forwardToLocalPort = forwardToLocalPort;
        this.forwardToLocalHost = forwardToLocalHost;
        this.forwardFromRemotePort = forwardFromRemotePort;
        this.forwardFromRemoteHost = forwardFromRemoteHost;
        this.sshRemoteHost = sshRemoteHost;
        this.remoteSSHPort = remoteSSHPort;
        this.sshUserName = sshUserName;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getKnowHostsPath() {
        return knowHostsPath;
    }

    public void setKnowHostsPath(String knowHostsPath) {
        this.knowHostsPath = knowHostsPath;
    }

    public String getPrivateKeyPhrase() {
        return privateKeyPhrase;
    }

    public void setPrivateKeyPhrase(String privateKeyPhrase) {
        this.privateKeyPhrase = privateKeyPhrase;
    }

    public int getForwardToLocalPort() {
        return forwardToLocalPort;
    }

    public void setForwardToLocalPort(int forwardToLocalPort) {
        this.forwardToLocalPort = forwardToLocalPort;
    }

    public String getForwardToLocalHost() {
        return forwardToLocalHost;
    }

    public void setForwardToLocalHost(String forwardToLocalHost) {
        this.forwardToLocalHost = forwardToLocalHost;
    }

    public int getForwardFromRemotePort() {
        return forwardFromRemotePort;
    }

    public void setForwardFromRemotePort(int forwardFromRemotePort) {
        this.forwardFromRemotePort = forwardFromRemotePort;
    }

    public String getForwardFromRemoteHost() {
        return forwardFromRemoteHost;
    }

    public void setForwardFromRemoteHost(String forwardFromRemoteHost) {
        this.forwardFromRemoteHost = forwardFromRemoteHost;
    }

    public String getSshRemoteHost() {
        return sshRemoteHost;
    }

    public void setSshRemoteHost(String sshRemoteHost) {
        this.sshRemoteHost = sshRemoteHost;
    }

    public int getRemoteSSHPort() {
        return remoteSSHPort;
    }

    public void setRemoteSSHPort(int remoteSSHPort) {
        this.remoteSSHPort = remoteSSHPort;
    }

    public String getSshUserName() {
        return sshUserName;
    }

    public void setSshUserName(String sshUserName) {
        this.sshUserName = sshUserName;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public File getConfigFile() {
        return configFile;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    @Override
    public String toString() {
        return "{" +
                "privateKeyPath='" + privateKeyPath + '\'' +
                ", knowHostsPath='" + knowHostsPath + '\'' +
                ", privateKeyPhrase='" + privateKeyPhrase + '\'' +
                ", authType=" + authType +
                ", forwardToLocalPort=" + forwardToLocalPort +
                ", forwardToLocalHost='" + forwardToLocalHost + '\'' +
                ", forwardFromRemotePort=" + forwardFromRemotePort +
                ", forwardFromRemoteHost='" + forwardFromRemoteHost + '\'' +
                ", sshRemoteHost='" + sshRemoteHost + '\'' +
                ", remoteSSHPort=" + remoteSSHPort +
                ", sshUserName='" + sshUserName + '\'' +
                ", configFile=" + configFile +
                '}';
    }
}