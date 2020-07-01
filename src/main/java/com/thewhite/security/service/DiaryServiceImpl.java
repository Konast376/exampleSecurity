package com.thewhite.security.service;

import com.thewhite.security.errorInfo.DiaryErrorInfo;
import com.thewhite.security.model.Diary;
import com.thewhite.security.repository.DiaryRepository;
import com.thewhite.security.service.argument.CreateDiaryArgument;
import com.thewhite.security.service.argument.UpdateDiaryArgument;
import com.whitesoft.util.Guard;
import com.whitesoft.util.exceptions.WSNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.apache.logging.log4j.util.Strings.trimToNull;
import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    private final DiaryRepository repository;

    @Transactional
    public Diary create(@NonNull CreateDiaryArgument argument) {
        Guard.checkArgumentExists(argument.getWriterId(), DiaryErrorInfo.WRITER_ID_IS_MANDATORY);
        Guard.checkArgumentExists(trimToNull(argument.getTitle()), DiaryErrorInfo.TITLE_IS_MANDATORY);

        return repository.save(Diary.builder()
                                    .writerId(argument.getWriterId())
                                    .title(argument.getTitle())
                                    .record(argument.getRecord())
                                    .recordDate(argument.getRecordDate())
                                    .build());
    }

    @Transactional(readOnly = true)
    public Diary getExisting(@NonNull UUID id) {
        return repository.findById(id).orElseThrow(WSNotFoundException.of(DiaryErrorInfo.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Diary> getAll(@NonNull Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(isolation = REPEATABLE_READ)
    public Diary update(@NonNull UUID id, @NonNull UpdateDiaryArgument argument) {
        Guard.checkArgumentExists(trimToNull(argument.getTitle()), DiaryErrorInfo.TITLE_IS_MANDATORY);

        Diary diary = getExisting(id);
        diary.setTitle(argument.getTitle());
        diary.setRecord(argument.getRecord());
        diary.setRecordDate(argument.getRecordDate());

        return repository.save(diary);
    }

    @Transactional(isolation = SERIALIZABLE)
    public void delete(@NonNull UUID id) {
        Guard.checkArgumentExists(id, DiaryErrorInfo.NOT_FOUND);
        Diary diary = getExisting(id);
        repository.delete(diary);
    }
}