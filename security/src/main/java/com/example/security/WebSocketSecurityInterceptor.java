package com.example.security;

import java.util.Optional;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WebSocketSecurityInterceptor implements ChannelInterceptor {
    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String headerAuth = accessor.getFirstNativeHeader("Authorization");

            Optional<String> jwt = jwtUtils.parseJwt(headerAuth);

            if (!jwt.isPresent() || !jwtUtils.validateJwtToken(jwt.get())) {
                throw new MessageDeliveryException("Invalid JWT token");
            }

            String username = jwtUtils.getUsernameFromJwtToken(jwt.get());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return message;
    }
}
