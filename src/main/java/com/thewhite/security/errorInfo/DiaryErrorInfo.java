package com.thewhite.security.errorInfo;

import com.whitesoft.util.errorinfo.ErrorInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DiaryErrorInfo implements ErrorInfo {
    WRITER_ID_IS_MANDATORY("Идентификатор автора не передан"),
    TITLE_IS_MANDATORY("Заголовок не передан"),
    NOT_FOUND("Запись отсутствует");

    private final int code = ordinal() + 100;
    private final String message;
}