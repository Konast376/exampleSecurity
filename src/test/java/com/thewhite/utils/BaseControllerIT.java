package com.thewhite.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
@AutoConfigureMockMvc
public abstract class BaseControllerIT extends BaseDatabaseRiderIT {
    protected ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
}
