package fabianopinto.threebunkers.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;
import org.slf4j.LoggerFactory;

public class Database extends AbstractVerticle {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private static final String HEAVY_SQL = """
            SELECT count(1)
            FROM (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE $1
                     ORDER BY content -> 'id' DESC
                 ) a, (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE $2
                     ORDER BY content -> 'id' DESC
                 ) b;
            """;

    private PgPool pool;

    /**
     *
     */
    @Override
    public void start(Promise<Void> promise) {
        var config = vertx.getOrCreateContext().config();
        var connectOptions = new PgConnectOptions()
                .setHost(config.getString("db.host"))
                .setPort(config.getInteger("db.port"))
                .setDatabase(config.getString("db.database"))
                .setUser(config.getString("db.username"))
                .setPassword(config.getString("db.password"));
        var poolOptions = new PoolOptions().setMaxSize(4);
        pool = PgPool.pool(vertx, connectOptions, poolOptions);

        vertx.eventBus().consumer("database.heavy-query", this::heavyQuery);
    }

    private void heavyQuery(Message<JsonArray> message) {
        var start = System.currentTimeMillis();
        var string1 = message.body().getString(0);
        var string2 = message.body().getString(1);
        LOGGER.debug("repository -- {} {}", string1, string2);
        pool
                .preparedQuery(HEAVY_SQL)
                .mapping(row -> row.getLong(0))
                .execute(Tuple.of("%" + string1 + "%", "%" + string2 + "%"))
                .onSuccess(result -> {
                    var millis = System.currentTimeMillis() - start;
                    message.reply("repository %d".formatted(millis));
                })
                .onFailure(err -> {
                    LOGGER.error("vertx", err);
                    message.fail(1, err.getMessage());
                });
    }

}
