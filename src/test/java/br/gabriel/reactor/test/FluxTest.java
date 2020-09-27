package br.gabriel.reactor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
}
