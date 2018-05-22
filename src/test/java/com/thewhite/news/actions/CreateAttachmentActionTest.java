package com.thewhite.news.actions;

import com.thewhite.news.actions.arguments.CreateAttachmentActionArgument;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.NewsRecordService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Vdovin S. on 21.05.18.
 * <p>
 * Тест действия создания вложения
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public class CreateAttachmentActionTest {

    @Mock
    private AttachmentService attachmentService;
    @Mock
    private NewsRecordService newsRecordService;
    private CreateAttachmentAction createAttachmentAction;
    private final UUID attachmentId = UUID.randomUUID();
    private final UUID newsRecordId = UUID.randomUUID();
    private final String name = "name";
    private final InputStream inputStream = mock(InputStream.class);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        createAttachmentAction = new CreateAttachmentAction(
                attachmentService,
                newsRecordService
        );
    }

    /**
     * Тест создания вложения
     *
     * @throws Exception
     */
    @Test
    public void execute() throws Exception {
        //Arrange
        NewsRecord newsRecord = mock(NewsRecord.class);
        when(newsRecordService.getExisting(newsRecordId)).thenReturn(newsRecord);
        //Act
        createAttachmentAction.execute(CreateAttachmentActionArgument
                                               .builder()
                                               .name(name)
                                               .newsRecordId(newsRecordId)
                                               .fileData(inputStream)
                                               .build());
        //Assert
        verify(newsRecordService).getExisting(newsRecordId);
        verify(attachmentService).create(name, newsRecord, inputStream);
    }
}