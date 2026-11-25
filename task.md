# 第一阶段任务：下单 → 收到

## 技术栈：

- Java 21（本机 JDK 21.0.8 LTS，安装在 D:\javaSoft）
- Maven 3.9.14（本机路径 D:\Maven\apache-maven-3.9.14）
- Spring Boot 3.5.16，仅引入 `spring-boot-starter` 一个依赖（该版本已在本地 Maven 仓库中，构建无需联网下载）
- 交互方式：`Scanner` 读取标准输入，`CommandLineRunner` 在容器启动后接管控制台
- 输出方式：SLF4J 日志（Spring Boot 自带）

不引入 web、数据库、校验、消息队列等任何依赖

## 具体的内容：（自然语言说明当前要实现什么样的功能）

客户下单这一小段的最小闭环

客户没有界面，在命令行输入 `0` 即视为完成一次下单动作

系统收到下单信号后，在服务端输出「用户下单了」

这一版只验证一个下单信号能从客户端走到服务端并产生反应，信号本身不携带任何订单内容，不存储订单，不做状态流转，不做浏览器界面，不发网络请求

## 文件结构：

```
order-delivery/
├─ pom.xml                    Java 21、Spring Boot 3.5.16 父工程、spring-boot-starter 依赖与打包插件
└─ src/main/
   ├─ java/com/li/order/
   │  ├─ OrderApp.java        启动类，启动 Spring 容器
   │  ├─ ConsoleRunner.java   读键盘输入，识别 0，调用 OrderService
   │  ├─ OrderService.java    下单这件事本身，收到调用后交给商家侧
   │  └─ MerchantNotifier.java 商家侧，收到信号后输出「用户下单了」
   └─ resources/
      └─ application.yml      关闭 banner 等基础配置
```

四个 Java 文件职责互不交叉：启动、输入、下单、接收

不建订单数据类，不建订单仓库，不建状态字段

## 验收标准：（比如这次是客户端输入 0 下单，服务端输出"用户下单了"）

- 执行 `mvn spring-boot:run` 能正常启动并停在等待输入的状态
- 客户端输入 `0`，服务端输出「用户下单了」
- 连续输入多次 `0`，每次都输出一次「用户下单了」
- 除上述输出外，没有任何多余的功能输出

