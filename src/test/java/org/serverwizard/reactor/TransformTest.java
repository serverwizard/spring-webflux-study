package org.serverwizard.reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

class TransformTest {

    @DisplayName("Mono에서 map operator 사용법을 확인한다.")
    @Test
    void monoMapTest() {
        StepVerifier.create(Mono.just(new User("serverwizard"))
                .map(u -> new User(u.getName().toUpperCase())))
                .expectNext(new User("SERVERWIZARD"))
                .verifyComplete();
    }

    @DisplayName("Flux에서 map operator 사용법을 확인한다.")
    @Test
    void fluxMapTest() {
        StepVerifier.create(Flux.just(new User("serverwizard"))
                .map(u -> new User(u.getName().toUpperCase())))
                .expectNext(new User("SERVERWIZARD"))
                .verifyComplete();
    }

    @DisplayName("Flux에서 flatMap operator 사용법을 확인한다.")
    @Test
    void fluxFlatMapTest() {
        StepVerifier.create(Flux.just(new User("serverwizard"), new User("jwh"))
                .flatMap(u -> Flux.just(new User(u.getName().toUpperCase())))
                .log())
                .expectNext(new User("SERVERWIZARD"))
                .expectNext(new User("JWH"))
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

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
