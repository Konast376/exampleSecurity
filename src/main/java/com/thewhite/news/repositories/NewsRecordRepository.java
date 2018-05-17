package com.thewhite.news.repositories;

import com.thewhite.news.model.NewsRecord;
import com.whitesoft.core.repositories.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Year;
import java.util.List;

public interface NewsRecordRepository extends BaseRepository<NewsRecord> {

    @Query("select distinct year(n.postDate) from NewsRecord n order by n.postDate")
    List<Integer> getYears();
}
