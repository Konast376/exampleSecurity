package com.thewhite.news.errorinfo;

import com.whitesoft.util.errorinfo.ErrorInfo;

/**
 * Created by Vdovin S. on 18.05.18.
 * Ошибки при работе с вложениями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public enum AttachmentErrorInfo implements ErrorInfo {
    FILE_UPLOAD_FAILED("Ошибка при загрузке файла."),
    FILE_DOWNLOAD_FAILED("Ошибка при скачивании файла."),
    FILE_DELETE_FAILED("Ошибка удаления файла."),
    ATTACHMENT_NOT_FOUND("Вложение не найдено.");

    private final String message;

    AttachmentErrorInfo(String message) {
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return 3432 + ordinal();
    }
}
