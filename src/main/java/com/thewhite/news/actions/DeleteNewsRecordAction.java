package com.thewhite.news.actions;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.util.actions.ActionArgument;
import com.whitesoft.util.actions.BaseVoidAction;
import com.whitesoft.util.actions.OneFieldActionArgument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * Действие удаления новости вместе со всеми вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Component("deleteNewsRecordAction")
public class DeleteNewsRecordAction extends BaseVoidAction<OneFieldActionArgument<UUID>> {

    private final AttachmentService attachmentService;
    private final NewsRecordService newsRecordService;

    @Autowired
    public DeleteNewsRecordAction(AttachmentService attachmentService,
                                  NewsRecordService newsRecordService) {
        this.attachmentService = attachmentService;
        this.newsRecordService = newsRecordService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Void execute(ActionArgument argument) {
        return super.execute(argument);
    }

    @Override
    protected void executeImpl(OneFieldActionArgument<UUID> argument) {
        attachmentService.getAll(argument.getField())
                         .forEach(item -> attachmentService.delete(item.getId()));
        newsRecordService.delete(argument.getField());
    }

}
