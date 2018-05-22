package com.thewhite.news.api;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.service.AttachmentService;
import com.whitesoft.util.test.MvcRequester;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Vdovin S. on 21.05.18.
 * Тесты контроллера для работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public class AttachmentControllerTest {

    @Mock
    private AttachmentService attachmentService;
    private AttachmentController attachmentController;

    private MockMvc mockMvc;
    private final UUID attachmentId = UUID.randomUUID();
    private final String content = "content";
    private final String name = "name";
    private final String mediaType = "applicaiton/pdf";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        attachmentController = new AttachmentController(attachmentService);
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).alwaysDo(print()).build();
    }

    /**
     * Тест скачивания вложения
     *
     * @throws Exception
     */
    @Test
    public void download() throws Exception {
        //Arrange
        Attachment attachment = mock(Attachment.class);
        when(attachmentService.getExisting(attachmentId)).thenReturn(attachment);
        when(attachment.getName()).thenReturn(name);
        when(attachment.getMimeType()).thenReturn(mediaType);
        PowerMockito.doAnswer(a -> {
            org.apache.commons.io.IOUtils.copy(new ByteArrayInputStream(content.getBytes()),
                                               (OutputStream) a.getArguments()[1]);
            return null;
        }).when(attachmentService).download(eq(attachmentId), any(OutputStream.class));

        //Act
        MvcResult result = mockMvc.perform(get("{url}/{id}", AttachmentController.URI, attachmentId)).andExpect(status().isOk()).andReturn();
        //Assert
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(result.getResponse().getContentAsByteArray())) {
            Assertions.assertThat(inputStream).hasSameContentAs(new ByteArrayInputStream(content.getBytes()));
        }
        Assertions.assertThat(result.getResponse().getContentType()).isEqualTo(mediaType);
        Assertions.assertThat(result.getResponse().getHeader("Content-Disposition"))
                  .isEqualTo("attachment:filename=" + URLEncoder.encode(attachment.getName(), "UTF-8"));
    }

    /**
     * Тест удаления вложения
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/{id}/delete", AttachmentController.URI, attachmentId)
                    .post()
                    .doExpect(status().isOk());
        //Assert
        verify(attachmentService).delete(attachmentId);
    }
}