package com.hadoken.framework.websocket.core.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/6/6 9:53
 */
@Slf4j
@Component
public class HadokenWebSocketEventListener {

    @EventListener
    public void handleConnectListener(SessionConnectedEvent event) {
        log.info("[ws-connected] socket connect: {}", event.getMessage());
    }

    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
        log.info("[ws-disconnect] socket disconnect: {}", event.getMessage());
    }
}
