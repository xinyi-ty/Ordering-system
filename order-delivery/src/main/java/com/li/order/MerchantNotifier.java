package com.li.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 商家侧：下单信号链路的终点
 *
 * <p>收到信号后只在服务端日志里输出「用户下单了」，用于证明信号已经从客户端
 * 走到了服务端。本类不关心信号从哪来，也不做任何后续处理（不接单、不回执、不发货）
 */
@Component
public class MerchantNotifier {

    /** 日志对象，唯一职责是输出「用户下单了」这一行 */
    private static final Logger log = LoggerFactory.getLogger(MerchantNotifier.class);

    /**
     * 接收下单信号
     *
     * <p>作用：整条链路最后一环。被 {@link OrderService#placeOrder()} 调用后，
     * 输出一行「用户下单了」的日志，作为本阶段的验收依据
     *
     * <p>调用者：{@link OrderService#placeOrder()}
     *
     * <p>输出要求：每次调用输出且只输出一次「用户下单了」，不附加订单号等业务内容，
     * 不输出其他提示信息
     */
    public void notifyOrderReceived() {
        // TODO 待实现：输出「用户下单了」
    }
}
