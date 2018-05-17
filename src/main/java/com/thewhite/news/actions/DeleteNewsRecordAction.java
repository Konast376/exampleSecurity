package com.thewhite.news.actions;

import com.thewhite.news.model.Attachment;
import com.thewhite.news.service.AttachmentService;
import com.thewhite.news.service.NewsRecordService;
import com.whitesoft.util.actions.ActionArgument;
import com.whitesoft.util.actions.BaseVoidAction;
import com.whitesoft.util.actions.OneFieldActionArgument;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * <p>
 * TODO: replace on javadoc
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Component("deleteNewsRecordAction")
public class DeleteNewsRecordAction extends BaseVoidAction<OneFieldActionArgument<UUID>> {

    private final AttachmentService attachmentService;
    private final NewsRecordService newsRecordService;

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
        attachmentService.delete(argument.getField());
        newsRecordService.delete(argument.getField());
    }
}
