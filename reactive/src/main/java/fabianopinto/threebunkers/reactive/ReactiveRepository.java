package fabianopinto.threebunkers.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveRepository.class);
    private static final String HEAVY_SQL = """
            SELECT count(1)
            FROM (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE :p1
                     ORDER BY content -> 'id' DESC
                 ) a, (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE :p2
                     ORDER BY content -> 'id' DESC
                 ) b;
            """;

    private final DatabaseClient databaseClient;

    public ReactiveRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<String> heavyQuery(String string1, String string2) {
        LOGGER.debug("repository -- {} {}", string1, string2);
        return Mono
                .zip(Mono.just(System.currentTimeMillis()),
                        databaseClient
                                .sql(HEAVY_SQL)
                                .bind("p1", "%" + string1 + "%")
                                .bind("p2", "%" + string2 + "%")
                                .fetch()
                                .first())
                .map(result -> {
                    var millis = System.currentTimeMillis() - result.getT1();
                    return "repository %s".formatted(millis);
                });
    }

}
