package com.thewhite.security.mapper;

import com.thewhite.security.api.dto.DiaryDto;
import com.thewhite.security.model.Diary;
import org.mapstruct.Mapper;

@Mapper
public interface DiaryMapper {
    DiaryDto toDto(Diary diary);
}
