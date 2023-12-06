package fabianopinto.threebunkers.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReactiveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveService.class);

    private final ReactiveRepository reactiveRepository;
    private final WebClient webClient;

    public ReactiveService(ReactiveRepository reactiveRepository, WebClient webClient) {
        this.reactiveRepository = reactiveRepository;
        this.webClient = webClient;
    }

    public Mono<String> heavyQuery() {
        var start = System.currentTimeMillis();
        return reactiveRepository.heavyQuery(randomString(), randomString())
                .map(result -> {
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("heavyQuery {} -- {}", millis, result);
                    return "heavyQuery %d -- %s".formatted(millis, result);
                });
    }

    public Mono<String> slowRequest() {
        var start = System.currentTimeMillis();
        return webClient
                .get()
                .uri("http://httpbin.org/delay/{random}", randomDelay())
                .retrieve()
                .bodyToMono(String.class)
                .map(result -> {
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("slowRequest -- {}", millis);
                    return "slowRequest %d".formatted(millis);
                });
    }

    private String randomString() {
        return UUID.randomUUID().toString().substring(0, 3);
    }

    private int randomDelay() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }

}
