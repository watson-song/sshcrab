# sshcrab 远程发蟹

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/res/sshcrab.png?raw=true)

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/res/ScreenShot1.png?raw=true)

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/res/ScreenShot2.png?raw=true)

本工具可以方便的帮助开发者将ssh连接端口转发到本地

##Mac app 下载

[Download](https://github.com/watson-song/sshcrab/releases/download/1.0/SSH.Crab-Mac.zip)

##PC 2.0 app 下载
[Download](https://github.com/watson-song/sshcrab/releases/download/2.0/sshcrab2.zip)

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

C:\Users\Watson\.jdks\corretto-17.0.3\bin\jpackage --name SSHCrab --app-version 2.0 --copyright 2023@WatsonTech.Ltd --description "SSHCrap V2.0" --vendor WatsonTech.Ltd --type msi --win-shortcut --win-menu --win-dir-chooser --icon src\main\resources\app.ico -m com.watsontech.tools.sshcrab2/com.watsontech.tools.sshcrab2.SSHCrabApplication --dest .\dist\ --runtime-image .\target\image