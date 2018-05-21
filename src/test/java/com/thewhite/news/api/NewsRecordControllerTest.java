package com.thewhite.news.api;

import com.sun.org.apache.xpath.internal.Arg;
import com.thewhite.news.actions.arguments.CreateAttachmentActionArgument;
import com.thewhite.news.api.dto.NewsCreateDTO;
import com.thewhite.news.api.dto.NewsRecordDTO;
import com.thewhite.news.api.dto.NewsUpdateDTO;
import com.thewhite.news.api.mappers.AttachmentMapper;
import com.thewhite.news.api.mappers.NewsRecordMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.AuthService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.api.dto.CollectionDTO;
import com.whitesoft.util.Functions;
import com.whitesoft.util.actions.Action;
import com.whitesoft.util.actions.OneFieldActionArgument;
import com.whitesoft.util.functions.Function1;
import com.whitesoft.util.test.CustomAssertion;
import com.whitesoft.util.test.MvcRequester;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Vdovin S. on 18.05.18.
 * <p>
 * TODO: replace on javadoc
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public class NewsRecordControllerTest {

    @Mock
    private NewsRecordService newsRecordService;
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private NewsRecordMapper newsRecordMapper;
    @Mock
    private AttachmentMapper attachmentMapper;
    @Mock
    private AuthService authService;
    @Mock
    private Action<Attachment> createAttachmentAction;
    @Mock
    private Action<Void> deleteNewsRecordAction;

    private NewsRecordController controller;

    private final String title = "title";
    private final String content = "content";
    private final Date endDate = new Date();
    private final UUID currentUserId = UUID.randomUUID();
    private final UUID id = UUID.randomUUID();
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new NewsRecordController(newsRecordService,
                                              attachmentService,
                                              newsRecordMapper,
                                              authService,
                                              createAttachmentAction,
                                              attachmentMapper,
                                              deleteNewsRecordAction);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();
        when(newsRecordMapper.toDTO(any())).thenReturn(new NewsRecordDTO());
    }


    /**
     * Тест создания записи
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        //Arrange
        NewsCreateDTO createDTO = NewsCreateDTO.builder()
                                               .title(title)
                                               .content(content)
                                               .endDate(endDate)
                                               .build();
        when(authService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsRecordService.create(eq(title), notNull(Date.class), eq(content), eq(endDate), eq(currentUserId)))
                .thenReturn(new NewsRecord());
        //Act
        MvcRequester.on(mockMvc)
                    .to("/news/create")
                    .post(createDTO)
                    .doExpect(status().isCreated());

        //Assert
        verify(newsRecordService).create(eq(title), notNull(Date.class), eq(content), eq(endDate), eq(currentUserId));
    }

    /**
     * Тест получения записи по ID
     *
     * @throws Exception
     */
    @Test
    public void getOne() throws Exception {
        //Arrange
        NewsRecord newsRecord = mock(NewsRecord.class);
        when(newsRecordService.getExisting(id)).thenReturn(newsRecord);
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}", NewsRecordController.URI, id)
                    .get()
                    //Assert
                    .doExpect(status().isOk());
        verify(newsRecordService).getExisting(id);
        verify(newsRecordMapper).toDTO(newsRecord);
    }

    /**
     * Тест получения списка опубликованных новостей
     *
     * @throws Exception
     */
    @Test
    public void getActualNews() throws Exception {
        //Arrange
        int pageSize = 10;
        int pageNo = 2;
        Page page = mock(Page.class);
        when(newsRecordService.getAll(eq(currentUserId),
                                      eq(currentUserId),
                                      eq(RecordStatus.PUBLISHED),
                                      Mockito.any(Date.class),
                                      eq(null),
                                      eq(pageSize),
                                      eq(pageNo))).thenReturn(page);
        when(authService.getCurrentUserId()).thenReturn(currentUserId);

        //Todo вообще странно выглядит, что мы в арранже делаем ассерты
        when(newsRecordMapper.getPageMapper())
                .then(a -> (Function1<Page, CollectionDTO>) p -> {
                    assertThat(p).isEqualTo(page);
                    return new CollectionDTO<>();
                });
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/actual/list", NewsRecordController.URI)
                    .withParam("pageSize", pageSize)
                    .withParam("pageNo", pageNo)
                    .get();
        //Assert
        verify(newsRecordService).getAll(eq(currentUserId),
                                         eq(currentUserId),
                                         eq(RecordStatus.PUBLISHED),
                                         Mockito.any(Date.class),
                                         eq(null),
                                         eq(pageSize),
                                         eq(pageNo));
    }

    /**
     * Тест поиска новостей по параметрам
     *
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        //Arrange
        Page page = mock(Page.class);
        RecordStatus recordStatus = RecordStatus.PUBLISHED;
        int year = 3533;
        int pageSize = 10;
        int pageNo = 3;
        when(newsRecordService.getAll(null, null, recordStatus, null, year, pageSize, pageNo))
                .thenReturn(page);

        when(newsRecordMapper.getPageMapper()).thenReturn(p -> {
            assertThat(p).isEqualTo(page);
            return new CollectionDTO<>();
        });
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/list", NewsRecordController.URI)
                    .withParam("recordStatus", recordStatus)
                    .withParam("year", year)
                    .withParam("pageSize", pageSize)
                    .withParam("pageNo", pageNo)
                    .get()
                    .doExpect(status().isOk());
        //Assert
        verify(newsRecordService).getAll(null, null, recordStatus, null, year, pageSize, pageNo);
    }

    /**
     * Тест получения списка годов, в которых есть новости
     *
     * @throws Exception
     */
    @Test
    public void getYears() throws Exception {
        //Arrange
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/years", NewsRecordController.URI)
                    .get();
        //Assert
        verify(newsRecordService).getYears();
    }

    /**
     * Тест редактирования новости
     *
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        //Arrange

        NewsUpdateDTO newsUpdateDTO = new NewsUpdateDTO(
                title,
                content,
                endDate
        );
        NewsRecord newsRecord = mock(NewsRecord.class);
        when(newsRecordService.update(id, title, content, endDate)).thenReturn(newsRecord);
        when(newsRecordMapper.getMapper()).thenReturn(r -> {
            assertThat(r).isEqualTo(newsRecord);
            return null;
        });
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/{id}/update", NewsRecordController.URI, id)
                    .post(newsUpdateDTO)
                    .doExpect(status().isOk());
        //Assert
        verify(newsRecordService).update(id, title, content, endDate);
    }

    /**
     * Тест публикации новости
     *
     * @throws Exception
     */
    @Test
    public void publish() throws Exception {
        //Arrange
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}/publish", NewsRecordController.URI, id)
                    .post()
                    .doExpect(status().isOk());
        //Assert
        verify(newsRecordService).publish(id);
    }

    /**
     * Тест пометки новости как прочитанной
     *
     * @throws Exception
     */
    @Test
    public void mark() throws Exception {
        //Arrange
        when(authService.getCurrentUserId()).thenReturn(currentUserId);
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}/mark", NewsRecordController.URI, id)
                    .post()
                    .doExpect(status().isOk());
        //Assert
        verify(newsRecordService).mark(id, currentUserId);
    }

    /**
     * Тест удаления новости
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        ArgumentCaptor<OneFieldActionArgument> captor = ArgumentCaptor.forClass(OneFieldActionArgument.class);
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/{id}/delete", NewsRecordController.URI, id)
                    .post();
        //Assert
        verify(deleteNewsRecordAction).execute(captor.capture());
        assertThat(captor.getValue().getField()).isEqualTo(id);
    }

    /**
     * Тест прикрепления файла к новости
     *
     * @throws Exception
     */
    @Test
    public void attach() throws Exception {
        //Arrange
        String originalFileName = "file name.pdf";
        MimeType mediaType = MimeType.valueOf("application/pdf");
        ArgumentCaptor<CreateAttachmentActionArgument> captor =
                ArgumentCaptor.forClass(CreateAttachmentActionArgument.class);
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/{id}/attachments/create", NewsRecordController.URI, id)
                    .withFile("data", originalFileName, mediaType, content.getBytes())
                    .upload();
        //Assert
        verify(createAttachmentAction).execute(captor.capture());
        CreateAttachmentActionArgument attachmentActionArgument = captor.getValue();
        CustomAssertion.assertThat(attachmentActionArgument)
                       .lazyCheck(CreateAttachmentActionArgument::getNewsRecord, id)
                       .lazyCheck(CreateAttachmentActionArgument::getName, originalFileName)
                       .check();
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            Assertions.assertThat(attachmentActionArgument.getFileData())
                      .hasSameContentAs(inputStream);
        }
    }

    /**
     * Тест получения списка вложений для новости
     *
     * @throws Exception
     */
    @Test
    public void getAttachments() throws Exception {
        //Arrange
        Attachment attachment = mock(Attachment.class);
        when(attachmentService.getAll(id)).thenReturn(Collections.singletonList(attachment));
        when(attachmentMapper.getCollectionMapper()).thenReturn(c -> {
            Assertions.assertThat(c).hasSize(1);
            Assertions.assertThat(c).containsExactly(attachment);
            return new CollectionDTO<>();
        });
        //Act
        MvcRequester.on(mockMvc)
                    .to("{uri}/{id}/attachments/list", NewsRecordController.URI, id)
                    .get();
        //Assert
        verify(attachmentService).getAll(id);
    }
}