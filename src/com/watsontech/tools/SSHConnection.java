package com.watsontech.tools;

/**
 * Created by Watson on 2018/12/12.
 */

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SSHConnection {

    public boolean isConnected() {
        return session!=null&&session.isConnected();
    }

    public interface ConnectionCallback {
        void onConnected(Session session);
        void onConnecteFailed(Session session);
    }

    Logger logger = LoggerFactory.getLogger(SSHConnection.class);

    private Session session; //represents each ssh session
    private SSHConnectionParams connectionParams;

    public void closeSSH() {
        session.disconnect();
        logger.info("SSH断开连接？{}", !session.isConnected());//这里打印SSH服务器版本信息
    }

    public void startSSH(int timeout, ConnectionCallback callback) throws JSchException {
        checkParams();
        logger.info("准备SSH远程连接 {}", connectionParams.getSshRemoteHost() + ":" + connectionParams.getRemoteSSHPort());//这里打印SSH服务器版本信息

        session.connect(timeout*1000); //ssh connection established!
        logger.info("SSH服务器版本 {}", session.getServerVersion());//这里打印SSH服务器版本信息

        //by security policy, you must connect through a fowarded port
        //ssh -L 192.168.0.102:5555:192.168.0.101:3306 yunshouhu@192.168.0.102  正向代理
        int assingedPort = session.setPortForwardingL(connectionParams.getForwardToLocalHost(), connectionParams.getForwardToLocalPort(), connectionParams.getForwardFromRemoteHost(), connectionParams.getForwardFromRemotePort());
        logger.info("端口转发成功 远程:{} >> 本地:{}", connectionParams.getForwardFromRemoteHost()+":"+connectionParams.getForwardFromRemotePort(), connectionParams.getForwardToLocalHost()+":" + assingedPort);
        logger.info("连接状态:{}", session.getPortForwardingL());

        if (callback!=null) {
            callback.onConnected(session);
        }
    }

    public SSHConnection(SSHConnectionParams params) throws JSchException {
        connectionParams = params;
        checkParams();

        JSch jsch = new JSch();
        JSch.setLogger(new com.jcraft.jsch.Logger() {
            @Override
            public boolean isEnabled(int level) {
                return true;
            }

            @Override
            public void log(int level, String message) {
                System.out.println(message);
            }
        });
        if (connectionParams.getKnowHostsPath()!=null) {
            jsch.setKnownHosts(connectionParams.getKnowHostsPath());
        }
        if (connectionParams.getPrivateKeyPath()!=null) {
            jsch.addIdentity(connectionParams.getPrivateKeyPath(), connectionParams.getPrivateKeyPhrase());
        }

        session = jsch.getSession(connectionParams.getSshUserName(), connectionParams.getSshRemoteHost(), connectionParams.getRemoteSSHPort());
        session.setPassword(connectionParams.getPrivateKeyPhrase());

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
    }

    private void checkParams() {
        if (connectionParams==null||connectionParams.getSshRemoteHost()==null) {
            throw new IllegalArgumentException("链接参数不能为空");
        }
    }
}
