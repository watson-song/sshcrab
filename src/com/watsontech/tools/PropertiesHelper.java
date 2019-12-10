package com.watsontech.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Watson on 2019/12/10.
 */
public class PropertiesHelper {

    public static SSHConnectionParams readPropertyFile(org.slf4j.Logger logger, String filepath) throws IOException {
        SSHConnectionParams params = new SSHConnectionParams();
        logger.info("读取配置文件{}", filepath);

        Properties properties = new Properties();
        File configFile = new File(filepath);
        if (!configFile.exists()) {
            // 使用ClassLoader加载properties配置文件生成对应的输入流
            try {
                filepath = SSHCrab.class.getClassLoader().getResource(filepath).toURI().getPath();
                configFile = new File(filepath);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            if (!configFile.exists()) {
                filepath = System.getProperty("user.dir")+ File.separatorChar+filepath;
                configFile = new File(filepath);
            }
        }

        if (configFile.exists()) {
            logger.info("本地配置文件已找到，路径为：{}", configFile.getAbsolutePath());
            params.setConfigFile(configFile);
        }else {
            logger.info("本地配置文件已找到，路径为:{}", filepath);
            return null;
        }

        // 使用properties对象加载输入流
        properties.load(new FileInputStream(configFile));
        //获取key对应的value值
        String rhp = properties.getProperty("rhp");
        if( rhp!=null ) {
            try {
                parseRHPParams(rhp, params);
            }catch (NumberFormatException e) {
                System.out.println("remote host port should be integer");
                return null;
            }
        } else if(properties.getProperty("rh")!=null) {
            String remoteHost = properties.getProperty("rh");
            params.setSshRemoteHost(remoteHost);
        }else {
            return null;
        }

        String frhp = properties.getProperty("frhp");
        if(frhp!=null) {
            try {
                parseFRHPParams(frhp, params);
            }catch (NumberFormatException e) {
                System.out.println("forward remote host port should be integer");
                return null;
            }
        } else {
            return null;
        }

        String flhp = properties.getProperty("flhp");
        if(flhp!=null) {
            try {
                parseFLHPParams(flhp, params);
            }catch (NumberFormatException e) {
                System.out.println("forward local host port should be integer");
                return null;
            }
        } else if(properties.getProperty("flh")!=null) {
            String flh = properties.getProperty("flhp");
            params.setForwardToLocalHost(flh);
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            System.out.println("No localport configuration. default local port same as remote, current is "+params.getForwardToLocalPort());
        } else {
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            System.out.println("No localhost configuration. default host is 'localhost', port is same as remote, current is "+params.getForwardToLocalPort());
        }

        String u = properties.getProperty("u");
        if( u!=null ) {
            params.setSshUserName(u);
        }

        String skp = properties.getProperty("skp");
        if(skp!=null) {
            String[] keyPharse = skp.split("@");
            if (keyPharse.length>0) {
                params.setPrivateKeyPath(keyPharse[0]);
            }
            if (keyPharse.length>1) {
                params.setPrivateKeyPhrase(keyPharse[1]);
            }
        }else if (properties.getProperty("sk")!=null) {
            String sk = properties.getProperty("sk");
            params.setPrivateKeyPath(sk);
        }

        return params;
    }

    private static void parseFLHPParams(String flhp, SSHConnectionParams params) throws NumberFormatException {
        String[] localHostPort = flhp.split(":");
        if (localHostPort.length>0) {
            params.setForwardToLocalHost(localHostPort[0]);
        }
        if(localHostPort.length>1) {
            params.setForwardToLocalPort(Integer.parseInt(localHostPort[1]));
        }
    }

    private static void parseFRHPParams(String frhp, SSHConnectionParams params) throws NumberFormatException {
        String[] remoteHostPort = frhp.split(":");
        if (remoteHostPort.length>0) {
            params.setForwardFromRemoteHost(remoteHostPort[0]);
        }
        if(remoteHostPort.length>1) {
            params.setForwardFromRemotePort(Integer.parseInt(remoteHostPort[1]));
        }
    }

    private static void parseRHPParams(String rhp, SSHConnectionParams params) throws NumberFormatException {
        String[] remoteHostPort = rhp.split(":");
        params.setSshRemoteHost(remoteHostPort[0]);
        params.setRemoteSSHPort(Integer.parseInt(remoteHostPort[1]));
    }
}
