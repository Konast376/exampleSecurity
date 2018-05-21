package com.thewhite.news.service;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.repositories.AttachmentRepository;
import com.whitesoft.util.Guard;
import com.whitesoft.util.TempFileHelper;
import com.whitesoft.util.exceptions.WSArgumentException;
import com.whitesoft.util.exceptions.WSNotFoundException;
import com.whitesoft.util.test.CustomAssertion;
import com.whitesoft.util.test.GuardCheck;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.thewhite.news.errorinfo.AttachmentErrorInfo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

/**
 * Created by Vdovin S. on 21.05.18.
 * Юнит тесты сервиса для работы с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AttachmentServiceImpl.class, TempFileHelper.class, IOUtils.class,
                 FileSystemResource.class,
                 FileOutputStream.class})
public class AttachmentServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AttachmentRepository attachmentRepository;

    private final String contentStorageUri = "http://localhost:8080/cs/containers";

    private AttachmentServiceImpl service;
    private final String name = "name";
    private final NewsRecord record = mock(NewsRecord.class);
    private final String content = "content";
    private final UUID testNodeId = UUID.randomUUID();
    private final UUID attachmentId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new AttachmentServiceImpl(restTemplate,
                                            attachmentRepository,
                                            contentStorageUri);
    }

    /**
     * Тест создания вложения
     *
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        //Arrange
        mockStatic(TempFileHelper.class, IOUtils.class, FileSystemResource.class);
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        Path path = mock(Path.class);
        File file = mock(File.class);
        FileOutputStream tempOutputStream = mock(FileOutputStream.class);
        FileSystemResource fileSystemResource = mock(FileSystemResource.class);
        PowerMockito.doReturn(path).when(TempFileHelper.class, "createTempFile", ".pdf");
        when(path.toFile()).thenReturn(file);
        whenNew(FileSystemResource.class).withArguments(file).thenReturn(fileSystemResource);
        whenNew(FileOutputStream.class).withArguments(file).thenReturn(tempOutputStream);
        PowerMockito.doReturn(0).when(IOUtils.class, "copy", inputStream, tempOutputStream);
        ArgumentCaptor<HttpEntity> argumentCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ResponseEntity<String> response = ResponseEntity.ok("{\"nodeId\":\"" + testNodeId.toString() + "\"," +
                                                            "\"mediaType\":\"application/pdf\"}");
        when(restTemplate.exchange(eq(UriComponentsBuilder.fromUriString("{url}/create")
                                                          .buildAndExpand(contentStorageUri)
                                                          .toUri()),
                                   eq(HttpMethod.POST),
                                   any(HttpEntity.class),
                                   eq(String.class)))
                .thenReturn(response);
        ArgumentCaptor<Attachment> captor = ArgumentCaptor.forClass(Attachment.class);
        //Act

        service.create(name, record, inputStream);
        //Assert
        verify(restTemplate).exchange(eq(UriComponentsBuilder.fromUriString("{url}/create")
                                                             .buildAndExpand(contentStorageUri)
                                                             .toUri()),
                                      eq(HttpMethod.POST),
                                      argumentCaptor.capture(),
                                      eq(String.class));
        HttpEntity<MultiValueMap<String, Object>> entity = argumentCaptor.getValue();
        assertThat(entity.getHeaders())
                .containsOnly(entry("Content-Type", Collections.singletonList(MULTIPART_FORM_DATA.toString())));
        assertThat(entity.getBody().getFirst("data")).isEqualTo(fileSystemResource);
        verify(attachmentRepository).save(captor.capture());
        CustomAssertion.assertThat(captor.getValue())
                       .lazyCheck(Attachment::getContainerId, testNodeId)
                       .lazyCheck(Attachment::getMimeType, "application/pdf")
                       .lazyCheck(Attachment::getName, name)
                       .lazyCheck(Attachment::getNewsRecord, record)
                       .check();

    }

    /**
     * Тест создания вложения без указания имени файла
     *
     * @throws Exception
     */
    @Test
    public void createWithEmptyFileName() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.create(" ", record, mock(InputStream.class)),
                              //Assert
                              WSArgumentException.class,
                              FILE_NAME_CANT_BE_EMPTY);
    }

    /**
     * Тест создания вложения без укащания
     *
     * @throws Exception
     */
    @Test
    public void createWithoutName() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.create(null, record, mock(InputStream.class)),
                              //Assert
                              WSArgumentException.class,
                              FILE_NAME_IS_MANDATORY);
    }


    /**
     * Тест создания вложения без новости
     *
     * @throws Exception
     */
    @Test
    public void createWithoutNewsRecord() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.create(name, null, mock(InputStream.class)),
                              //Assert
                              WSArgumentException.class,
                              NEWS_RECORD_IS_MANDATORY);
    }

    /**
     * Тест создания вложения без данных
     *
     * @throws Exception
     */
    @Test
    public void createWithoutFileData() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.create(name, record, null),
                              //Assert
                              WSArgumentException.class,
                              FILE_DATA_IS_MANDATORY);
    }

    /**
     * Тест скачивания файла
     *
     * @throws Exception
     */
    @Test
    public void download() throws Exception {
        //Arrange
        Attachment attachment = mock(Attachment.class);
        when(attachment.getContainerId()).thenReturn(testNodeId);
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
        OutputStream outputStream = mock(OutputStream.class);
        when(restTemplate.execute(eq(UriComponentsBuilder
                                             .fromUriString("{url}/{id}")
                                             .buildAndExpand(contentStorageUri, testNodeId)
                                             .toUri()),
                                  eq(HttpMethod.GET),
                                  eq(null),
                                  any(ResponseExtractor.class)))
                .thenReturn(Boolean.TRUE);
        //Act
        service.download(attachmentId, outputStream);
        //Assert
        verify(restTemplate).execute(eq(UriComponentsBuilder
                                                .fromUriString("{url}/{id}")
                                                .buildAndExpand(contentStorageUri, testNodeId)
                                                .toUri()),
                                     eq(HttpMethod.GET),
                                     eq(null),
                                     any(ResponseExtractor.class));
    }

    /**
     * Тест скачивания файла, без указания исходящего потока
     *
     * @throws Exception
     */
    @Test
    public void downloadFileWithoutOutputStream() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.download(attachmentId, null),
                              //Assert
                              WSArgumentException.class,
                              OUTPUT_FILE_IS_MANDATORY);
    }

    /**
     * Тест получения вложений по id новости
     *
     * @throws Exception
     */
    @Test
    public void getAll() throws Exception {
        //Arrange
        UUID recordId = UUID.randomUUID();
        //Act
        service.getAll(recordId);
        //Assert
        verify(attachmentRepository).findAllByNewsRecordId(recordId);
    }

    /**
     * Тест удаления вложения
     *
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        //Arrange
        Attachment attachment = mock(Attachment.class);
        when(attachmentRepository.findById(attachmentId))
                .thenReturn(Optional.of(attachment));
        when(attachment.getContainerId()).thenReturn(testNodeId);
        ResponseEntity result = ResponseEntity.ok().build();
        when(restTemplate.postForEntity(UriComponentsBuilder
                                                .fromUriString("{url}/{id}/delete")
                                                .buildAndExpand(contentStorageUri,
                                                                testNodeId)
                                                .toUri(),
                                        null,
                                        Object.class)).thenReturn(result);
        //Act
        service.delete(attachmentId);
        //Assert
        verify(attachmentRepository).delete(attachment);
        verify(restTemplate).postForEntity(UriComponentsBuilder
                                                   .fromUriString("{url}/{id}/delete")
                                                   .buildAndExpand(contentStorageUri,
                                                                   testNodeId)
                                                   .toUri(),
                                           null,
                                           Object.class);
    }

    /**
     * Тест
     *
     * @throws Exception
     */
    @Test
    public void getExisting() throws Exception {
        //Arrange
        //Act
        service.getExisting(attachmentId);
        //Assert
        verify(attachmentRepository).findById(attachmentId);
    }

    /**
     * Тест поведения при попытке получить запись без указания ID
     *
     * @throws Exception
     */
    @Test
    public void getExistingWithoutId() throws Exception {
        //Arrange
        //Act
        GuardCheck.guardCheck(() -> service.getExisting(null),
                              //Assert
                              WSArgumentException.class,
                              ID_IS_MANDATORY);
    }

    /**
     * Тест поведения при попытке получить несуществующую запись
     *
     * @throws Exception
     */
    @Test
    public void getExistingWhenItIsNot() throws Exception {
        //Arrange
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());
        //Act
        GuardCheck.guardCheck(() -> service.getExisting(attachmentId),
                              //Assert
                              WSNotFoundException.class,
                              ATTACHMENT_NOT_FOUND);
    }
}