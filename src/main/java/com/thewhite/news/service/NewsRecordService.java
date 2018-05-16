package com.thewhite.news.service;

import com.thewhite.news.model.NewsRecord;
import com.whitesoft.core.services.ReferableEntityService;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.UUID;

public interface NewsRecordService extends ReferableEntityService<NewsRecord> {
    NewsRecord create(
            String title,
            Date postDate,
            String content
    );

    Page<NewsRecord> getAll(int pageSize,int pageNo);

    NewsRecord update(UUID id,
                      String title,
                      Date postDate,
                      String content);


    void delete(UUID id);
}
