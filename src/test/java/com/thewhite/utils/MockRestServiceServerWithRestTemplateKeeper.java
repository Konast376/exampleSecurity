package com.thewhite.utils;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Sergey Nikolaev on 13.09.2017.
 *
 * Вспомогательный класс-обёртка над {@link MockRestServiceServer}.
 * <p>
 * Необходим для восстановления состояния restTemplate после тестов,
 * использующих {@link MockRestServiceServer}
 * <p>
 * Появление вызывано тем, что {@link RestTemplate}, являющийся singletone bean,
 * не восстанавливает своего состояния без чистки контекста.
 * Из-за этого в тестах, обращающихся к restTemplate, возможно падение из-за неожиданных запросов,
 * если они запускались после тестов, использующих MockRestServiceServer.
 * <p>
 * При создании экземпляра этого класса сохраняется текущее состояние
 * {@link ClientHttpRequestFactory} restTemplate.
 * После прогона всех тестов необходимо вызывать метод restoreFactory,
 * помещающий сохранённое значение обратно в restTemplate.
 *
 * @author Sergey Nikolaev
 * @version 1.0.0
 */
public class MockRestServiceServerWithRestTemplateKeeper {

    private final RestTemplate restTemplate;
    private final ClientHttpRequestFactory factoryBackUp;

    public MockRestServiceServerWithRestTemplateKeeper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.factoryBackUp = restTemplate.getRequestFactory();
    }

    /**
     * Создание MockRestServiceServer
     */
    public MockRestServiceServer createServer() {
        return MockRestServiceServer.createServer(restTemplate);
    }

    /**
     * Восстановление ClientHttpRequestFactory в RestTemplate.
     */
    public void restoreFactory() {
        restTemplate.setRequestFactory(factoryBackUp);
    }

}
