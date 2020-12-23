package com.gt.example.reactivedemo;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
public class RSocketServerHandler {

    @MessageMapping("ping")
    Mono<String> rsocketPing(@Payload String testString) {
        System.out.println("testString: " + testString);
        return Mono.just("pong " + testString);
    }

    @MessageMapping("channel.{test}")
    Flux<String> rsocketChannel(@DestinationVariable String test, Flux<String> flux) {
        System.out.println("rsocketChannel: " + test);
        return flux
                .doOnNext(s -> System.out.println("rsocketChannel.next: " + s))
//                .defaultIfEmpty("yo-default")
                .map(s -> "channel: " + s);
    }

    @MessageMapping("objectChannel.{test}")
    Flux<Instant> rsocketObjectChannel(@DestinationVariable String test, Flux<Instant> flux) {
        System.out.println("objectChannel: " + test);
        return flux
//                .defaultIfEmpty(Instant.now())
//                .switchIfEmpty(Flux.empty())
                .map(i -> i.plus(1, DAYS))
                .doOnNext(s -> System.out.println("rsocketObjectChannel.next: " + s))
                .onErrorContinue((t, v) -> {
                    System.out.println("server onErrorContinue test=" + test);
                    System.out.println("server onErrorContinue: " + t.getMessage());
//                    t.printStackTrace();
                    System.out.println("v = " + v);
                } )
                .doOnComplete(() -> System.out.println("rsocketObjectChannel complete"));
    }

    @MessageMapping("requestStream")
    Flux<Instant> requestStream(Mono<Integer> mono) {
        System.out.println("requestStream");
        return mono.flatMapMany(i -> Flux.fromStream(IntStream.range(0, i).mapToObj(v -> Instant.now().plus(v, DAYS))));
    }
}
