package com.hadoken.framework.websocket.config;

import com.hadoken.framework.websocket.core.interceptor.HadokenWebSocketHandleInterceptor;
import com.hadoken.framework.websocket.core.message.DefaultWebSocketSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import jakarta.annotation.Resource;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2022/4/25 15:15
 */
@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
@EnableWebSocketMessageBroker
public class HadokenWebSocketAutoConfiguration implements WebSocketMessageBrokerConfigurer {

    @Resource
    private WebSocketProperties webSocketProperties;

    @Bean
    public DefaultWebSocketSender defaultWebSocketSender(SimpMessagingTemplate simpMessagingTemplate) {
        return new DefaultWebSocketSender(simpMessagingTemplate);
    }

    // 添加这个Endpoint，这样在网页中就可以通过websocket连接上服务,也就是我们配置websocket的服务地址,并且可以指定是否使用socketjs
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProperties.getEndpoint())
                .setAllowedOrigins("*")
                .withSockJS();

        registry.addEndpoint(webSocketProperties.getEndpoint())
                .setAllowedOrigins("*");
    }

    // 配置发送与接收的消息参数，可以指定消息字节大小，缓存大小，发送超时时间
/*    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    }*/

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new HadokenWebSocketHandleInterceptor());
    }

    // 设置输出消息通道的线程数，默认线程为1，可以自己自定义线程数，最大线程数，线程存活时间
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(1).maxPoolSize(1);
    }

    // 自定义控制器方法的参数类型，有兴趣可以百度google HandlerMethodArgumentResolver这个的用法
/*    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    }*/

    // 自定义控制器方法返回值类型，有兴趣可以百度google HandlerMethodReturnValueHandler这个的用法
/*    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }*/

    // 添加自定义的消息转换器，spring 提供多种默认的消息转换器，
    // 返回false,不会添加消息转换器，
    // 返回true，会添加默认的消息转换器，
    // 当然也可以把自己写的消息转换器添加到转换链中
/*    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return false;
    }*/

    // 配置消息代理，哪种路径的消息会进行代理处理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix(webSocketProperties.getUserDestinationPrefix())
                .setApplicationDestinationPrefixes(webSocketProperties.getApplicationDestinationPrefixes())
                // 允许的消息前缀
                .enableSimpleBroker(webSocketProperties.getTopicDestinationPrefix())
                .setHeartbeatValue(new long[]{webSocketProperties.getHeartBeat().getServer(), webSocketProperties.getHeartBeat().getClient()})
                .setTaskScheduler(wsTaskScheduler());
    }

    @Bean
    public TaskScheduler wsTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(webSocketProperties.getWsTaskScheduler().getPoolSize());
        threadPoolTaskScheduler.setThreadNamePrefix(webSocketProperties.getWsTaskScheduler().getThreadNamePrefix());
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
