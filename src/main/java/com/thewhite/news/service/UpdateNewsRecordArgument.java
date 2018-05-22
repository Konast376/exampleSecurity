package com.thewhite.news.service;

import com.whitesoft.core.argument.Argument;
import com.whitesoft.util.Guard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

import static com.thewhite.news.errorinfo.NewsErrorInfo.*;

/**
 * Created by Vdovin S. on 22.05.18.
 * Аргумент для создания новости
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class UpdateNewsRecordArgument implements Argument {
    private final UUID id;
    private final String title;
    private final String content;
    private final Date endDate;

    @Override
    public void validate() {
        Guard.checkStringArgumentExists(title, TITLE_CANT_BE_EMPTY, TITLE_IS_MANDATORY);
        Guard.checkStringArgumentExists(content, CONTENT_CANT_BE_EMPTY, CONTENT_IS_MANDATORY);
    }
}
