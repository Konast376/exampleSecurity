package com.thewhite.news.api;

import com.github.database.rider.core.api.dataset.DataSet;
import com.thewhite.utils.BaseDatabaseRiderIT;
import com.thewhite.utils.BaseIT;
import com.whitesoft.util.test.MvcRequester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Vdovin S. on 21.05.18.
 * Интеграционные тесты работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@DataSet(value = "/datasets/attachments.json", cleanAfter = true, cleanBefore = true)
public class AttachmentControllerIT extends BaseIT {

    private MockRestServiceServer mockServer;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${content.storage.url}")
    private String contentStorageUrl;
    private final UUID attachmentId = UUID.fromString("a0772192-3df1-419a-87ae-d824ae911e4e");
    private final UUID containerId = UUID.fromString("f520dad3-c052-4365-a767-542973f3d4b9");
    private final String content = "content";
    private final String mediaType = "application/pdf";

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() throws Exception {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).alwaysDo(print()).build();
    }

    /**
     * Тест скачивания файла вложения
     *
     * @throws Exception
     */
    @Test
    public void download() throws Exception {
        //Arrange
        mockServer.expect(MockRestRequestMatchers.requestTo(UriComponentsBuilder.fromUriString("{url}/{id}")
                                                                                .buildAndExpand(contentStorageUrl, containerId)
                                                                                .toUri()))
                  .andRespond(withSuccess().body(content.getBytes())
                                           .contentType(MediaType.valueOf(mediaType)));
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}", AttachmentController.URI, attachmentId)
                    .get()
                    .doExpect(content().bytes(content.getBytes()));
        //Assert
        mockServer.verify();
    }

    /**
     * Тест удаления вложения
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        mockServer.expect(MockRestRequestMatchers.requestTo(UriComponentsBuilder.fromUriString("{url}/{id}/delete")
                                                                                .buildAndExpand(contentStorageUrl, containerId)
                                                                                .toUri()))
                  .andRespond(withSuccess());
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}/delete",
                        AttachmentController.URI,
                        attachmentId)
                    .post()
                    .doExpect(status().isOk());
        //Assert
        mockServer.verify();
    }
}