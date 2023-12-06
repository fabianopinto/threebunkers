package fabianopinto.threebunkers.traditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TraditionalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraditionalService.class);

    private final TraditionalRepository traditionalRepository;
    private final RestTemplate restTemplate;

    public TraditionalService(TraditionalRepository traditionalRepository, RestTemplate restTemplate) {
        this.traditionalRepository = traditionalRepository;
        this.restTemplate = restTemplate;
    }

    public String heavyQuery() {
        var start = System.currentTimeMillis();
        var result = traditionalRepository.heavyQuery(randomString(), randomString());
        var millis = System.currentTimeMillis() - start;
        LOGGER.debug("heavyQuery {} -- {}", millis, result);
        return "heavyQuery %d -- %s".formatted(millis, result);
    }

    public String slowRequest() {
        var start = System.currentTimeMillis();
        restTemplate.getForObject("http://httpbin.org/delay/" + randomDelay(), Object.class);
        var millis = System.currentTimeMillis() - start;
        LOGGER.debug("slowRequest -- {}", millis);
        return "slowRequest " + millis;
    }

    private String randomString() {
        return UUID.randomUUID().toString().substring(0, 3);
    }

    private int randomDelay() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }

}
