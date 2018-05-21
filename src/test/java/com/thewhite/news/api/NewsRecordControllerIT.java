package com.thewhite.news.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.database.rider.core.api.dataset.DataSet;
import com.thewhite.news.api.dto.NewsCreateDTO;
import com.thewhite.news.api.dto.NewsRecordDTO;
import com.thewhite.news.api.dto.NewsUpdateDTO;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.thewhite.news.repositories.NewsRecordRepository;
import com.thewhite.news.service.AuthService;
import com.thewhite.utils.BaseIT;
import com.whitesoft.api.dto.CollectionDTO;
import com.whitesoft.util.test.CustomAssertion;
import com.whitesoft.util.test.MvcRequester;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.thewhite.news.model.RecordStatus.PROJECT;
import static com.thewhite.news.model.RecordStatus.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 21.05.2018.
 * Интеграционные тесты контроллера для работы с новостями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@DataSet(value = "/datasets/attachments.json", cleanBefore = true, cleanAfter = true)
public class NewsRecordControllerIT extends BaseIT {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockService;

    @Autowired
    private NewsRecordRepository newsRecordRepository;

    @MockBean
    private AuthService authService;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID newsRecordId = UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa00");
    private final UUID userId = UUID.fromString("179a1f2b-78cd-4322-9c08-9697a1d810e6");

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).alwaysDo(print()).build();
        mockService = MockRestServiceServer.createServer(restTemplate);
        when(authService.getCurrentUserId()).thenReturn(testUserId);
    }

    /**
     * Тест создания новости
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        //Arrange
        String title = "title";
        String content = "content";
        Date date = new Date();

        NewsCreateDTO createDTO = NewsCreateDTO.builder()
                                               .content(content)
                                               .title(title)
                                               .endDate(date)
                                               .build();
        //Act
        NewsRecordDTO result = MvcRequester.on(mockMvc)
                                           .to("{url}/create", NewsRecordController.URI)
                                           .post(createDTO)
                                           .doExpect(status().isCreated())
                                           .returnAs(NewsRecordDTO.class);
        //Assert
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecordDTO::getTitle, title)
                       .lazyCheck(NewsRecordDTO::getContent, content)
                       .lazyCheck(NewsRecordDTO::getEndDate, date)
                       .lazyCheck(NewsRecordDTO::getStatus, RecordStatus.PROJECT)
                       .lazyCheck(NewsRecordDTO::getUserId, testUserId)
                       .check();
    }

    /**
     * Тест получения записи по ID
     *
     * @throws Exception
     */
    @Test
    public void getOne() throws Exception {
        //Arrange
        //Act
        NewsRecordDTO result = MvcRequester.on(mockMvc)
                                           .to("{url}/{id}", NewsRecordController.URI, newsRecordId)
                                           .get()
                                           .doExpect(status().isOk())
                                           .returnAs(NewsRecordDTO.class);
        //Assert
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecordDTO::getTitle, "title 1")
                       .lazyCheck(NewsRecordDTO::getContent, "content 1")
                       .lazyCheck(NewsRecordDTO::getEndDate, null)
                       .lazyCheck(NewsRecordDTO::getStatus, PUBLISHED)
                       .lazyCheck(NewsRecordDTO::getUserId, UUID.fromString("179a1f2b-78cd-4322-9c08-9697a1d810e6"))
                       .check();
    }

    /**
     * Тест получения актуальных новостей
     *
     * @throws Exception
     */
    @Test
    public void getActualNews() throws Exception {
        //Arrange
        //Act
        CollectionDTO<NewsRecordDTO> results =
                MvcRequester.on(mockMvc)
                            .to("{url}/actual/list", NewsRecordController.URI)
                            .withParam("pageSize", 10)
                            .withParam("pageNo", 0)
                            .get()
                            .doReturn(new TypeReference<CollectionDTO<NewsRecordDTO>>() {});
        //Assert
        assertThat(results.getItems()).extracting(
                NewsRecordDTO::getId,
                NewsRecordDTO::getTitle,
                NewsRecordDTO::getContent,
                NewsRecordDTO::getStatus,
                NewsRecordDTO::getUserId
        ).containsExactly(tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa00"),
                                "title 1",
                                "content 1",
                                PUBLISHED,
                                userId),
                          tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa01"),
                                "title 2",
                                "content 2",
                                PUBLISHED,
                                userId),
                          tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa02"),
                                "title 3",
                                "content 3",
                                PUBLISHED,
                                userId));
    }

    /**
     * Тест списка новостей
     *
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        //Arrange
        //Act
        CollectionDTO<NewsRecordDTO> results =
                MvcRequester.on(mockMvc)
                            .to("{url}/list", NewsRecordController.URI)
                            .withParam("pageSize", 10)
                            .withParam("pageNo", 0)
                            .get()
                            .doReturn(new TypeReference<CollectionDTO<NewsRecordDTO>>() {});
        //Assert
        assertThat(results.getItems()).extracting(
                NewsRecordDTO::getId,
                NewsRecordDTO::getTitle,
                NewsRecordDTO::getContent,
                NewsRecordDTO::getStatus,
                NewsRecordDTO::getUserId
        ).containsExactly(tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa00"),
                                "title 1",
                                "content 1",
                                PUBLISHED,
                                userId),
                          tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa01"),
                                "title 2",
                                "content 2",
                                PUBLISHED,
                                userId),
                          tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa02"),
                                "title 3",
                                "content 3",
                                PUBLISHED,
                                userId),
                          tuple(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa03"),
                                "title 4",
                                "content 4",
                                PROJECT,
                                userId));
    }

    /**
     * Тест получения списка лет
     *
     * @throws Exception
     */
    @Test
    public void getYears() throws Exception {
        //Arrange
        //Act
        List<String> result = MvcRequester.on(mockMvc)
                                          .to("{url}/years", NewsRecordController.URI)
                                          .get()
                                          .doExpect(status().isOk())
                                          .doReturn(new TypeReference<List<String>>() {});
        //Assert
        assertThat(result).containsOnly("2018", "2017");
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        //Arrange
        String title = "title";
        String content = "content";
        Date date = new Date();

        NewsUpdateDTO updateDTO = NewsUpdateDTO.builder()
                                               .content(content)
                                               .title(title)
                                               .endDate(date)
                                               .build();
        //Act
        NewsRecordDTO result = MvcRequester.on(mockMvc)
                                           .to("{url}/{id}/update", NewsRecordController.URI, newsRecordId)
                                           .post(updateDTO)
                                           .doExpect(status().isOk())
                                           .returnAs(NewsRecordDTO.class);
        //Assert
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecordDTO::getTitle, title)
                       .lazyCheck(NewsRecordDTO::getContent, content)
                       .lazyCheck(NewsRecordDTO::getEndDate, date)
                       .lazyCheck(NewsRecordDTO::getStatus, RecordStatus.PUBLISHED)
                       .lazyCheck(NewsRecordDTO::getUserId, userId)
                       .check();
    }

    /**
     * Тест публикация новости
     *
     * @throws Exception
     */
    @Test
    public void publish() throws Exception {
        //Arrange
        //Act
        NewsRecordDTO result = MvcRequester.on(mockMvc)
                                           .to("{url}/{id}/publish",
                                               NewsRecordController.URI,
                                               "84885588-33fe-49fb-a3e7-097d790eaa03")
                                           .post()
                                           .doExpect(status().isOk())
                                           .returnAs(NewsRecordDTO.class);
        //Assert
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecordDTO::getId, UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa03"))
                       .lazyCheck(NewsRecordDTO::getStatus, RecordStatus.PUBLISHED)
                       .lazyCheck(NewsRecordDTO::getUserId, userId)
                       .check();
    }

    /**
     * Тест пометка новости как прочитанной
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void mark() throws Exception {
        //Arrange
        when(authService.getCurrentUserId()).thenReturn(userId);
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}/mark", NewsRecordController.URI, newsRecordId)
                    .post();
        NewsRecord result = newsRecordRepository.findOne(newsRecordId);
        //Assert
        assertThat(result.getUsers()).containsOnly(userId);
        result.setUsers(null);
        newsRecordRepository.save(result);
    }

    /**
     * Тест удаления записи
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        //Act
        MvcRequester.on(mockMvc)
                    .to("{url}/{id}/delete", NewsRecordController.URI,
                        "84885588-33fe-49fb-a3e7-097d790eaa03")
                    .post()
                    .doExpect(status().isOk());
        //Assert
        assertThat(newsRecordRepository.exists(UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa03"))).isFalse();
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