# sshcrab 远程发蟹

#最新的Windows10+ 应用预览

![Image text](https://github.com/watson-song/sshcrab/assets/7800970/c23ff58c-07d8-47f0-a0f0-3f724f9897b7?raw=true)

#Mac Version

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/main/resources/sshcrab.png?raw=true)

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/main/resources/ScreenShot1.png?raw=true)

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/main/resources/ScreenShot2.png?raw=true)

本工具可以方便的帮助开发者将ssh连接端口转发到本地

##Mac app 下载

[Download](https://github.com/watson-song/sshcrab/releases/download/2.0.1.1/SSHCrab-2.0.dmg)

##PC 2.0 app 下载
[Download](https://github.com/watson-song/sshcrab/releases/download/2.0.1/SSHCrab-2.0.1.msi)

#使用手册

remote ssh host:port 远程SSH主机地址
#rh=xx.23x.23x.5x:22

forward remote:port 转发源地址/端口
#frhp=1x.6x.15x.16x:3306

forward localhost:port 转发目标地址/端口
#flhp=localhost:3306

username ssh用户名
#u=username

privateKeyPath@keyPhrase  私钥路径@密码(若有)
#skp=~/.ssh/id_rsa@password

打包步骤：
1、javafx:jlink
C:\Users\Watson\.jdks\corretto-17.0.3\bin\java.exe -Dmaven.multiModuleProjectDirectory=D:\JavaWorkspace\sshcrab "-Dmaven.home=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\plugins\maven\lib\maven3" "-Dclassworlds.conf=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\plugins\maven\lib\maven3\bin\m2.conf" "-Dmaven.ext.class.path=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\plugins\maven\lib\maven-event-listener.jar" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\lib\idea_rt.jar=44697:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\plugins\maven\lib\maven3\boot\plexus-classworlds-2.6.0.jar;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2022.1.3\plugins\maven\lib\maven3\boot\plexus-classworlds.license" org.codehaus.classworlds.Launcher -Didea.version=2022.1.3 org.openjfx:javafx-maven-plugin:0.0.8:jlink
2、jpackage 
## win version
C:\Users\Watson\.jdks\corretto-17.0.3\bin\jpackage --name SSHCrab --app-version 2.0 --copyright 2023@WatsonTech.Ltd --description "SSHCrap V2.0" --vendor WatsonTech.Ltd --type msi --win-shortcut --win-menu --win-dir-chooser --icon src\main\resources\app.ico -m com.watsontech.tools.sshcrab2/com.watsontech.tools.sshcrab2.SSHCrabApplication --dest .\dist\ --runtime-image .\target\image
## mac version 
/Users/watson/Library/Java/JavaVirtualMachines/openjdk-21.0.2/Contents/Home/bin/jpackage  --name SSHCrab --app-version 2.0 --copyright 2023@WatsonTech.Ltd --description "SSHCrap
  V2.0" --vendor WatsonTech.Ltd  --icon src/main/resources/sshscrab-128mac.icns -m com.watsontech.tools.sshcrab2/com.watsontech.tools.sshcrab2.SSHCrabApplication --dest ./dist/ --runtime-image ./target/image