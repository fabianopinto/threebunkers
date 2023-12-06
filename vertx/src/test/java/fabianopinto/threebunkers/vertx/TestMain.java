package fabianopinto.threebunkers.vertx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(VertxExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TestMain {

    private static final String STRING = "";

    @Nested
    class when_doing_something {
        @Test
        void then_something_should_happen() {
            assertThat(STRING).isEmpty();
            assertEquals(2, 1 + 1);
        }
    }

//    @BeforeEach
//    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
//        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
//    }

//    @Test
//    void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
//        testContext.completeNow();
//        assertNotNull(vertx);
//    }
}
