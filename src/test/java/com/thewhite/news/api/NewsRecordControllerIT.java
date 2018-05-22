package com.thewhite.news.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.database.rider.core.api.dataset.DataSet;
import com.thewhite.news.api.dto.AttachmentDTO;
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
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.RequestResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.thewhite.news.model.RecordStatus.PROJECT;
import static com.thewhite.news.model.RecordStatus.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 21.05.2018.
 * Интеграционные тесты контроллера для работы с новостями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@DataSet(value = "datasets/attachments.json", cleanBefore = true, cleanAfter = true)
public class NewsRecordControllerIT extends BaseIT {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private NewsRecordRepository newsRecordRepository;

    @Value("${content.storage.url}")
    private String contentStorageUrl;

    @MockBean
    private AuthService authService;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID newsRecordId = UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa00");
    private final UUID userId = UUID.fromString("179a1f2b-78cd-4322-9c08-9697a1d810e6");

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).alwaysDo(print()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
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
     * Тест загрузки вложений
     *
     * @throws Exception
     */
    @Test
    public void attach() throws Exception {
        //Arrange
        String content = "content";
        mockServer.expect(requestTo(UriComponentsBuilder.fromUriString("{url}/create")
                                                        .buildAndExpand(contentStorageUrl)
                                                        .toUri()))
                  .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                  .andRespond(withSuccess("{\"nodeId\":\"f520dad3-c052-4365-a767-542973f3d4b1\"," +
                                          "\"mediaType\":\"application/pdf\"}", MediaType.valueOf("application/json")));

        //Act
        AttachmentDTO result = MvcRequester.on(mockMvc)
                                           .to("{url}/{id}/attachments/create", NewsRecordController.URI, newsRecordId)
                                           .withFile("data", "test.pdf", MimeType.valueOf("application/pdf"), content.getBytes())
                                           .upload()
                                           //Assert
                                           .doExpect(status().isOk())
                                           .returnAs(AttachmentDTO.class);
        CustomAssertion.assertThat(result)
                       .lazyMatch(AttachmentDTO::getId, IsNull.notNullValue())
                       .lazyCheck(AttachmentDTO::getName, "test.pdf")
                       .lazyCheck(AttachmentDTO::getNewsRecordId, newsRecordId)
                       .check();
    }

    /**
     * Тест получения списка вложений
     *
     * @throws Exception
     */
    @Test
    public void getAttachments() throws Exception {
        //Arrange
        //Act
        CollectionDTO<AttachmentDTO> results = MvcRequester.on(mockMvc)
                                                           .to("{url}/{id}/attachments/list", NewsRecordController.URI, newsRecordId)
                                                           .get()
                                                           .doExpect(status().isOk())
                                                           .doReturn(new TypeReference<CollectionDTO<AttachmentDTO>>() {});
        //Assert
        assertThat(results.getItems()).extracting(AttachmentDTO::getId,
                                                  AttachmentDTO::getName,
                                                  AttachmentDTO::getNewsRecordId)
                                      .containsExactly(tuple(UUID.fromString("a0772192-3df1-419a-87ae-d824ae911e4e"),
                                                             "name1.pdf",
                                                             UUID.fromString("84885588-33fe-49fb-a3e7-097d790eaa00")));
    }

}