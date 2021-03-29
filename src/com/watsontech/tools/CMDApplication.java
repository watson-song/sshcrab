package com.watsontech.tools;

import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CMDApplication {
    static Logger logger = LoggerFactory.getLogger(CMDApplication.class);

    private SSHConnection conexionssh;

    public CMDApplication(SSHConnectionParams params) throws JSchException {
        conexionssh = new SSHConnection(params);
    }

    private static SSHConnectionParams readPropertyFile(String filename) throws IOException {
        SSHConnectionParams params = new SSHConnectionParams();
        logger.info("读取本地配置文件{}", filename);
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = CMDApplication.class.getClassLoader().getResourceAsStream(filename);
        if (in==null) {
            if(!new File(filename).exists()) {
                filename = System.getProperty("user.dir")+ File.separatorChar+filename;
            }

            if(new File(filename).exists()) {
                in = new FileInputStream(filename);
            }else {
                logger.warn("本地配置文件{}未找到，退出程序", filename);
            }
        }
        if (in==null) return null;

        // 使用properties对象加载输入流
        properties.load(in);
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

    private static SSHConnectionParams parseArgs(String[] args) throws ParseException, IOException {
        // Create a Parser
        CommandLineParser parser = new BasicParser( );
        Options options = new Options( );
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("cf", "config file", true, "config file, -cf config.properties");
        options.addOption("rhp", "remoteHostPort", true, "remote-host and port, like 10.10.10.1:22");
        options.addOption("rh", "remoteHost", true, "remote-host, like 10.10.10.1, port default 22");
        options.addOption("u", "remoteUser", true, "remote host username, like root");
        options.addOption("skp", "sshKeyPhrase", true, "ssh file path and phrase");
        options.addOption("frhp", "forwardRemoteHostPort", true, "forward from remote host port");
        options.addOption("flhp", "forwardLocalHostPort", true, "forward to local host port");

        // Parse the program arguments
        CommandLine commandLine = parser.parse( options, args );
        // Set the appropriate variables based on supplied options

        if( commandLine.hasOption('h') ) {
            return null;
        }
        if( commandLine.hasOption("cf") ) {
            return readPropertyFile(commandLine.getOptionValue("cf"));
        }
        SSHConnectionParams params = new SSHConnectionParams();

        if( commandLine.hasOption("rhp") ) {
            String rhp = commandLine.getOptionValue("rhp");
            try {
                parseRHPParams(rhp, params);
            }catch (NumberFormatException e) {
                System.out.println("remote host port should be integer");
                return null;
            }
        } else if(commandLine.hasOption("rh") ) {
            String remoteHost = commandLine.getOptionValue("rh");
            params.setSshRemoteHost(remoteHost);
        }else {
            return null;
        }

        if(commandLine.hasOption("frhp")) {
            String frhp = commandLine.getOptionValue("frhp");
            try {
                parseFRHPParams(frhp, params);
            }catch (NumberFormatException e) {
                System.out.println("forward remote host port should be integer");
                return null;
            }
        } else {
            return null;
        }

        if(commandLine.hasOption("flhp")) {
            String flhp = commandLine.getOptionValue("flhp");
            try {
                parseFLHPParams(flhp, params);
            }catch (NumberFormatException e) {
                System.out.println("forward local host port should be integer");
                return null;
            }
        } else if(commandLine.hasOption("flh")) {
            String flh = commandLine.getOptionValue("flh");
            params.setForwardToLocalHost(flh);
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            System.out.println("No localport configuration. default local port same as remote, current is "+params.getForwardToLocalPort());
        } else {
            params.setForwardToLocalPort(params.getForwardFromRemotePort());
            System.out.println("No localhost configuration. default host is 'localhost', port is same as remote, current is "+params.getForwardToLocalPort());
        }
        if(commandLine.hasOption("skp")) {
            String skp = commandLine.getOptionValue("skp");
            String[] keyPharse = skp.split("@");
            if (keyPharse.length>0) {
                params.setPrivateKeyPath(keyPharse[0]);
            }
            if (keyPharse.length>1) {
                params.setPrivateKeyPhrase(keyPharse[1]);
            }
        }else if (commandLine.hasOption("sk")) {
            String sk = commandLine.getOptionValue("sk");
            params.setPrivateKeyPath(sk);
        }

        return params;
    }

    public static void printHelpMessage() {
        System.out.println("Connect remote host use ssh and forward remote host port to local");
        System.out.println("Usage example: ");
        System.out.println( "java -jar sshcrab.jar -rhp 1xx.1xx.1xx.1:22 -u root -skp ~/.ssh/id_rsa@password -frhp 17x.1x.0.1:3306 -flhp localhost:3306");
        System.out.println( "-rhp remote-host and port, like 1xx.1xx.1xx.1:22");
        System.out.println( "-u remote username, default root");
        System.out.println( "-skp ssh file path, default is ~/.ssh/id_rsa, no pharse");
        System.out.println( "-frhp forward from remote host port");
        System.out.println( "-flhp forward to local host port");
        System.out.println( "-cf config file, like -cf config.properties");
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length==0) {
            args = new String[]{"-cf", "config.properties"};
        }

        try {
            SSHConnectionParams params = parseArgs(args);
            if (params==null) {
                printHelpMessage();
            }

            CMDApplication application = new CMDApplication(params);
            application.conexionssh.startSSH(100, null);
        } catch (JSchException e) {
            e.printStackTrace();
            logger.error("ssh代理启动失败", e);
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("参数解析失败", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取本地配置文件失败", e);
        }
        System.exit(0);
    }
}
