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
    }
    
    @Test
    public void shouldTestDoOnMethod() {
        String name = "Gabriel";
        Mono<Object> mono = Mono
            .just(name)
            .log()
            .map(String::toUpperCase)
            .doOnSubscribe(subscription -> log.info("SUBSCRIBED!"))
            .doOnRequest(l -> log.info("Request received, starting doing something..."))
            .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
            .flatMap(s -> Mono.empty())
            .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
            .doOnSuccess(s -> log.info("Completely successfully. Executing doOnSuccess {}", s));
        
        mono.subscribe();
    }
    
    @Test
    public void shouldTestDoOnError() {
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Illegal argument exception"))
            .doOnError(throwable -> log.info("Error: {}", throwable.getMessage()))
            .log();
        
        StepVerifier.create(mono)
                    .expectError(IllegalArgumentException.class)
                    .verify();
    }
    
    @Test
    public void shouldTestDoOnErrorResume() {
        String name = "Gabriel";
        
        Mono<Object> mono = Mono
            .error(new IllegalArgumentException("Illegal argument exception"))
            .onErrorResume(throwable -> {
                log.info("Error: {}", throwable.getMessage());
                return Mono.just(name);
            })
            .log();
        
        StepVerifier
            .create(mono)
            .expectNext(name)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestDoOnErrorReturn() {
        String name = "Gabriel";
        
        Mono<Object> mono = Mono
            .error(new IllegalArgumentException("Illegal argument exception"))
            .onErrorReturn("EMPTY")
            .onErrorResume(throwable -> {
                log.info("Error: {}", throwable.getMessage());
                return Mono.just(name);
            })
            .log();
        
        StepVerifier
            .create(mono)
            .expectNext("EMPTY")
            .verifyComplete();
    }
}
