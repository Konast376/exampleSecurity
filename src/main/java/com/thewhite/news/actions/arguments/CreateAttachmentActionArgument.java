package com.thewhite.news.actions.arguments;

import com.whitesoft.util.actions.ActionArgument;
import lombok.*;

import java.io.InputStream;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by Vdovin S. on 17.05.18.
 * Аргумент действия создания вложения
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@AllArgsConstructor
@Builder
public class CreateAttachmentActionArgument implements ActionArgument {
    private final UUID newsRecordId;
    private final String name;
    private final InputStream fileData;

    @Override
    public boolean validate() {
        return newsRecordId != null &&
               isNotBlank(name) &&
               fileData != null;
    }
}
