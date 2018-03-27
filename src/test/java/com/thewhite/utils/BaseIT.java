package com.thewhite.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Korovin A. on 20.11.2017.
 *
 * Run all context configuration with database rider rule.
 *
 * @author Korovin Anatoliy
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseIT extends BaseDatabaseRiderIT {
    protected ObjectMapper mapper = new ObjectMapper();
}
