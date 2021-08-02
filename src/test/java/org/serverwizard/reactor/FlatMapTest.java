package org.serverwizard.reactor;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FlatMapTest {

    @Test
    void flatMapTest() {
        Flux<String> inFlux = Flux.just("serverwizard", "hongjongwan").log();
        Flux<String> outFlux = inFlux.flatMap(s -> Flux.just(s.toUpperCase()));

        List<String> output = Lists.newArrayList();
        outFlux.subscribe(output::add);

        assertThat(output).isEqualTo(List.of("SERVERWIZARD", "HONGJONGWAN"));
    }

    @Test
    void flatMapTest2() {
        Flux<String> inFlux = Flux.fromIterable(List.of("serverwizard", "hongjongwan"));
        Flux<String> outFlux = inFlux.flatMap(s -> Flux.just(s.toUpperCase()));

        StepVerifier.create(outFlux)
                .expectNext("SERVERWIZARD", "HONGJONGWAN")
                .expectComplete()
                .verify();
    }

    @Test
    void mergeTest() {
        Mono<String> inMono1 = Mono.just("serverwizard");
        Mono<String> inMono2 = Mono.just("hongjongwan");

        Flux<String> outFlux = Flux.merge(inMono1, inMono2).log();

        StepVerifier.create(outFlux)
                .expectNext("serverwizard")
                .expectNext("hongjongwan")
                .verifyComplete();
    }

    @Test
    void mergeTest2() {
        Flux<Long> inFlux1 = Flux.interval(Duration.from(Duration.ofMillis(1000))).take(4);
        Flux<Long> inFlux2 = Flux.just(1L, 2L, 3L, 4L, 5L);

        Flux<Long> outFlux = Flux.merge(inFlux1, inFlux2).log();

        StepVerifier.create(outFlux)
                .thenRequest(1)
                .expectNext(1L)
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void mergeSequentialTest() {
        Flux<Long> inFlux1 = Flux.interval(Duration.from(Duration.ofMillis(1000))).take(4);
        Flux<Long> inFlux2 = Flux.just(100L, 101L, 102L, 103L);

        Flux<Long> outFlux = Flux.mergeSequential(inFlux1, inFlux2).log();

        StepVerifier.create(outFlux)
                .expectNextCount(8)
                .verifyComplete();
    }

    @Test
    void concatTest() {
        Mono<String> inMono1 = Mono.just("serverwizard");
        Mono<String> inMono2 = Mono.just("hongjongwan");

        Flux<String> outFlux = Flux.concat(inMono1, inMono2).log();

        StepVerifier.create(outFlux)
                .expectNext("serverwizard")
                .expectNext("hongjongwan")
                .verifyComplete();
    }

    @Test
    void subscribeTest() {
        Flux<String> source = Flux.just("serverwizard", "hongjongwan").log();

        source.subscribe(System.out::println);
    }

    @Test
    void doOnSubscribeTest() {
        Flux<Integer> source = Flux.range(1, 3).log()
                .doOnSubscribe(s -> System.out.println("do on subscribe"))
                .doOnRequest(s -> System.out.println("do on request"));

        source.subscribe(System.out::println);
    }
}
