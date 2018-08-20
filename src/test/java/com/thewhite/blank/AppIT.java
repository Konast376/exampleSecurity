package com.thewhite.blank;

import com.antkorwin.junit5integrationtestutils.test.runners.EnableIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Created on 20.08.2018.
 *
 * @author Anatolii Korovin
 * @author Sergey Vdovin
 * @author Maxim Seredkin
 */
@EnableIntegrationTests
class AppIT {

    @Test
    @DisplayName("Интеграционный тест подъема контекста")
    void contextRunTest() { }

}
