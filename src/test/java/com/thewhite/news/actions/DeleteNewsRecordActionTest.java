package com.thewhite.news.actions;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.util.actions.OneFieldActionArgument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Vdovin S. on 21.05.18.
 * Тест действия удаления новости вместе с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public class DeleteNewsRecordActionTest {
    @Mock
    private AttachmentService attachmentService;
    @Mock
    private NewsRecordService newsRecordService;
    private DeleteNewsRecordAction action;
    private final UUID newsRecordId = UUID.randomUUID();
    private final UUID attachmentId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        action = new DeleteNewsRecordAction(attachmentService,
                                            newsRecordService);
    }

    /**
     * Тест удаления новости с вложениями
     *
     * @throws Exception
     */
    @Test
    public void execute() throws Exception {
        //Arrange
        Attachment attachment = mock(Attachment.class);
        when(attachmentService.getAll(newsRecordId)).thenReturn(Collections.singletonList(attachment));
        when(attachment.getId()).thenReturn(attachmentId);
        //Act
        action.execute(OneFieldActionArgument.of(newsRecordId));
        //Assert
        verify(attachmentService).delete(attachmentId);
        verify(newsRecordService).delete(newsRecordId);
    }
}