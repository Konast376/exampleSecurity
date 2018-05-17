package com.thewhite.news.service;

import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.whitesoft.core.services.ReferableEntityService;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface NewsRecordService extends ReferableEntityService<NewsRecord> {
    /**
     * Создание записи
     *
     * @param title   тема новости
     * @param content содержание
     * @param endDate дата актуальности
     * @param userId  uuid пользователя
     * @return
     */
    NewsRecord create(
            String title,
            Date postDate,
            String content,
            Date endDate,
            UUID userId
    );

    Page<NewsRecord> getAll(UUID userId,
                            RecordStatus recordStatus,
                            Integer year,
                            int pageSize,
                            int pageNo);

    List<Integer> getYears();

    NewsRecord update(UUID id,
                      String title,
                      String content,
                      Date endDate);


    NewsRecord publish(UUID id);

    NewsRecord mark(UUID id);

    void delete(UUID id);
}
