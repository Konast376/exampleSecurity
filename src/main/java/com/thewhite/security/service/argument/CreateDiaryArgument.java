package com.thewhite.security.service.argument;

import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@Builder
public class CreateDiaryArgument {
    /** Идентификатор пользователя */
    UUID writerId;

    /** Заголовок записи дневника */
    String title;

    /** Запись дневника */
    String record;

    /** Дата последнего обновления */
    Date recordDate;
}