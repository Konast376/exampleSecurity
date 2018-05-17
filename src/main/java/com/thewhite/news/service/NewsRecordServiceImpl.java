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

@Service("newsRecordService")
public class NewsRecordServiceImpl implements NewsRecordService {

    private final NewsRecordRepository recordRepository;

    @Autowired
    public NewsRecordServiceImpl(NewsRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public NewsRecord create(String title,
                             Date postDate,
                             String content,
                             Date endDate,
                             UUID userId) {
        Guard.checkStringArgumentExists(title, TITLE_CANT_BE_EMPTY, TITLE_IS_MANDATORY);
        Guard.checkStringArgumentExists(content, CONTENT_CANT_BE_EMPTY, CONTENT_IS_MANDATORY);
        return recordRepository.save(NewsRecord.builder()
                                               .title(title)
                                               .postDate(postDate)
                                               .content(content)
                                               .endDate(endDate)
                                               .userId(userId)
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
        return recordRepository.findAll(
                new PageRequest(
                        pageNo,
                        pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getYears() {
        return recordRepository.getYears();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NewsRecord update(UUID id, String title, String content, Date endDate) {
        Guard.checkStringArgumentExists(title, TITLE_CANT_BE_EMPTY, TITLE_IS_MANDATORY);
        Guard.checkStringArgumentExists(content, CONTENT_CANT_BE_EMPTY, CONTENT_IS_MANDATORY);
        NewsRecord record = getExisting(id);
        record.setTitle(title);
        record.setContent(content);
        record.setEndDate(endDate);
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
    public NewsRecord mark(UUID id) {
        NewsRecord record = getExisting(id);
        record.getUsers().add(id);
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
