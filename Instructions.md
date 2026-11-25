# Instructions — 第一阶段（下单 → 收到）接口与函数签名

本轮只给出签名与职责，不含实现；所有方法体为空，留 `TODO` 待填充

## 一、本次范围

- 覆盖 `order-delivery/src/main/java/com/li/order/` 下 4 个类，共 5 个方法与 3 个构造器
- 不含 `pom.xml`、`application.yml`：属于构建与配置项，不是方法签名，本轮不列
- 不新增任何类：无订单实体、无仓库、无状态字段，与 task.md 的约束一致
- 所有类同包 `com.li.order`，均为 `public`，方法均无返回值（`void`）

## 二、已确认的四项设计选择

| 项      | 结论                                                                   |
| ------ | -------------------------------------------------------------------- |
| 工程根目录  | `D:\订购+送货系统\order-delivery`（按 task.md 的结构块）                          |
| 核心方法命名 | `OrderService.placeOrder()`、`MerchantNotifier.notifyOrderReceived()` |
| 输入分支   | `0` 下单；`q` 退出；其余输入输出「下单错误」后继续循环                                      |
| 依赖注入   | 构造器注入，字段 `final`，不使用字段 `@Autowired`                                  |

## 三、各个接口与方法的作用（按文件）

### 1. `order-delivery/src/main/java/com/li/order/OrderApp.java`

| 签名                                       | 作用                                                            |
| ---------------------------------------- | ------------------------------------------------------------- |
| `public static void main(String[] args)` | 程序入口，把 `OrderApp` 交给 Spring 启动容器，容器启动后由 `ConsoleRunner` 接管控制台 |

标注：`@SpringBootApplication` 加在类上，使本类同时充当配置类与启动类

调用者：JVM（`mvn spring-boot:run` 间接拉起），不被业务代码调用

### 2. `order-delivery/src/main/java/com/li/order/ConsoleRunner.java`

| 签名                                                | 作用                                                              |
| ------------------------------------------------- | --------------------------------------------------------------- |
| `public ConsoleRunner(OrderService orderService)` | 构造器注入，让容器把 `OrderService` 交给本类持有                                |
| `public void run(String... args)`                 | `CommandLineRunner` 回调，进入循环读键盘；`0` 调用下单，`q` 结束循环，其他输入报「下单错误」并继续 |

相关字段：

- `private static final String ORDER_COMMAND = "0"`：下单指令，避免 `"0"` 散落在判断逻辑里
- `private static final String QUIT_COMMAND = "q"`：退出指令，让程序有明确的结束方式，不必按 Ctrl+Z
- `private static final Logger log`：只用于输出「下单错误」

标注：`@Component`（交容器管理）、`implements CommandLineRunner`（启动后自动回调）

边界：本类只解析输入，不保存订单，不输出「用户下单了」

### 3. `order-delivery/src/main/java/com/li/order/OrderService.java`

| 签名                                                       | 作用                               |
| -------------------------------------------------------- | -------------------------------- |
| `public OrderService(MerchantNotifier merchantNotifier)` | 构造器注入，让「下单」与「通知商家」两件事通过一层方法调用相连  |
| `public void placeOrder()`                               | 「下单」动作的唯一入口，被调用即代表客户下单，并把信号转交商家侧 |

标注：`@Service`

调用者：`ConsoleRunner.run(String...)`

边界：不生成订单对象、不校验、不落库、不做状态流转、无返回值

### 4. `order-delivery/src/main/java/com/li/order/MerchantNotifier.java`

| 签名                                  | 作用                              |
| ----------------------------------- | ------------------------------- |
| `public void notifyOrderReceived()` | 链路终点，被调用后在服务端日志输出「用户下单了」，作为验收依据 |

相关字段：`private static final Logger log = LoggerFactory.getLogger(MerchantNotifier.class)`，SLF4J 日志对象

标注：`@Component`

调用者：`OrderService.placeOrder()`

边界：不接单、不回执、不发货，每次调用只输出一行「用户下单了」

## 四、依赖与调用关系

### 4.1 Bean 依赖（谁持有谁）

```
ConsoleRunner ──持有──> OrderService ──持有──> MerchantNotifier
```

两条依赖全部由构造器注入，没有字段注入、没有 `new`，依赖方向单一，不存在循环依赖

### 4.2 运行时调用树（从启动到输出）

```
mvn spring-boot:run
└─ JVM
   └─ OrderApp.main(String[])                       ← 程序入口，启动容器
      └─ SpringApplication.run(OrderApp.class, args)
         ├─ 创建 MerchantNotifier                     ← 无依赖，先建
         ├─ 创建 OrderService(MerchantNotifier)       ← 构造器注入
         ├─ 创建 ConsoleRunner(OrderService)          ← 构造器注入
         └─ ConsoleRunner.run(String...)              ← 框架回调（非业务调用）
            └─ 循环：Scanner 读一行输入
               ├─ input == "0"
               │  └─ OrderService.placeOrder()              ← 下单入口
               │     └─ MerchantNotifier.notifyOrderReceived()
               │        └─ log.info("用户下单了")            ← 链路终点，产生输出
               ├─ input == "q"
               │  └─ 跳出循环，run 返回，程序退出            ← 无输出
               └─ 其他输入
                  └─ log.warn("下单错误")                    ← 输入层本地输出，不进入业务链路
```

循环只在输入 `q` 或输入流结束（Ctrl+Z / EOF）时终止，因此可以连续输入多次 `0`，每次都完整走一遍从 `placeOrder()` 到 `log.info("用户下单了")` 的路径

### 4.3 调用边清单（含边的类型）

| 起点                                     | 终点                                     | 类型           | 说明                                |
| -------------------------------------- | -------------------------------------- | ------------ | --------------------------------- |
| `OrderApp.main`                        | `SpringApplication.run`                | 直接调用（框架 API） | 启动容器，本阶段唯一一行实现                    |
| `SpringApplication.run`                | `OrderService` 构造器                     | 容器反射调用       | 依赖装配，注入 `MerchantNotifier`        |
| `SpringApplication.run`                | `ConsoleRunner` 构造器                    | 容器反射调用       | 依赖装配，注入 `OrderService`            |
| `SpringApplication.run`                | `ConsoleRunner.run`                    | 框架回调         | `CommandLineRunner` 回调，代码中无直接调用语句 |
| `ConsoleRunner.run`                    | `OrderService.placeOrder`              | 直接调用         | 命中 `ORDER_COMMAND` 时触发            |
| `ConsoleRunner.run`                    | `ConsoleRunner.log.warn`               | 直接调用（日志 API） | 非法输入时输出「下单错误」，是本次唯一的旁路输出          |
| `OrderService.placeOrder`              | `MerchantNotifier.notifyOrderReceived` | 直接调用         | 把信号交给商家侧                          |
| `MerchantNotifier.notifyOrderReceived` | `MerchantNotifier.log.info`            | 直接调用（日志 API） | 输出「用户下单了」                         |

### 4.4 图中没有出现的边（便于核对是否有遗漏）

- 没有反向调用：`OrderService` 不回调 `ConsoleRunner`，`MerchantNotifier` 不回调 `OrderService`
- 没有任何方法返回值被使用，链路上没有数据传递，符合「信号不携带订单内容」
- 除 `CommandLineRunner.run` 的 `String...` 形参外，本次 5 个方法均无入参
- `q` 分支没有对应的业务方法，只是循环控制，因此不出现在业务调用链上

## 五、与 task.md 验收标准的对应

| 验收项                             | 对应实现位置                                                             |
| ------------------------------- | ------------------------------------------------------------------ |
| `mvn spring-boot:run` 启动后停在等待输入 | `ConsoleRunner.run` 内的循环阻塞主线程                                      |
| 输入 `0` 输出「用户下单了」                | `OrderService.placeOrder` → `MerchantNotifier.notifyOrderReceived` |
| 连续多次 `0` 每次都输出一次                | 循环不退出，每次命中都重走一遍链路                                                  |
| 除上述输出外没有多余输出                    | 仅 `0`/`q`/非法输入三分支；`q` 静默                                           |

一处与验收标准第 4 条的偏差需要明确记录：非法输入会打印「下单错误」，这属于「用户下单了」之外的额外输出，是按本轮设计选择引入的；`q` 分支保持静默，不额外打印退出提示
