package com.thewhite.blank;

import com.thewhite.utils.BaseIT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Интеграционный тест подъема контекста и докера.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AppIT extends BaseIT {
    @Test
    public void contextRunTest() { }
}
