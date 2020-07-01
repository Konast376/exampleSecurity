package com.thewhite.security.service;

import com.thewhite.security.model.Diary;
import com.thewhite.security.service.argument.CreateDiaryArgument;
import com.thewhite.security.service.argument.UpdateDiaryArgument;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DiaryService {
    Diary create(@NonNull CreateDiaryArgument argument);

    Diary getExisting(@NonNull UUID id);

    Page<Diary> getAll(@NonNull Pageable pageable);

    Diary update(@NonNull UUID id, @NonNull UpdateDiaryArgument argument);

    void delete(@NonNull UUID id);
}