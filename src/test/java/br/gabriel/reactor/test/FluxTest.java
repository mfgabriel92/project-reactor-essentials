package br.gabriel.reactor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {
    @Test
    public void shouldTestFluxSubscriber() {
        Flux<String> flux = Flux
            .just("Gabriel", "Monteiro", "Fernandes")
            .log();
    
        StepVerifier
            .create(flux)
            .expectNext("Gabriel", "Monteiro", "Fernandes")
            .verifyComplete();
    }
    
    @Test
    public void shouldTestFluxSubscriberNumbers() {
        Flux<Integer> flux = Flux
            .range(1, 5)
            .log();
        
        StepVerifier
            .create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestFluxSubscriberList() {
        Flux<Integer> flux = Flux
            .fromIterable(List.of(1, 2, 3, 4, 5))
            .log();
        
        StepVerifier
            .create(flux)
            .expectNext(1, 2, 3, 4, 5)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestFluxNumbersError() {
        Flux<Integer> flux = Flux
            .range(1, 5)
            .map(i -> {
                if (i == 4) {
                    throw new IndexOutOfBoundsException("Index out of bounds");
                }
                
                return i;
            });
        
        flux.subscribe(
            i -> log.info("Number: {}", i),
            Throwable::printStackTrace,
            () -> log.info("Done."),
            subscription -> subscription.request(3)
        );
        
        StepVerifier
            .create(flux)
            .expectNext(1, 2, 3)
            .expectError(IndexOutOfBoundsException.class)
            .verify();
    }
    
    @Test
    public void shouldTestFluxNumbersUglyBackpressure() {
        Flux<Integer> flux = Flux
            .range(1, 10)
            .log();
        
        flux.subscribe(new Subscriber<Integer>() {
            private Integer count = 0;
            private Subscription subscription;
            
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(2);
            }
    
            @Override
            public void onNext(Integer integer) {
                count++;
                
                if (count >= 2) {
                    count = 0;
                    subscription.request(2);
                }
            }
    
            @Override
            public void onError(Throwable throwable) {
        
            }
    
            @Override
            public void onComplete() {
        
            }
        });
        
        StepVerifier
            .create(flux)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestFluxNumbersNotSoUglyBackpressure() {
        Flux<Integer> flux = Flux
            .range(1, 10);

        flux.subscribe(new BaseSubscriber<>() {
            private Integer count = 0;
            private final Integer REQ_COUNT = 2;
    
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(REQ_COUNT);
            }
    
            @Override
            protected void hookOnNext(Integer value) {
                count++;
                
                if (count >= REQ_COUNT) {
                    count = 0;
                    request(REQ_COUNT);
                }
            }
        });
        
        StepVerifier
            .create(flux)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .verifyComplete();
    }
    
    @Test
    public void shouldTestFluxSubscriberIntervalOne() throws Exception {
        Flux<Long> flux = Flux
            .interval(Duration.ofMillis(100))
            .take(10)
            .log();
        
        flux.subscribe(i -> log.info("{}", i));
        
        Thread.sleep(3000);
    }
    
    @Test
    public void shouldTestFluxSubscriberIntervalTwo() {
        StepVerifier
            .withVirtualTime(this::createInterval)
            .expectSubscription()
            .expectNoEvent(Duration.ofDays(1))
            .thenAwait(Duration.ofDays(2))
            .expectNext(0L)
            .expectNext(1L)
            .thenCancel()
            .verify();
    }
    
    private Flux<Long> createInterval() {
        return Flux.interval(Duration.ofDays(1)).log();
    }
}
