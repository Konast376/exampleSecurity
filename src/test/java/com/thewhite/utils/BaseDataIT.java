package com.thewhite.utils;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Korovin A. on 15.11.2017.
 *
 * Abstract Base test for testing work with the database.
 * Use only for testing DAO level.
 * Does not load entire context configuration.
 *
 * Main test cases:
 * - testing db.schem and constraints
 * - testing queries created by the spring-data, specification, querydsl
 * - testing custom query
 * - asserts sql query count
 *
 * @author Korovin Anatoliy
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseDataIT extends BaseDatabaseRiderIT {


}
