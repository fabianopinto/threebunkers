package fabianopinto.threebunkers.traditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TraditionalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraditionalController.class);

    private final TraditionalService traditionalService;

    public TraditionalController(TraditionalService traditionalService) {
        this.traditionalService = traditionalService;
    }

    @GetMapping
    public ResponseEntity<String> get() {
        var start = System.currentTimeMillis();
        var result1 = traditionalService.heavyQuery();
        var result2 = traditionalService.slowRequest();
        var millis = System.currentTimeMillis() - start;
        LOGGER.debug("traditional {} -- {} -- {}", millis, result1, result2);
        return ResponseEntity.ok("traditional %d -- %s -- %s".formatted(millis, result1, result2));
    }

}
