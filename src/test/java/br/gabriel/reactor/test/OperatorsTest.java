package br.gabriel.reactor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class OperatorsTest {
    @Test
    public void shouldTestSimpleSubscribeOn() {
        Flux<Integer> flux = Flux
            .range(1, 3)
            .map(i -> {
                log.info("Map #1 | Number {} | Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .subscribeOn(Schedulers.elastic())
            .map(i -> {
                log.info("Map #1 | Number {} | Thread {}", i, Thread.currentThread().getName());
                return i;
            });
    
        StepVerifier
            .create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestSimplePublishOn() {
        Flux<Integer> flux = Flux
            .range(1, 3)
            .map(i -> {
                log.info("Map #1 | Number {} | Thread {}", i, Thread.currentThread().getName());
                return i;
            })
            .publishOn(Schedulers.elastic())
            .map(i -> {
                log.info("Map #1 | Number {} | Thread {}", i, Thread.currentThread().getName());
                return i;
            });
        
        StepVerifier
            .create(flux)
            .expectSubscription()
            .expectNext(1, 2, 3)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestSubscribeOnIO() throws Exception {
        Mono<List<String>> file = Mono.fromCallable(() -> Files.readAllLines(Path.of("text-file")))
            .log()
            .subscribeOn(Schedulers.boundedElastic());
        
        Thread.sleep(2000);
        
        StepVerifier
            .create(file)
            .expectSubscription()
            .thenConsumeWhile(i -> {
                Assertions.assertFalse(i.isEmpty());
                log.info("{}", i.size());
                return true;
            })
            .verifyComplete();
    }
}
