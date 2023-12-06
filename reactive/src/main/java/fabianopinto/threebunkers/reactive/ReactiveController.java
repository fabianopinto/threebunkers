package fabianopinto.threebunkers.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ReactiveController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveController.class);

    private final ReactiveService reactiveService;

    public ReactiveController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    @GetMapping
    public Mono<String> get() {
        var start = System.currentTimeMillis();
        return Mono.zip(reactiveService.heavyQuery(), reactiveService.slowRequest())
                .map(result -> {
                    var result1 = result.getT1();
                    var result2 = result.getT2();
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("reactive {} -- {} -- {}", millis, result1, result2);
                    return "reactive %d -- %s -- %s".formatted(millis, result1, result2);
                });
    }

}
