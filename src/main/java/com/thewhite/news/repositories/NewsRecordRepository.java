package com.thewhite.news.repositories;

import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.QNewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.whitesoft.core.repositories.BaseRepository;
import com.whitesoft.core.utils.WhereClauseBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vdovin S. on 18.05.2018.
 * Репозиторий для работы с новостями
 *
 * @author Sergey Vdovin
 * @version 1.0
 */
public interface NewsRecordRepository extends BaseRepository<NewsRecord>, QueryDslPredicateExecutor<NewsRecord> {

    @Query("select distinct year(n.postDate) from NewsRecord n order by n.postDate")
    List<Integer> getYears();


    default Page<NewsRecord> searchNews(UUID userId,
                                        UUID excludedUserId,
                                        RecordStatus recordStatus,
                                        Date deadline,
                                        Integer year,
                                        Pageable pageable) {
        QNewsRecord newsRecord = QNewsRecord.newsRecord;
        return findAll(
                WhereClauseBuilder.getNew()
                                  .optionalAnd(userId, newsRecord.userId::eq)
                                  .optionalAnd(recordStatus, newsRecord.status::eq)
                                  .optionalAnd(year, newsRecord.postDate.year()::eq)
                                  .optionalAnd(excludedUserId, id -> newsRecord.users.contains(id).not())
                                  .optionalAnd(deadline, newsRecord.endDate::after)
                                  .build(),
                pageable);
    }
}
