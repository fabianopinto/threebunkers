package fabianopinto.threebunkers.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    @Override
    public void start(Promise<Void> promise) {
//        vertx.deployVerticle(HealthCheck.class, new DeploymentOptions());

        var port = vertx.getOrCreateContext().config().getInteger("http.port");
        vertx.createHttpServer()
                .requestHandler(this::serviceHandler)
                .listen(port)
                .onSuccess(http -> {
                    promise.complete();
                    LOGGER.info("Started ServerVerticle on port {}", port);
                })
                .onFailure(err -> promise.fail(err.getCause()));
    }

    private void serviceHandler(HttpServerRequest request) {
        var start = System.currentTimeMillis();
        Future.all(
                        vertx.eventBus().<String>request("service.heavy-query", null),
                        vertx.eventBus().<String>request("service.slow-request", null))
                .onSuccess(results -> {
                    var result1 = results.<Message<String>>resultAt(0).body();
                    var result2 = results.<Message<String>>resultAt(1).body();
                    var millis = System.currentTimeMillis() - start;
                    LOGGER.debug("vertx {} -- {} -- {}", millis, result1, result2);
                    request.response()
                            .putHeader("content-type", "text/plain")
                            .end("vertx %d -- %s -- %s".formatted(millis, result1, result2));
                })
                .onFailure(err -> {
                    LOGGER.error("vertx", err);
                    request.response()
                            .setStatusCode(500)
                            .end();
                });
    }

}
