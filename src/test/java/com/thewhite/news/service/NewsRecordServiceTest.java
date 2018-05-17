package com.thewhite.news.service;

import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.repositories.NewsRecordRepository;
import com.whitesoft.util.exceptions.WSArgumentException;
import com.whitesoft.util.test.CustomAssertion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.thewhite.news.errorinfo.NewsErrorInfo.*;
import static com.whitesoft.util.test.GuardCheck.guardCheck;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewsRecordServiceTest {

    @Mock
    private NewsRecordRepository recordRepository;

    private NewsRecordServiceImpl service;

    private final String title = "title";
    private final Date postDate = new Date();
    private final String content = "content";
    private final UUID id = UUID.randomUUID();
    private final Date endDate = new Date();
    private final UUID userId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new NewsRecordServiceImpl(recordRepository);
        when(recordRepository.save(any(NewsRecord.class))).then(a -> a.getArguments()[0]);
    }

    /**
     * Тест создения записи
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        //Arrange
        //Act
        NewsRecord result = service.create(title, postDate, content, endDate, userId);
        //Assert
        verify(recordRepository).save(any(NewsRecord.class));
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecord::getTitle, title)
                       .lazyCheck(NewsRecord::getPostDate, postDate)
                       .lazyCheck(NewsRecord::getContent, content)
                       .lazyCheck(NewsRecord::getEndDate, endDate)
                       .lazyCheck(NewsRecord::getUserId, userId)
                       .check();
    }

    /**
     * Тест поведения при попытке создать запись с пустым заголовком
     *
     * @throws Exception
     */
    @Test
    public void createWithEmptyTile() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.create(" ", postDate, content, endDate, userId),
                   //Assert
                   WSArgumentException.class,
                   TITLE_CANT_BE_EMPTY);
    }

    /**
     * Тест поведения при попытке создать запись без заголовка
     *
     * @throws Exception
     */
    @Test
    public void createWithoutTile() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.create(null, postDate, content, endDate, userId),
                   //Assert
                   WSArgumentException.class,
                   TITLE_IS_MANDATORY);
    }

    /**
     * Тест получения списка запией
     *
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        //Arrange
        final int pageNo = 2;
        final int pageSize = 10;
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        //Act
        service.getAll(pageSize, pageNo);
        //Assert
        verify(recordRepository).findAll(captor.capture());
        CustomAssertion.assertThat(captor.getValue())
                       .lazyCheck(PageRequest::getPageNumber, pageNo)
                       .lazyCheck(PageRequest::getPageSize, pageSize)
                       .check();
    }

    /**
     * Тест редактирования записи
     *
     * @throws Exception
     */
    @Test
    public void update() throws Exception {
        //Arrange
        when(recordRepository.findById(id)).thenReturn(Optional.of(new NewsRecord()));
        //Act
        NewsRecord result = service.update(id, title, postDate, content, endDate);
        //Assert
        CustomAssertion.assertThat(result)
                       .lazyCheck(NewsRecord::getTitle, title)
                       .lazyCheck(NewsRecord::getPostDate, postDate)
                       .lazyCheck(NewsRecord::getContent, content)
                       .lazyCheck(NewsRecord::getEndDate, endDate)
                       .check();
    }

    /**
     * Тест поведения при попытке поставить пустой заголовок
     *
     * @throws Exception
     */
    @Test
    public void updateWithEmptyTitle() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.update(id, " ", postDate, content, endDate),
                   //Assert
                   WSArgumentException.class,
                   TITLE_CANT_BE_EMPTY);
    }

    /**
     * Тест поведения при попытке удалить заголовок
     *
     * @throws Exception
     */
    @Test
    public void updateWithoutTitle() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.update(id, null, postDate, content, endDate),
                   //Assert
                   WSArgumentException.class,
                   TITLE_IS_MANDATORY);
    }

    /**
     * Тест поведения при попытке установить пустое содержание
     *
     * @throws Exception
     */
    @Test
    public void updateWithEmptyContent() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.update(id, title, postDate, " ", endDate),
                   //Assert
                   WSArgumentException.class,
                   CONTENT_CANT_BE_EMPTY);
    }

    /**
     * Тест поведения при попытке удалить содержание
     *
     * @throws Exception
     */
    @Test
    public void updateWithoutContent() throws Exception {
        //Arrange
        //Act
        guardCheck(() -> service.update(id, title, postDate, null, endDate),
                   //Assert
                   WSArgumentException.class,
                   CONTENT_IS_MANDATORY);
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
        service.delete(id);
        //Assert
        verify(recordRepository).delete(id);
    }
}