package com.li.order;

import org.springframework.stereotype.Service;

/**
 * 下单业务层：表示「下单」这件事本身
 *
 * <p>它是链路的中转站——上游是控制台输入，下游是商家侧。本类不读输入、
 * 不决定怎么通知商家，只负责把「用户要下单」这个事实转交出去
 *
 * <p>本阶段信号不携带任何内容：没有订单号、没有商品、没有金额、没有状态，也不落库
 */
@Service
public class OrderService {

    /** 商家侧接收者，由 Spring 通过构造器注入 */
    private final MerchantNotifier merchantNotifier;

    /**
     * 构造器注入
     *
     * <p>作用：由 Spring 把容器里的 {@link MerchantNotifier} 交给本类，
     * 使「下单」与「通知商家」两件事之间只保留一层方法调用关系
     *
     * <p>调用者：Spring 容器创建 bean 时
     *
     * @param merchantNotifier 商家侧通知器，不能为 null
     */
    public OrderService(MerchantNotifier merchantNotifier) {
        this.merchantNotifier = merchantNotifier;
    }

    /**
     * 完成一次下单
     *
     * <p>作用：「下单」动作的唯一入口。被调用即代表客户已经下单，
     * 并把信号沿调用链交给商家侧 {@link MerchantNotifier#notifyOrderReceived()}
     *
     * <p>调用者：{@link ConsoleRunner#run(String...)}
     *
     * <p>本阶段不做：生成订单对象、校验参数、写库、状态流转、返回处理结果
     */
    public void placeOrder() {
        // TODO 待实现：把下单信号交给商家侧
    }
}
