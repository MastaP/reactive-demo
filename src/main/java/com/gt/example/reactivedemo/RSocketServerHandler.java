package com.gt.example.reactivedemo;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
public class RSocketServerHandler {

    @MessageMapping("request-response.{first}.{second}")
    Mono<Void> rsocketRequestResponse(
            @DestinationVariable String first,
            @DestinationVariable String second,
            Mono<String> data) {
        System.out.println("request-response metadata: first = " + first + ", second = " + second);
        return data
                .doOnNext(s -> System.out.println("request-response: " + s))
                .then();
    }

    @MessageMapping("receive-stream")
    Mono<Void> rsocketReceiveStream(Flux<String> data) {
        return data
                .doOnNext(s -> System.out.println("receive-stream: " + s))
                .then();
    }

    @MessageMapping("request-stream")
    Flux<String> rsocketStream() {
        return Flux.empty();
    }

    @MessageMapping("request-object-stream")
    Flux<Instant> rsocketObjectStream() {
        return Flux.empty();
    }

    @MessageMapping("channel.{test}")
    Flux<String> rsocketChannel(@DestinationVariable String test, Flux<String> flux) {
        System.out.println("rsocketChannel: " + test);
        return flux
                .doOnNext(s -> System.out.println("rsocketChannel.next: '" + s + "'"))
                .map(s -> "channel: " + s);
    }

    @MessageMapping("objectChannel.{test}")
    Flux<Instant> rsocketObjectChannel(@DestinationVariable String test, Flux<Instant> flux) {
        System.out.println("objectChannel: " + test);
        return flux
                .map(i -> i.plus(1, DAYS))
                .doOnNext(s -> System.out.println("rsocketObjectChannel.next: " + s))
                .doOnComplete(() -> System.out.println("rsocketObjectChannel complete"));
    }
}
