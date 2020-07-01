package com.thewhite.security.service.argument;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class UpdateDiaryArgument {
    /** Заголовок записи дневника */
    String title;

    /** Запись дневника */
    String record;

    /** Дата последнего обновления */
    Date recordDate;
}