package org.serverwizard.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class ReactorApplication {

    public static void main(String[] args) throws InterruptedException {
        // data source가 String 인 경우
//        Flux<String> flux = Flux.just("hello world");
//        flux.map(s -> s + " serverwizard")
//                .subscribe(System.out::println);
//
//        System.out.println("먼저 찍히나?");

        // data source가 List 인 경우
//        Flux.fromIterable(List.of(1L, 2L, 3L))
//                .log()
//                .map(d -> d * 2)
//                .take(3)
//                .subscribe(System.out::println);
//
//        System.out.println("먼저 찍히나?");

        // 에러 발생시킨 경우
//        Flux.error(new IllegalArgumentException("에러"))
//                .log()
//                .doOnError(System.out::println)
//                .subscribe();


        // asynchronous, non-blocking 되는지 확인
        Flux.interval(Duration.ofMillis(300))
                .take(3)
                .subscribe(System.out::println);

        System.out.println("먼저 찍히나?");

        Thread.sleep(5000);
    }
}
