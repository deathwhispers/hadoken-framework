package com.hadoken.framework.websocket.core.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/6/6 9:53
 */
@Component
public class HadokenWebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(HadokenWebSocketEventListener.class);

    @EventListener
    public void handleConnectListener(SessionConnectedEvent event) {
        logger.info("[ws-connected] socket connect: {}", event.getMessage());
    }

    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
        logger.info("[ws-disconnect] socket disconnect: {}", event.getMessage());
    }
}
