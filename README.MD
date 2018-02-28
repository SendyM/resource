# 基本环境部署
1) 创建基础目录结构

        $ mkdir logs
2) 安装[ansible](http://docs.ansible.com/ansible/latest/intro_installation.html#latest-releases-via-pip)

        $ sudo easy_install pip
        $ sudo pip install ansible
        

# 部署
   使用[ansible](http://badu.com)
    

# 服务启动、远程停止

## 启动
1) 默认启动


    nohup $ ./resource-0.0.1-SNAPSHOT.jar >> logs/`date +"%Y_%m_%d.log"` 2>>logs/`date +"%Y_%m_%d.err"` &

2) 指定端口
    通过 `server.port=7788`指定端口

    java -Dserver.port=7788 -jar ./resource-0.0.1-SNAPSHOT.jar

## 远程停止


    curl -X POST http://localhost:8080/shutdown
    
## cassandra

测试机配置远程连接


    #apache-cassandra-3.0.15/conf/cassandra.yaml
    listen_interface: ens3 #481行左右
    
##API接口文档地址
http://localhost:8080/swagger-ui.html