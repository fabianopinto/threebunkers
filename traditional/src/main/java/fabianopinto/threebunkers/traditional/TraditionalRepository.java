package fabianopinto.threebunkers.traditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TraditionalRepository implements RowMapper<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraditionalRepository.class);
    private static final String HEAVY_SQL = """
            SELECT count(1)
            FROM (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE ?
                     ORDER BY content -> 'id' DESC
                 ) a, (
                     SELECT *
                     FROM resource
                     WHERE content::text LIKE ?
                     ORDER BY content -> 'id' DESC
                 ) b;
            """;

    private final JdbcTemplate jdbcTemplate;

    public TraditionalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String heavyQuery(String string1, String string2) {
        var start = System.currentTimeMillis();
        LOGGER.debug("repository -- {} {}", string1, string2);
        jdbcTemplate.query(HEAVY_SQL, this, "%" + string1 + "%", "%" + string2 + "%");
        var millis = System.currentTimeMillis() - start;
        return "repository " + millis;
    }

    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt(1);
    }

}
