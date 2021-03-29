package com.watsontech.tools;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Watson on 2019/12/10.
 */
public class PropertiesHelper {

    public static SSHConnectionParams readPropertyFile(org.slf4j.Logger logger, String filepath) throws IOException {
        SSHConnectionParams params = new SSHConnectionParams();
        logger.info("准备读取本地配置文件{}", filepath);

        Properties properties = new Properties();
        //首先检查当前路径下是否有config.properties文件
        File configFile = new File(filepath);
        if (!configFile.exists()) {
            logger.info("当前目录未找到配置文件："+filepath);

            File tmpFile = new File(System.getProperty("user.dir")+ File.separatorChar+filepath);
            if (tmpFile.exists()) {
                configFile = tmpFile;
            }
        }
        if (!configFile.exists()) {
            logger.info("启动目录未找到配置文件："+filepath);

            //最后检查个人主目录下是否有.sshscrab/config.properties文件
            File tmpFile = new File(System.getProperty("user.home")+ File.separatorChar+".sshscrab"+ File.separatorChar+filepath);
            if (tmpFile.exists()) {
                configFile = tmpFile;
            }else {
                logger.info("用户主目录未找到配置文件："+filepath);
            }
        }

        if (configFile.exists()) {
            logger.info("本地配置文件已找到，路径为：{}", configFile.getAbsolutePath());
            params.setConfigFile(configFile);
        }else {
            logger.info("本地配置文件未找到配置文件：{}", filepath);
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
                logger.info("remote host port should be integer");
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
                logger.info("forward remote host port should be integer");
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
                logger.info("forward local host port should be integer");
                return null;
            }
        } else if(properties.getProperty("flh")!=null) {
            String flh = properties.getProperty("flhp");
            params.setForwardToLocalHost(flh);
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            logger.info("No localport configuration. default local port same as remote, current is "+params.getForwardToLocalPort());
        } else {
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            logger.info("No localhost configuration. default host is 'localhost', port is same as remote, current is "+params.getForwardToLocalPort());
        }

        String u = properties.getProperty("u");
        if( u!=null ) {
            params.setSshUserName(u);
        }

        String at = properties.getProperty("at");
        if( at!=null ) {
            try {
                params.setAuthType(SSHConnectionParams.AuthType.valueOf(at));
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        String skp = properties.getProperty("skp");
        if(skp!=null) {
            String[] keyPharse = skp.split("@");
            if (keyPharse.length>0) {
                params.setPrivateKeyPath(keyPharse[0]);
            }
            if (keyPharse.length>1) {
                params.setPrivateKeyPhrase(Base64Util.decode(keyPharse[1]));
            }
        }else if (properties.getProperty("sk")!=null) {
            String sk = properties.getProperty("sk");
            params.setPrivateKeyPath(sk);
        }

        return params;
    }

    public static String writePropertyFile(SSHConnectionParams connectionParams) {
        if (connectionParams==null) return null;

        Properties properties = new Properties();
        //首先检查当前路径下是否有config.properties文件
        properties.put("rhp", wrapRHPParams(connectionParams));
        properties.put("frhp", wrapFRHPParams(connectionParams));
        properties.put("flhp", wrapFLHPParams(connectionParams));
        properties.put("u", connectionParams.getSshUserName());
        properties.put("skp", wrapSKPParams(connectionParams));

        if (connectionParams.getAuthType()!=null) {
            properties.put("at", connectionParams.getAuthType().name());
        }
        // 使用properties对象加载输入流
        String configFileDir = System.getProperty("user.home")+ File.separatorChar+".sshscrab";
        String configFilePath = configFileDir + File.separatorChar+"config.properties";
        try {
            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                if(!new File(configFileDir).exists()) {
                    if(!new File(configFileDir).mkdirs()) {
                        return "创建目录失败："+configFileDir;
                    }
                }

                if(!configFile.createNewFile()) {
                    return "创建配置文件失败："+configFilePath;
                }
            }
            FileWriter fw = new FileWriter(configFile);
            properties.store(fw, "saved in "+ DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
            fw.close();
            return "配置已保存："+configFile;
        } catch (IOException e) {
            e.printStackTrace();
            return "配置保存异常："+e.getMessage();
        }
    }

    private static void parseFLHPParams(String flhp, SSHConnectionParams params) {
        String[] localHostPort = flhp.split(":");
        if (localHostPort.length>0) {
            params.setForwardToLocalHost(localHostPort[0]);
        }
        if(localHostPort.length>1) {
            try {
                params.setForwardToLocalPort(Integer.parseInt(localHostPort[1]));
            }catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String wrapSKPParams(SSHConnectionParams params) {
        String skp = "";
        if (params.getPrivateKeyPath()!=null) {
            skp +=params.getPrivateKeyPath();
        }
        skp +="@"+Base64Util.encode(params.getPrivateKeyPhrase());
        return skp;
    }

    private static String wrapFLHPParams(SSHConnectionParams params) {
        String flhp = "";
        if (params.getForwardToLocalHost()!=null) {
            flhp +=params.getForwardToLocalHost();
        }
        flhp +=":"+params.getForwardToLocalPort();
        return flhp;
    }

    private static void parseFRHPParams(String frhp, SSHConnectionParams params) {
        String[] remoteHostPort = frhp.split(":");
        if (remoteHostPort.length>0) {
            params.setForwardFromRemoteHost(remoteHostPort[0]);
        }
        if(remoteHostPort.length>1) {
            try {
                params.setForwardFromRemotePort(Integer.parseInt(remoteHostPort[1]));
            }catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String wrapFRHPParams(SSHConnectionParams params) {
        String frhp = "";
        if (params.getForwardFromRemoteHost()!=null) {
            frhp +=params.getForwardFromRemoteHost();
        }
        frhp +=":"+params.getForwardFromRemotePort();
        return frhp;
    }

    private static void parseRHPParams(String rhp, SSHConnectionParams params) {
        String[] remoteHostPort = rhp.split(":");
        params.setSshRemoteHost(remoteHostPort[0]);
        try {
            params.setRemoteSSHPort(Integer.parseInt(remoteHostPort[1]));
        }catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private static String wrapRHPParams(SSHConnectionParams params) {
        String rhp = "";
        if (params.getSshRemoteHost()!=null) {
            rhp +=params.getSshRemoteHost();
        }
        rhp +=":"+params.getRemoteSSHPort();
        return rhp;
    }
}
