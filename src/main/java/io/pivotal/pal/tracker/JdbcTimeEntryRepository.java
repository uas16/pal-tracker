package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Repository
@Primary
public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private JdbcTemplate template;
    public JdbcTimeEntryRepository(DataSource dataSource){
        this.template= new JdbcTemplate(dataSource);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
                rs.getLong("id"),
                rs.getLong("project_id"),
                rs.getLong("user_id"),
                rs.getDate("date").toLocalDate(),
                rs.getInt("hours")
        );

    private final ResultSetExtractor<TimeEntry> extractor = (rs)->{
     return rs.next()?  mapper.mapRow(rs,1) : null;
    };

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder new_id = new GeneratedKeyHolder();
        String sql = "INSERT INTO time_entries (project_id,user_id,date,hours) VALUES (?,?,?,?)";
        template.update(connection->{
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1,timeEntry.getProjectId());
            ps.setLong(2,timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4,timeEntry.getHours());
            return ps;
        }, new_id);
        return find(new_id.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        String sql = "Select * from time_entries where id=?";
        return template.query(sql,new Object[]{id},extractor);
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "Select * from time_entries";
        return template.query(sql,mapper);
    }

    @Override
    public void delete(long id) {
        String sql = "Delete from time_entries where id=?";
        template.update(sql,id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String sql = "update time_entries set project_id=?, user_id= ?,date=?,hours=? where id=?";
        template.update(sql,timeEntry.getProjectId(),timeEntry.getUserId(),Date.valueOf(timeEntry.getDate()),timeEntry.getHours(),id);
        return find(id);
    }
}
