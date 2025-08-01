package com.hadoken.framework.websocket.core.interceptor;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/6/6 9:54
 */
@SuppressWarnings("all")
public class HadokenWebSocketHandleInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HadokenWebSocketHandleInterceptor.class);

    public static final ConcurrentHashMap<String, Integer> SUBSCRIBE_COUNT = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, String> SUBSCRIBE_SESSION = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        //1、判断是否首次连接
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            //2、判断token
            String token = accessor.getFirstNativeHeader("Authorization");
            if (StrUtil.isNotBlank(token)) {
                logger.info("stompClient-token:{}", token);
/*                if (loginUser != null) {
                    //如果存在用户信息，将用户名赋值，后期发送时，可以指定用户名即可发送到对应用户
                    Principal principal = () -> loginUser.getUsername();
                    accessor.setUser(principal);
                    return message;
                }*/
            }
            return message;
        }
        //不是首次连接，已经登陆成功
        return message;
    }

    /**
     * 在消息发送后立刻调用，boolean值参数表示该调用的返回值
     *
     * @param message        /
     * @param messageChannel /
     * @param b              /
     */
    @Override
    public void postSend(Message<?> message, MessageChannel messageChannel, boolean b) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        /*
         * 拿到消息头对象后，我们可以做一系列业务操作
         * 1. 通过getSessionAttributes()方法获取到websocketSession，
         *    就可以取到我们在WebSocketHandshakeInterceptor拦截器中存在session中的信息
         * 2. 我们也可以获取到当前连接的状态，做一些统计，例如统计在线人数，或者缓存在线人数对应的令牌，方便后续业务调用
         */
//        HttpSession httpSession = (HttpSession) ac.cessorgetSessionAttributes().get("HTTP_SESSION");

        // 这里只是单纯的打印，可以根据项目的实际情况做业务处理
//        logger.info("postSend 中获取httpSession key：" + httpSession.getId());

        // 忽略心跳消息等非STOMP消息
        if (accessor.getCommand() == null) {
            return;
        }
        // 根据连接状态做处理，这里也只是打印了下，可以根据实际场景，对上线，下线，首次成功连接做处理
        logger.debug("command: {}", accessor.getCommand());
        switch (accessor.getCommand()) {
            case CONNECT:
                // 首次连接
                logger.info(" 首次连接");
                break;
            case CONNECTED:
                // 连接中
                break;
            case DISCONNECT:
                // 下线
                logger.info(" 下线");
                break;
            case SUBSCRIBE:
                String subscribeTopic = accessor.getDestination();
                // 存储session，方便取消订阅时拿到对应topic
                String subscribeSessionId = accessor.getSessionId();
                SUBSCRIBE_SESSION.put(subscribeSessionId, subscribeTopic);
                int increaseCount = null != SUBSCRIBE_COUNT.get(subscribeSessionId) ? SUBSCRIBE_COUNT.get(subscribeSessionId) : 0;
                SUBSCRIBE_COUNT.put(subscribeSessionId, ++increaseCount);
                break;
            case UNSUBSCRIBE:
                String unsubscribeSessionId = accessor.getSessionId();
                // 根据session获取对应topic
                String unsubscribeTopic = SUBSCRIBE_SESSION.get(unsubscribeSessionId);
                int decreaseCount = (int) SUBSCRIBE_COUNT.get(unsubscribeSessionId);
                if (decreaseCount > 0) {
                    SUBSCRIBE_COUNT.put(unsubscribeSessionId, --decreaseCount);
                } else {
                    SUBSCRIBE_COUNT.remove(unsubscribeSessionId);
                }
                break;
            default:
                break;
        }
    }
}
