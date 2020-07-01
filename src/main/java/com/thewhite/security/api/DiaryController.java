package com.thewhite.security.api;

import com.thewhite.security.api.dto.CreateDiaryDto;
import com.thewhite.security.api.dto.DiaryDto;
import com.thewhite.security.api.dto.UpdateDiaryDto;
import com.thewhite.security.mapper.DiaryMapper;
import com.thewhite.security.service.DiaryService;
import com.thewhite.security.service.argument.CreateDiaryArgument;
import com.thewhite.security.service.argument.UpdateDiaryArgument;
import com.whitesoft.api.dto.CollectionDTO;
import com.whitesoft.api.mappers.MapperUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService service;
    private final DiaryMapper mapper;

    @ApiOperation("Создание записи дневника")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public DiaryDto create(@RequestBody CreateDiaryDto createDiaryDto) {
        CreateDiaryArgument createArgument = CreateDiaryArgument.builder()
                                                                .writerId(createDiaryDto.getWriterId())
                                                                .title(createDiaryDto.getTitle())
                                                                .record(createDiaryDto.getRecord())
                                                                .recordDate(createDiaryDto.getRecordDate())
                                                                .build();
        return MapperUtils.getMapper(mapper::toDto)
                          .apply(service.create(createArgument));
    }

    @ApiOperation("Получение записи дневника по id")
    @GetMapping("/{id}")
    public DiaryDto get(@PathVariable UUID id) {
        return mapper.toDto(service.getExisting(id));
    }

    @ApiOperation("Получение пейдженированного списка записей дневника")
    @GetMapping("/list")
    public CollectionDTO<DiaryDto> getAll(@RequestParam(name = "pageNo") int pageNo,
                                          @RequestParam(name = "pageSize") int pageSize,
                                          @RequestParam String sortField,
                                          @RequestParam Sort.Direction sortDirection) {
        return MapperUtils.mapPage(mapper::toDto, service.getAll(PageRequest.of(pageNo, pageSize,
                                                                                Sort.by(sortDirection, sortField))));
    }

    @ApiOperation("Обновление записи дневника")
    @PostMapping("/{id}/update")
    public DiaryDto update(@PathVariable UUID id,
                           @RequestBody UpdateDiaryDto updateDiaryDto) {
        UpdateDiaryArgument updateArgument = UpdateDiaryArgument.builder()
                                                                .title(updateDiaryDto.getTitle())
                                                                .record(updateDiaryDto.getRecord())
                                                                .recordDate(updateDiaryDto.getRecordDate())
                                                                .build();
        return MapperUtils.getMapper(mapper::toDto)
                          .apply(service.update(id, updateArgument));
    }

    @ApiOperation("Обновление записи дневника")
    @PostMapping("/{id}/delete")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}