package com.thewhite.news.actions.arguments;

import com.whitesoft.util.actions.ActionArgument;
import lombok.*;

import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Vdovin S. on 17.05.18.
 * Аргумент действия создания вложения
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAttachmentActionArgument implements ActionArgument {
    private UUID newsRecord;
    private String name;
    private InputStream fileData;

    @Override
    public boolean validate() {
        return false;
    }
}
