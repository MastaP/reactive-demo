package com.gt.example.reactivedemo;

import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.net.URI;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@RequiredArgsConstructor
class ReactiveDemoApplicationTests extends BaseDemoTests {

    @LocalServerPort int serverPort;

    @BeforeEach
    void setup(@Autowired RSocketRequester.Builder builder) {
        requester = builder
                .rsocketConnector( connector -> connector
                        .dataMimeType("application/json")
                        .payloadDecoder(PayloadDecoder.ZERO_COPY))
                .connectWebSocket(URI.create("ws://localhost:" + serverPort + "/rs")).block();
    }
}
