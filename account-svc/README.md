# account远程调试

1.项目打包
  gradle clean build -x test
2.启动jar包
  java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar account-svc-1.0.0.jar
     ##注释：      
     ##-Xdebug是通知JVM工作在DEBUG模式下
     ##-Xrunjdwp是通知JVM使用(java debug wire protocol)来运行调试环境。
     ##server=y/n            VM 是否需要作为调试服务器执行。 
     ##address=8000          调试服务器的端口号，客户端用来连接服务器的端口号。 
     ##suspend=y/n           是否在调试客户端建立连接之后启动 VM 。 
     ##dt_socket             套接字传输
3.eclipse配置
  run --->>>>debug configurations------>>>remote java application------>>>new configuration
  host: localhost
  port: 8000
  debug启动
  