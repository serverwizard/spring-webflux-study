package org.serverwizard.reactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static reactor.core.scheduler.Schedulers.parallel;

public class PublisherTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("비동기처리 환경에서 ObjectMapper 사용")
    @Test
    void objectMapperTest() {
        String bodyAsJson = "{\"name\": \"hongjongwan\", \"job\": \"student\"}";
        Mono.fromCallable(() -> {
            Thread.sleep(3000);
            return objectMapper.readTree(bodyAsJson);
        }).log()
                .map(postBodyJson -> {
                    System.out.println("1. " + Thread.currentThread().getName());
                    assertThat(postBodyJson.findValue("job").asText()).isEqualTo("student");
                    return postBodyJson;
                }).subscribe();

        System.out.println("2. " + Thread.currentThread().getName());
    }

    @DisplayName("operators 테스트1")
    @Test
    void flatMapTest() {
        Flux.just("hongjongwan", "serverwizard")
                .flatMap(s -> Flux.just(s.toUpperCase()))
                .subscribe(value -> {
                    System.out.println("1. " + Thread.currentThread().getName());
                    System.out.println(value);
                });

        System.out.println("2. " + Thread.currentThread().getName());
    }

    @DisplayName("operators 테스트2")
    @Test
    void flatMapTest2() {
        Flux.just("hongjongwan", "serverwizard")
                .flatMap(s -> Flux.just(s.toUpperCase()))
                .subscribe(value -> {
                    System.out.println("1. " + Thread.currentThread().getName());
                    System.out.println(value);
                });

        System.out.println("2. " + Thread.currentThread().getName());
    }

    @Test
    void flatMapTest3() {
        Flux.just("a", "b", "c", "d", "e", "f", "g")
                .log()
                .window(3)
                .flatMap(l -> l.map(this::toUpperCaseAndToLowerCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    private List<String> toUpperCaseAndToLowerCase(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of(s.toUpperCase(), s.toLowerCase());
    }

    @DisplayName("리액터는 비동기 실행을 강제하지 않음")
    @Test
    void synchronousTest() {
        Flux.range(1, 3)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName() + "1");
                    System.out.println("map " + i + " to " + (i + 2));
                    return i + 2;
                })
                .flatMap(i -> {
                    System.out.println(Thread.currentThread().getName() + "2");
                    System.out.println("flatMap " + i + " to " + "Flux.range(" + 1 + "," + i + ")");
                    return Flux.range(1, i);
                })
                .subscribe(i -> {
                    System.out.println(Thread.currentThread().getName() + "3");
                    System.out.println("next " + i);
                });
    }

    @DisplayName("리액터에서 비동기 처리될 수 있도록 변경")
    @Test
    void asynchronousTest() throws InterruptedException {
        Flux.range(1, 3)
                .map(i -> {
                    System.out.println(Thread.currentThread().getName() + "1");
                    System.out.println("map " + i + " to " + (i + 2));
                    return i + 2;
                })
                .publishOn(Schedulers.parallel())
                .flatMap(i -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("flatMap " + i + " to " + "Flux.range(" + 1 + "," + i + ")");
                    return Flux.range(1, i);
                })
                .publishOn(Schedulers.parallel())
                .subscribe(i -> {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("next " + i);
                });

        System.out.println("End... " + Thread.currentThread().getName());
        Thread.sleep(3000);
    }

    @DisplayName("sequence는 바로 signal을 발생시키지 않음")
    @Test
    void sequenceTest() {
        Flux<Integer> seq = Flux.just(1, 2, 3)
                .doOnNext(i -> System.out.println("doOnNext: " + i));

        System.out.println("시퀀스 생성");
        seq.subscribe(i -> System.out.println("Received: " + i));
    }

    @DisplayName("cold sequence 확인")
    @Test
    void sequenceTest2() {
        Flux<Integer> seq = Flux.just(1, 2, 3);
        seq.subscribe(v -> System.out.println("구독1: " + v));
        seq.subscribe(v -> System.out.println("구독2: " + v));
    }

    @DisplayName("커스텀 Subscriber 테스트")
    @Test
    void sequenceTest3() {
        Flux<Integer> seq = Flux.just(1, 2, 3);
        seq.subscribe(new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Subscriber.onSubscribe");
                this.subscription = s;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("Subscriber.onNext: " + i);
//                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Subscriber.onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber.onComplete");
            }
        });
    }

    @DisplayName("Flux.generate 메서드로 Flux만들기")
    @Test
    void generateTest() {
        Consumer<SynchronousSink<Integer>> randGen = new Consumer<>() {
            private int emitCount = 0;
            private Random rand = new Random();

            @Override
            public void accept(SynchronousSink<Integer> sink) {
                emitCount++;
                int data = rand.nextInt(100) + 1; // 1~100 사이 임의의 정수
                System.out.println("Generator sink next " + data);
                sink.next(data);
                if (emitCount == 10) {
                    System.out.println("Generator sink complete");
                    sink.complete();
                }
            }
        };

        Flux<Integer> seq = Flux.generate(randGen);

        seq.subscribe(new BaseSubscriber<>() {
            private int receiveCount = 0;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("Subscriber#onSubscribe");
                System.out.println("Subscriber request first 3 items");
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Subscriber#onNext: " + value);
                receiveCount++;
                if (receiveCount % 3 == 0) {
                    System.out.println("Subscriber request next 3 items");
                    request(3);
                }
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Subscriber#onComplete");
            }
        });
    }

    @DisplayName("Mono 테스트")
    @Test
    void monoTest() {
//        Mono.justOrEmpty(null).log().subscribe();
//        Mono.justOrEmpty(1).log().subscribe();
        Mono.justOrEmpty(Optional.empty()).log().subscribe();
        Mono.justOrEmpty(Optional.of(1)).log().subscribe();
    }

    @DisplayName("flux generate 테스트")
    @Test
    void fluxGenerateTest() {
        Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                }).log()
                .subscribe();
    }
}
