package com.li.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类：整个程序的入口
 *
 * <p>职责只有一件事——把 Spring 容器启动起来。容器启动后会创建
 * {@link ConsoleRunner}、{@link OrderService}、{@link MerchantNotifier} 三个对象，
 * 其中 {@link ConsoleRunner} 会被容器回调，从而接管控制台
 *
 * <p>本类不读输入、不下单、不输出业务日志；出现这类代码即说明职责越界
 */
@SpringBootApplication
public class OrderApp {

    /**
     * 程序入口方法（JVM 调用的第一个方法）
     *
     * <p>作用：把 {@code OrderApp} 交给 Spring 启动容器。容器启动完成后依次创建各 bean，
     * 并回调 {@link ConsoleRunner#run(String...)}，程序此后停在等待键盘输入的状态
     *
     * <p>调用者：JVM（由 {@code mvn spring-boot:run} 间接拉起），不被业务代码调用
     *
     * @param args 命令行参数，本阶段不使用
     */
    public static void main(String[] args) {
        // TODO 待实现：启动 Spring 容器
    }
}
