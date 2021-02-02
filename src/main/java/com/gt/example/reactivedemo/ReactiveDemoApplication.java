package com.gt.example.reactivedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import reactor.netty.Connection;
import reactor.netty.ConnectionObserver;

@SpringBootApplication
public class ReactiveDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveDemoApplication.class, args);
    }

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return httpServer -> {
            var observer = new ConnectionObserver() {
                @Override
                public void onStateChange(Connection connection, State newState) {
//                    System.out.println(connection);
                    System.out.println("ConnectionObserver.onStateChange: " + newState);
                }
            };
            return httpServer
                    .observe(observer)
                    .childObserve(observer);
        };
    }
}
