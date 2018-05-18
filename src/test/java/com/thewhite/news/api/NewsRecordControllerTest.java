package com.thewhite.news.api;

import com.thewhite.news.api.dto.NewsCreateDTO;
import com.thewhite.news.api.dto.NewsRecordDTO;
import com.thewhite.news.api.mappers.AttachmentMapper;
import com.thewhite.news.api.mappers.NewsRecordMapper;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.AuthService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.util.actions.Action;
import com.whitesoft.util.test.MvcRequester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        when(newsRecordService.create(eq(title), notNull(Date.class), eq(content), eq(endDate), currentUserId))
                .thenReturn(new NewsRecord());
        //Act
        MvcRequester.on(mockMvc)
                    .to("/news/create")
                    .post(createDTO)
                    .doExpect(status().isCreated());

        //Assert
        verify(newsRecordService).create(eq(title), notNull(Date.class), eq(content), eq(endDate), currentUserId);
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getOne() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getActualNews() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getYears() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void publish() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void mark() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void attach() throws Exception {
        //Arrange
        //Act
        //Assert
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getAttachments() throws Exception {
        //Arrange
        //Act
        //Assert
    }
}