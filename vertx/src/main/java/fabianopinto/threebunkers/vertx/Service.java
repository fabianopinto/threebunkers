package fabianopinto.threebunkers.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Service extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private WebClient client;

    @Override
    public void start(Promise<Void> promise) {
        client = WebClient.create(vertx);

        vertx.eventBus().consumer("service.heavy-query", this::heavyQuery);
        vertx.eventBus().consumer("service.slow-request", this::slowRequest);
    }

    private void heavyQuery(Message<String> message) {
        var start = System.currentTimeMillis();
        vertx.eventBus().<String>request("database.heavy-query", JsonArray.of(randomString(), randomString()))
                .onSuccess(result -> {
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("heavyQuery {} -- {}", millis, result.body());
                    message.reply("heavyQuery %d -- %s".formatted(millis, result.body()));
                })
                .onFailure(err -> {
                    LOGGER.error("vertx", err);
                    message.fail(1, err.getMessage());
                });
    }

    private void slowRequest(Message<String> message) {
        var start = System.currentTimeMillis();
        client
                .get(80, "httpbin.org", "/delay/%d".formatted(randomDelay()))
                .send()
                .onSuccess(response -> {
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("slowRequest -- {}", millis);
                    message.reply("slowRequest -- %d".formatted(millis));
                })
                .onFailure(err -> {
                    LOGGER.error("vertx", err);
                    message.fail(1, err.getMessage());
                });
    }

    private String randomString() {
        return UUID.randomUUID().toString().substring(0, 3);
    }

    private int randomDelay() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }

}
