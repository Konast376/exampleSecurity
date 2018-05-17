package com.thewhite.news.actions;

import com.thewhite.news.actions.arguments.CreateAttachmentActionArgument;
import com.thewhite.news.model.Attachment;
import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.util.actions.ActionArgument;
import com.whitesoft.util.actions.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Vdovin S. on 17.05.18.
 * Действие создания вложения
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Component("createAttachmentAction")
public class CreateAttachmentAction extends BaseAction<Attachment, CreateAttachmentActionArgument> {

    private final AttachmentService attachmentService;
    private final NewsRecordService newsRecordService;

    @Autowired
    public CreateAttachmentAction(AttachmentService attachmentService,
                                  NewsRecordService newsRecordService) {
        this.attachmentService = attachmentService;
        this.newsRecordService = newsRecordService;
    }

    @Override
    @Transactional
    public Attachment execute(ActionArgument argument) {
        return super.execute(argument);
    }

    @Override
    protected Attachment executeImpl(CreateAttachmentActionArgument createAttachmentActionArgument) {

        NewsRecord record = newsRecordService.getExisting(createAttachmentActionArgument.getNewsRecord());

        return attachmentService.create(createAttachmentActionArgument.getName(),
                                        record,
                                        createAttachmentActionArgument.getFileData());
    }
}
