package org.serverwizard.reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ReactorApplicationTests {

    @DisplayName("파라미터가 String 인 경우, 값을 검증한다.")
    @Test
    void fluxTest1() {
        StepVerifier.create(Flux.just("foo", "bar"))
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete();
    }

    @DisplayName("Flux 처리 중 발생한 에러를 검증한다.")
    @Test
    void fluxErrorTest() {
        StepVerifier.create(Flux.error(new RuntimeException()))
                .verifyError(RuntimeException.class);
    }

    @DisplayName("파라미터가 객체인 경우, 각 객체의 프로퍼티 값을 검증한다.")
    @Test
    void fluxTest2() {
        StepVerifier.create(Flux.just(new User("serverwizard"), new User("jwh")))
                .assertNext(u -> assertThat(u.getName()).isEqualTo("serverwizard"))
                .assertNext(u -> assertThat(u.getName()).isEqualTo("jwh"))
                .verifyComplete();
    }

    @DisplayName("기대한 파라미터 수가 넘어오는지를 검증한다.")
    @Test
    void fluxTest3() {
        StepVerifier.create(Flux.interval(Duration.ofSeconds(1)).take(10))
                .expectNextCount(10)
                .verifyComplete();
    }

    @DisplayName("withVirtualTime를 이용해서 실제 오래걸리는 작업을 빠르게 검증한다.")
    @Test
    void fluxTest4() {
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(1)))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(1)
                .verifyComplete();
    }

    private class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
