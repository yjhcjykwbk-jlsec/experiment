========
运行环境
首先用eclipse打开，看到CampusServer 项目（文件存在桌面上）。点eclipse上三个tomcat插件图标可以控制tomcat开启，关闭。
数据库开启要用wamp。在开始菜单里有。mysql 用户名root，密码1234。可以cmd下敲入mysql -uroot -p1234. 数据库名android。
每次刷新本app到tomcat上，可以在项目名上右键，找到tomcat菜单项，里面有个update context defination.(ps 本app对应localhost:8080/，即放在根目录下。)

========
配置文件
java resources/resources 下面
1 数据库相关配置
  hibernate.cfg.xml 
  是j2ee的数据库访问框架Hibernate的配置文件，
  定义了很多mapping映射，把数据库表映射到某个model数据模型类（下面提到））
  以及连接数据库参数（用户名密码等）

 *.hbm.xml 是一些多主键表的格式定义。
 如friend.hbm.xml声明了apn_friend数据库表包含Id1,id2两个列作为主键。

 jdbc.properties 是具体数据库连接时用到的用户名密码

2 spring-config.xml 是j2ee的spring框架配置文件，其实就是定义了很多bean，其类名，及其构造它所需的参数。
  这些bean基本上就是运行web app前预先需要初始化的一些(全局?)类对象实例。
  比如userdao,userservice,ioacceptor(xmpp用到的）

========
源码包的结构
java resources/src 下面
1 model 和数据库的表对应的数据模型类
2 dao  操作数据库的类接口，
  dao.hibernate 实现dao接口
3 service 完成一些逻辑功能的类的接口，如查询用户订阅。 
  service.impl实现了service类接口。
4 controller web服务的控制器。接收http请求返回结果（网页/xml)。
5 xmpp.* 推送xmpp协议的实现，包括加密鉴别通信等
 