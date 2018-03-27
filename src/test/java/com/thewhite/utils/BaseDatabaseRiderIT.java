package com.thewhite.utils;

import com.github.database.rider.core.DBUnitRule;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Korovin A. on 27.11.2017.
 *
 * инициализация правила для работы DatabaseRider'а
 *
 * @author Korovin Anatoliy
 * @version 1.0
 */
public abstract class BaseDatabaseRiderIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static Connection connection;

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> {
        // Пока не пофиксили в DatabaseRider'е, боремся руками с connection leak
        return (connection != null)
               ? connection
               : getConnection();
    });

    private Connection getConnection() {
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Assertions.fail("Error: get database connection");
        }
        return connection;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
