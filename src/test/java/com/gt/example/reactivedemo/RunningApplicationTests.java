package com.gt.example.reactivedemo;

import io.rsocket.frame.decoder.PayloadDecoder;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

class RunningApplicationTests {

    int serverPort = 8080;
    RSocketRequester requester;

    @BeforeEach
    void setup() {
        RSocketRequester.Builder builder = RSocketRequester.builder();

        requester = builder
//                .dataMimeType(MimeType.valueOf("application/cbor"))
                .rsocketStrategies( b -> {
                    b.decoder(new Jackson2JsonDecoder());
                    b.encoder(new Jackson2JsonEncoder());
                })
                .rsocketConnector( connector -> {
                    connector
                            .dataMimeType("application/json")
                            .payloadDecoder(PayloadDecoder.ZERO_COPY)
                    ;
                })
//                .websocket(URI.create("ws://localhost:" + serverPort + "/rs"))
                .connectWebSocket(URI.create("ws://localhost:" + serverPort + "/rs")).block()
        ;
    }

    @Test
    void testRequest() {
        System.out.println("--- testRequest");
        requester.route("ping").data("hello").retrieveMono(String.class).subscribe(s -> System.out.println("s: " + s));
    }

    @Test
    void testChannel() {
        System.out.println("--- testChannel");
        Flux<String> data = Flux.fromIterable(Lists.list("a", "b"));
        requester.route("channel.{test}", "haha").data(data).retrieveFlux(String.class).subscribe(s -> System.out.println("testChannel: " + s));
    }

    @Test
    void testEmptyChannel() {
        System.out.println("--- testEmptyChannel");
        Flux<String> data = Flux.empty();
        requester.route("channel.{test}", "haha-empty").data(data).retrieveFlux(String.class).subscribe(s -> System.out.println("testChannel: " + s));
    }

    @Test
    void testEmptyChannelWithDefault() {
        System.out.println("--- testEmptyChannel");
        Flux<String> data = Flux.<String>empty().defaultIfEmpty("default");
        requester.route("channel.{test}", "haha-empty").data(data).retrieveFlux(String.class).subscribe(s -> System.out.println("testChannel: " + s));
    }

    @Test
    void testEmptyObjectChannel() {
        System.out.println("--- testObjectChannel");
        Flux<Instant> data = Flux.empty();
//        data = data.defaultIfEmpty(Instant.now());
        requester.route("objectChannel.{test}", "test-empty")
                .data(data)
                .retrieveFlux(Instant.class)
                .onErrorContinue((t, v) -> {
                    System.out.println("client onErrorContinue: " + t.getMessage());
//                    t.printStackTrace();
                    System.out.println("v = " + v);
                } )
                .doOnComplete(() -> System.out.println("testObjectChannel complete"))
                .subscribe(s -> System.out.println("testObjectChannel: " + s));
    }

    @Test
    void testRequestStream() {
        System.out.println("--- testRequestStream");
        Mono<Integer> data = Mono.just(0);
        requester.route("requestStream").data(data).retrieveFlux(Instant.class)
                .defaultIfEmpty(Instant.now())
                .doOnComplete(() -> System.out.println("doOnComplete"))
                .subscribe(s -> System.out.println("testRequestStream: " + s));
    }
}
