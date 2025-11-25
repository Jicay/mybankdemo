package com.example.mybank.infrastructure.driven.jdbc;

import com.example.mybank.domain.model.Client;
import com.example.mybank.domain.model.Client.Id;
import com.example.mybank.domain.model.Client.Name;
import com.example.mybank.domain.ports.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static java.lang.IO.println;

@Repository
public class JdbcClientRepository implements ClientRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcClientRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcClientRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Client> MAPPER = (rs, _) -> {
        Id id = Id.from(rs.getString("id"));
        Name last = new Name(rs.getString("last_name"));
        Name first = new Name(rs.getString("first_name"));
        return new Client(id, last, first);
    };

    @Override
    public List<Client> findAll() {
        logger.debug("Retrieving all clients from database");
        return jdbcTemplate.query("SELECT id, last_name, first_name FROM clients ORDER BY last_name, first_name", MAPPER);
    }

    @Override
    public Client add(Client client) {
        println("Adding client to database: " + client);
        println("Adding id to database: " + client.id().toString());
        int updated = jdbcTemplate.update(
                // language=SQL
                "INSERT INTO clients (id, last_name, first_name) VALUES (:id, :last_name, :first_name) ON CONFLICT (id) DO NOTHING",
                Map.of(
                        "id", client.id().value().toString(),
                        "last_name", client.lastName().value(),
                        "first_name", client.firstName().value()
                )
        );
        logger.debug("Saved client {} {} with id {} (rows {})", client.firstName(), client.lastName(), client.id(), updated);
        return client;
    }

    @Override
    public boolean existsBy(Name lastName, Name firstName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM clients WHERE lower(last_name) = lower(:last_name) AND lower(first_name) = lower(:first_name)",
                Map.of(
                        "last_name", lastName.value(),
                        "first_name", firstName.value()
                ),
                Integer.class
        );
        return count != null && count > 0;
    }
}
