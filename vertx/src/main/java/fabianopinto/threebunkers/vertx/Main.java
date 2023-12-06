package fabianopinto.threebunkers.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class Main extends AbstractVerticle {

    private static final int SERVER_PORT = 8083;

    @Override
    public void start(Promise<Void> promise) {
        Future.all(
                        vertx.deployVerticle(Database.class, new DeploymentOptions()
                                .setInstances(4)
                                .setConfig(JsonObject.of(
                                        "db.host", "localhost",
                                        "db.port", 5432,
                                        "db.database", "postgres",
                                        "db.username", "postgres",
                                        "db.password", ""))),
                        vertx.deployVerticle(Service.class, new DeploymentOptions()
                                .setInstances(4)),
                        vertx.deployVerticle(HealthCheck.class, new DeploymentOptions()
                                .setConfig(JsonObject.of("interval", 5))),
                        vertx.deployVerticle(Server.class, new DeploymentOptions()
                                .setConfig(JsonObject.of("http.port", SERVER_PORT))))
                .onSuccess(composite -> promise.complete())
                .onFailure(err -> promise.fail(err.getCause()));
    }

}
