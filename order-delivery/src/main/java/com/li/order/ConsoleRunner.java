package com.li.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 控制台输入层：负责「读键盘、识别 0 与 q、把下单动作交给 OrderService」
 *
 * <p>它是客户端的替身——客户没有界面，在这里输入 {@code 0} 就表示完成一次下单动作
 *
 * <p>本类只做输入解析，不知道下单之后会发生什么；不保存订单，也不输出「用户下单了」，
 * 唯一会输出的内容是输入非法时的「下单错误」
 */
@Component
public class ConsoleRunner implements CommandLineRunner {

    /** 约定的下单指令：输入该字符串视为一次下单信号 */
    private static final String ORDER_COMMAND = "0";

    /** 约定的退出指令：输入该字符串结束循环，程序随之退出 */
    private static final String QUIT_COMMAND = "q";

    /** 日志对象，只用于输出非法输入提示「下单错误」 */
    private static final Logger log = LoggerFactory.getLogger(ConsoleRunner.class);

    /** 下单动作的执行者，由 Spring 通过构造器注入 */
    private final OrderService orderService;

    /**
     * 构造器注入
     *
     * <p>作用：由 Spring 把容器里的 {@link OrderService} 交给本类使用，
     * 不在方法内 new 对象；同时让依赖关系直接体现在签名上，便于核对
     *
     * <p>调用者：Spring 容器创建 bean 时
     *
     * @param orderService 下单服务，不能为 null
     */
    public ConsoleRunner(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 容器启动完成后的回调：进入循环，等待键盘输入
     *
     * <p>作用：阻塞主线程，使程序停在等待输入的状态；每读到一行输入按三种情况分支：
     * 命中 {@link #ORDER_COMMAND} 调用 {@link OrderService#placeOrder()}；
     * 命中 {@link #QUIT_COMMAND} 跳出循环结束程序；其余输入输出一行「下单错误」后继续循环
     *
     * <p>调用者：Spring 容器（{@link CommandLineRunner} 回调），不被业务代码调用
     *
     * <p>行为要求：循环直到输入 {@code q} 或输入流结束（Ctrl+Z / EOF）；
     * 期间可重复输入 {@code 0} 并重复下单；除「用户下单了」与「下单错误」外不产生其他输出
     *
     * @param args 启动参数，本阶段不使用
     */
    @Override
    public void run(String... args) {
        // TODO 待实现：Scanner 循环读入 -> 分支判断 ORDER_COMMAND / QUIT_COMMAND / 其他
    }
}
