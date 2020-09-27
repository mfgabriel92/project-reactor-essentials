package br.gabriel.reactor.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
public class ConnectableFluxTest {
    @Test
    public void shouldTestConnectableFlux() {
        ConnectableFlux<Integer> connectableFlux = Flux
            .range(1, 10)
            .log()
            .delayElements(Duration.ofMillis(100))
            .publish();
        
        connectableFlux.connect();
    
        StepVerifier
            .create(connectableFlux)
            .then(connectableFlux::connect)
            .thenConsumeWhile(i -> i <= 5)
            .expectNext(6, 7, 8, 9, 10)
            .expectComplete()
            .verify();
    }
    
    @Test
    public void shouldTestAutoConnectableFlux() {
        Flux<Integer> flux = Flux
            .range(1, 5)
            .log()
            .delayElements(Duration.ofMillis(100))
            .publish()
            .autoConnect();
        
        StepVerifier
            .create(flux)
            .then(flux::subscribe)
            .expectNext(1, 2, 3, 4, 5)
            .expectComplete()
            .verify();
    }
}
