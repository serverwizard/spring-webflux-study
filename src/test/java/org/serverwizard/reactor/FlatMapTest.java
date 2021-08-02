package org.serverwizard.reactor;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    void concatTest() {
        Mono<String> inMono1 = Mono.just("serverwizard");
        Mono<String> inMono2 = Mono.just("hongjongwan");

        Flux<String> outFlux = Flux.concat(inMono1, inMono2).log();

        StepVerifier.create(outFlux)
                .expectNext("serverwizard")
                .expectNext("hongjongwan")
                .verifyComplete();
    }
}
