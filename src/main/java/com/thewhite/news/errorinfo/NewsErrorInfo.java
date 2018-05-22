package com.thewhite.news.errorinfo;

import com.whitesoft.util.errorinfo.ErrorInfo;

public enum NewsErrorInfo implements ErrorInfo {
    RECORD_NOT_FOUND("Запись не найдена."),
    ID_IS_MANDATORY("ID - обязательный аргумент."),
    CONTENT_IS_MANDATORY("Содержание - обязательный аргумент."),
    CONTENT_CANT_BE_EMPTY("Содержание не может быть пустым."),
    TITLE_IS_MANDATORY("Заголовок - обязательный аргумент."),
    TITLE_CANT_BE_EMPTY("Заголовок не может быть пуст.");


    private final String message;

    NewsErrorInfo(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getCode() {
        return 9038 + ordinal();
    }
}
