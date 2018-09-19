package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntryToCreate) {
        String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate. update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntryToCreate.getProjectId());
            statement.setLong(2, timeEntryToCreate.getUserId());
            statement.setDate(3, Date.valueOf(timeEntryToCreate.getDate()));
            statement.setInt(4, timeEntryToCreate.getHours());

            return statement;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        String sql = "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id=?";
        return jdbcTemplate.query(sql, new Object[]{id}, extractor);
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "SELECT id, project_id, user_id, date, hours FROM time_entries";
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String sql = "UPDATE time_entries SET project_id=?, user_id=?, date=?, hours=? WHERE id=?";

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());
            statement.setLong(5, id);

            return statement;
        });

        return find(id);
    }

    @Override
    public TimeEntry delete(long id) {
        TimeEntry timeEntry = find(id);
        String sql = "DELETE FROM time_entries WHERE id = ?";
        if (timeEntry != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setLong(1, id);
                return statement;
            });
        }
        return timeEntry;
    }
}
