package br.gabriel.reactor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {
    @Test
    public void shouldTestMonoSubscriber() {
        String name = "Gabriel";
        Mono<String> mono = Mono.just(name).log();
        
        mono.subscribe();
    
        StepVerifier.create(mono)
                    .expectNext(name)
                    .verifyComplete();
    }
    
    @Test
    public void shouldTestMonoSubscriberConsumer() {
        String name = "Gabriel";
        Mono<String> mono = Mono.just(name).log();
        
        mono.subscribe(s -> log.info("Value: {}", s));
        
        StepVerifier.create(mono)
                    .expectNext(name)
                    .verifyComplete();
    }
    
    @Test
    public void shouldTestMonoSubscriberConsumerError() {
        String name = "Gabriel";
        Mono<String> mono = Mono.just(name)
            .map(s -> { throw new RuntimeException("Testing Mono with error"); });
        
        mono.subscribe(s -> log.info("Value: {}", s), Throwable::printStackTrace);
        
        StepVerifier.create(mono)
                    .expectError(RuntimeException.class)
                    .verify();
    }
    
    @Test
    public void shouldTestMonoSubscriberConsumerComplete() {
        String name = "Gabriel";
        Mono<String> mono = Mono.just(name)
            .map(String::toUpperCase);
        
        mono.subscribe(
            s -> log.info("Value: {}", s),
            Throwable::printStackTrace,
            () -> log.info("Finished successfully")
        );
        
        StepVerifier.create(mono)
                    .expectNext(name.toUpperCase())
                    .verifyComplete();
    }
    
    @Test
    public void shouldTestMonoSubscriberConsumerSubscription() {
        String name = "Gabriel";
        Mono<String> mono = Mono.just(name)
                                .map(String::toUpperCase);
        
        mono.subscribe(
            s -> log.info("Value: {}", s),
            Throwable::printStackTrace,
            () -> log.info("Finished successfully"),
            Subscription::cancel
        );
        
        StepVerifier.create(mono)
                    .expectNext(name.toUpperCase())
                    .verifyComplete();
    }
}
