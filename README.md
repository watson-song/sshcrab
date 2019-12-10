# sshcrab 远程发蟹 

![Image text](https://github.com/watson-song/sshcrab/blob/master/src/res/sshcrab.png?raw=true)

本工具可以方便的帮助开发者将ssh连接端口转发到本地

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