package cz.diamo.vratnice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/rz-vozidla"); // Enable a simple memory-based message broker
        config.setApplicationDestinationPrefixes("/app"); // Set the prefix for messages handled by methods annotated with @MessageMapping
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws-vratnice") // WebSocket endpoint for clients to connect to
                .setAllowedOrigins("http://localhost:4206") // Allow all origins (you might want to restrict this in production)
                .withSockJS(); // Enable SockJS fallback options
    }
}
