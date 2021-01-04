package com.gt.example.reactivedemo;

import io.rsocket.frame.decoder.PayloadDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.net.URI;

@Disabled("Run this manually when application is running")
class RunningApplicationTests extends BaseDemoTests {

    int serverPort = 8080;

    @BeforeEach
    void setup() {
        RSocketRequester.Builder builder = RSocketRequester.builder();

        requester = builder
                .rsocketStrategies( b -> {
                    b.decoder(new Jackson2JsonDecoder());
                    b.encoder(new Jackson2JsonEncoder());
                })
                .rsocketConnector( connector -> connector
                        .dataMimeType("application/json")
                        .payloadDecoder(PayloadDecoder.ZERO_COPY))
                .connectWebSocket(URI.create("ws://localhost:" + serverPort + "/rs")).block()
        ;
    }
}
