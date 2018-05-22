package com.thewhite.news.service;

import com.thewhite.news.model.NewsRecord;
import com.thewhite.news.model.RecordStatus;
import com.thewhite.news.repositories.NewsRecordRepository;
import com.whitesoft.util.Guard;
import com.whitesoft.util.exceptions.WSNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.thewhite.news.errorinfo.NewsErrorInfo.*;

/**
 * Реализация сервиса для работы с новостями
 */
@Service("newsRecordService")
public class NewsRecordServiceImpl implements NewsRecordService {

    private final NewsRecordRepository recordRepository;

    @Autowired
    public NewsRecordServiceImpl(NewsRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public NewsRecord create(CreateNewsRecordArgument argument) {
        argument.validate();
        return recordRepository.save(NewsRecord.builder()
                                               .title(argument.getTitle())
                                               .postDate(argument.getPostDate())
                                               .content(argument.getContent())
                                               .endDate(argument.getEndDate())
                                               .userId(argument.getUserId())
                                               .status(RecordStatus.PROJECT)
                                               .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsRecord> getAll(UUID userId,
                                   RecordStatus recordStatus,
                                   Integer year,
                                   int pageSize,
                                   int pageNo) {
        return recordRepository.searchNews(userId,
                                           recordStatus,
                                           year,
                                           new PageRequest(pageNo,
                                                           pageSize));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<NewsRecord> getActual(UUID userId,
                                      Date deadline,
                                      int pageSize,
                                      int pageNo) {
        return recordRepository.searchActual(userId,
                                             deadline,
                                             new PageRequest(pageNo,
                                                             pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getYearsThatHasNews() {
        return recordRepository.getYears();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NewsRecord update(UpdateNewsRecordArgument argument) {
        argument.validate();
        NewsRecord record = getExisting(argument.getId());
        record.setTitle(argument.getTitle());
        record.setContent(argument.getContent());
        record.setEndDate(argument.getEndDate());
        return recordRepository.save(record);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NewsRecord publish(UUID id) {
        NewsRecord record = getExisting(id);
        record.setStatus(RecordStatus.PUBLISHED);
        return recordRepository.save(record);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NewsRecord mark(UUID id, UUID userId) {
        NewsRecord record = getExisting(id);
        record.getUsers().add(userId);
        return recordRepository.save(record);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(UUID id) {
        Guard.checkArgumentExists(id, ID_IS_MANDATORY);
        recordRepository.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsRecord getExisting(UUID id) {
        Guard.checkArgumentExists(id, ID_IS_MANDATORY);
        return recordRepository.findById(id)
                               .orElseThrow(WSNotFoundException.of(RECORD_NOT_FOUND));
    }


}
