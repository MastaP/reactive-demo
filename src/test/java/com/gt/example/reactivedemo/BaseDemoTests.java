package com.gt.example.reactivedemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Arrays;

public abstract class BaseDemoTests {

    RSocketRequester requester;

    @Test
    void testSendStreamGetFluxFromMono() {
        System.out.println("--- testReceiveStream");
        Flux<Void> result = requester
                .route("receive-stream")
                .data(Flux.fromIterable(Arrays.asList("1", "2")))
                .retrieveFlux(Void.class); // NB! Server actually returns Mono<Void> and this works
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void testSendStreamAndGetMono() {
        System.out.println("--- testReceiveStream");
        // NB! This should pass, but Spring disagrees
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Mono<Void> result = requester
                    .route("receive-stream")
                    .data(Flux.fromIterable(Arrays.asList("1", "2")))
                    .retrieveMono(Void.class);
            StepVerifier
                    .create(result)
                    .verifyComplete();
        });
    }

    @Test
    void testRequestEmptyStream() {
        System.out.println("--- testRequestEmptyStream");
        Flux<String> result = requester
                .route("request-stream")
                .retrieveFlux(String.class);
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void testRequestEmptyObjectStream() {
        System.out.println("--- testRequestEmptyObjectStream");
        Flux<Instant> result = requester
                .route("request-object-stream")
                .retrieveFlux(Instant.class);
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void testEmptyChannel() {
        System.out.println("--- testEmptyChannel");
        Flux<String> data = Flux.empty();
        Flux<String> result = requester
                .route("channel.{test}", "route-var1")
                .data(data)
                .retrieveFlux(String.class);
        StepVerifier
                .create(result)
                .verifyComplete();
    }

    @Test
    void testEmptyObjectChannel() {
        System.out.println("--- testObjectChannel");
        Flux<Instant> data = Flux.empty();
        Flux<Instant> result = requester.route("objectChannel.{test}", "route-var1")
                .data(data)
                .retrieveFlux(Instant.class)
                .doOnComplete(() -> System.out.println("testObjectChannel complete"));
        StepVerifier
                .create(result)
                .verifyComplete();
    }
}
