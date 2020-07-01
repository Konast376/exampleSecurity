package com.thewhite.security;

import com.whitesoft.util.CustomRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Created on 20.08.2018.
 *
 * Болванка для микросервисов
 *
 * @author Anatolii Korovin
 * @author Sergey Vdovin
 * @author Maxim Seredkin
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        CustomRunner.run(App.class, args, SpringApplication::run);
    }
}